package com.fourspoons.mikkureomi.rastaurant.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourspoons.mikkureomi.rastaurant.domain.Restaurant;
import com.fourspoons.mikkureomi.rastaurant.repository.RestaurantRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 애플리케이션 시작 시 엑셀 데이터를 읽어와 DB에 저장하는 서비스입니다.
 * (데이터가 이미 존재하면 스킵합니다)
 */
@Profile("local") // "local" 프로필에서만 컴포넌트 활성화
@Component
@RequiredArgsConstructor
public class SeoulPartnerRestaurantUploadService {
    private final RestaurantRepository restaurantRepository; // Spring Data JPA Repository
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${kakao.api.key}")
    private String KAKAO_API_KEY_RAW;
    private static final String KAKAO_API_URL = "https://dapi.kakao.com/v2/local/search/address.json";

    @PostConstruct
    public void run() throws Exception {
            if (restaurantRepository.count() > 0) {
                System.out.println("데이터가 이미 존재하므로 업로드를 중지합니다.");
                return;
            }

            ClassPathResource resource = new ClassPathResource("data/seoul_partner_restaurants.xlsx");
            InputStream inputStream = resource.getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            List<Restaurant> restaurantsToSave = new ArrayList<>();

            int totalRows = sheet.getLastRowNum();
            System.out.println("### 엑셀 파일 읽기 시작 - 총 " + totalRows + "개의 Row 감지 ###");

            for (int i = 1; i <= totalRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    System.out.println("WARN: " + i + "번째 줄이 비어있어 스킵합니다.");
                    continue;
                }

                String name = (row.getCell(0) != null) ? row.getCell(0).getStringCellValue() : null;
                String phone = (row.getCell(1) != null) ? row.getCell(1).getStringCellValue() : null;
                String zipCode = (row.getCell(2) != null) ? row.getCell(2).getStringCellValue() : null;
                String originalAddress = (row.getCell(3) != null) ? row.getCell(3).getStringCellValue() : null; // 원본 주소

                if (originalAddress == null || originalAddress.trim().isEmpty()) {
                    System.out.println("WARN: " + i + "번째 줄의 주소가 비어있어 스킵합니다.");
                    continue;
                }

                if (name == null || name.trim().isEmpty()) {
                    System.out.println("WARN: " + i + "번째 줄의 가맹점명이 비어있어 스킵합니다.");
                    continue;
                }

                // 양 끝의 공백을 제거
                name = name.trim();
                originalAddress = originalAddress.trim();

                // 주소 전처리 로직
                String cleanAddress = cleanAddress(originalAddress);

                if (cleanAddress.isEmpty()) {
                    System.out.println("WARN: " + i + "번째 줄의 주소가 전처리 후 비어있어 스킵합니다. [원본] " + originalAddress);
                    continue;
                }

                // API 호출 시 깨끗한 주소(cleanAddress) 사용
                double[] coords = getCoordinates(cleanAddress);
                Double latitude = (coords != null) ? coords[0] : null;
                Double longitude = (coords != null) ? coords[1] : null;

                if (coords == null) {
                    // (디버깅) API 호출이 실패(null)한 경우에만 로그를 남김
                    System.out.println("API 변환 실패: [원본] " + originalAddress + " -> [전처리] " + cleanAddress);
                }

                restaurantsToSave.add(
                        Restaurant.builder()
                                .name(name)
                                .phone(phone)
                                .zipCode(zipCode)
                                .address(originalAddress) // DB에는 원본 주소 저장
                                .latitude(latitude)
                                .longitude(longitude)
                                .build()
                );

                // Kakao API의 Rate Limit (초당 30회)을 준수하기 위해 약간의 딜레이를 줍니다.
                // 40ms = 0.04초 -> 초당 25회 요청
                Thread.sleep(40);
            }

            // 리스트에 모인 데이터를 DB에 일괄 저장
            if (!restaurantsToSave.isEmpty()) {
                System.out.println("### " + restaurantsToSave.size() + "개의 데이터 DB 저장 시도... ###");
                restaurantRepository.saveAll(restaurantsToSave);
                System.out.println("### " + restaurantsToSave.size() + "개의 가맹점 데이터 저장 성공! ###");
            } else {
                System.out.println("### 저장할 데이터가 없습니다. (모든 Row가 스킵됨) ###");
            }

            workbook.close();
            inputStream.close();
    }

    /**
     * Kakao '주소 검색' API를 호출하여 위도, 경도를 받는 메서드
     *
     * @param address 'query' 파라미터로 사용할 주소 문자열
     * @return double 배열 {위도(y), 경도(x)}
     */
    private double[] getCoordinates(String address) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", KAKAO_API_KEY_RAW);

        URI uri = UriComponentsBuilder.fromHttpUrl(KAKAO_API_URL)
                .queryParam("query", address)
                // 한글 주소의 안전한 전송을 위해 UTF-8 인코딩을 명시합니다.
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        try {
            HttpEntity<String> entity = new HttpEntity<>(headers);
            String responseBody = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class).getBody();

            // JSON 파싱
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode documents = root.path("documents");

            if (documents.size() > 0) {
                JsonNode firstDoc = documents.get(0);
                JsonNode roadAddress = firstDoc.path("road_address");
                JsonNode lotAddress = firstDoc.path("address"); // 지번 주소

                double latitude;
                double longitude;

                // 도로명 주소 좌표(road_address) 우선, 없으면 지번 주소(address) 좌표 사용
                if (!roadAddress.isMissingNode() && roadAddress.has("y") && roadAddress.has("x")) {
                    latitude = roadAddress.path("y").asDouble(); // 위도
                    longitude = roadAddress.path("x").asDouble(); // 경도
                } else if (!lotAddress.isMissingNode() && lotAddress.has("y") && lotAddress.has("x")) {
                    latitude = lotAddress.path("y").asDouble(); // 위도
                    longitude = lotAddress.path("x").asDouble(); // 경도
                } else {
                    System.err.println("좌표 파싱 실패 (x, y 없음): " + address + " | 응답: " + firstDoc.toString());
                    return null;
                }

                return new double[]{latitude, longitude};

            } else {
                // API가 200 OK를 반환했으나, 검색 결과(documents)가 없는 경우
                System.err.println("API 검색 결과 없음: " + address);
            }
        } catch (Exception e) {
            // RestTemplate 호출 실패 또는 JSON 파싱 실패
            System.err.println("주소 변환 API 호출 실패: " + address + " | 오류: " + e.getMessage());
        }
        return null;
    }

    /**
     * 원본 주소 문자열을 정제합니다.
     * @param originalAddress 원본 주소 문자열
     * @return 정제된 주소 문자열
     */
    private static String cleanAddress(String originalAddress) {
        if (originalAddress == null || originalAddress.trim().isEmpty()) return "";

        String addr = originalAddress.trim();

        // 1. 괄호 () 및 [] 안의 내용 제거 (예: (신사동), [본점])
        addr = addr.replaceAll("\\([^)]+\\)", "").replaceAll("\\[[^\\]]+\\]", "").trim();

        // 2. 쉼표(,)가 있다면 쉼표 앞부분만 사용 (예: "서울시 강남구 ..., 101호" -> "서울시 강남구 ...")
        if (addr.contains(",")) {
            addr = addr.split(",")[0].trim();
        }

        // --- 규칙 순서 변경 ---

        // 3. (기존 4번) '지하', '지상', '제'가 나오면 그 단어와 뒷부분 모두 제거 (앞부분만 살림)
        // (예: "... 10지상2층" -> "... 10")
        addr = addr.replaceAll("\\s*(지하|지상|제|상가|~).*", "").trim();

        // 4. '층'과 그 앞의 '한 자리 숫자'가 나오면, 그 부분부터 *뒷부분 전체*를 제거
        // (예: "... 31-33층 4층" -> "... 31-3")
        addr = addr.replaceAll("\\s*\\d{1}\\s*층.*", "").trim();


        // 5. (기존 3번) '로' 또는 '길' 주소 뒷부분(상세주소) 제거
        // '층'이나 '지상' 등이 먼저 제거되었기 때문에 건물번호까지만 깔끔하게 남김
//        addr = addr.replaceAll("(?i)(.*?(?:로|길)\\s*\\d{1,5}[-~–]?\\d{0,5}).*", "$1");

        // 6. 기타 불필요한 단어 제거 (호, 번지, 빌딩 등)
        addr = addr.replaceAll("(호|번지|빌딩|상가|건물|관|호점|호동|호관|필지|점포호|일부|B|F|b|f)\\b", "");

        // 7. 여러 개의 공백을 하나로 축소 및 최종 공백 제거
        addr = addr.replaceAll("\\s+", " ").trim();

        return addr;
    }
}

