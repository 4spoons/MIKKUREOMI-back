package com.fourspoons.mikkureomi.rastaurant.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourspoons.mikkureomi.rastaurant.domain.Restaurant;
import com.fourspoons.mikkureomi.rastaurant.domain.RestaurantType;
import com.fourspoons.mikkureomi.rastaurant.repository.RestaurantRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 애플리케이션 시작 시 CSV 데이터를 읽어와 DB에 저장하는 서비스입니다.
 * (데이터가 10만 개 미만일 때만 수행합니다. 착한가격 업소는 'GOOD_STORE' 타입으로 저장됩니다.)
 */
@Slf4j // 로깅 라이브러리 추가
//@Profile("local") // "local" 프로필에서만 컴포넌트 활성화
@Component
@RequiredArgsConstructor
public class SeoulGoodRestaurantUploadService {
    private final RestaurantRepository restaurantRepository; // Spring Data JPA Repository
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${kakao.api.key}")
    private String KAKAO_API_KEY_RAW;
    private static final String KAKAO_API_URL = "https://dapi.kakao.com/v2/local/search/address.json";

    // 착한 가격 업소 데이터는 10만 개 미만이므로, 기존 데이터가 너무 많지 않을 때만 초기 로딩을 수행합니다.
    private static final long MAX_RESTAURANT_COUNT = 100000;
//    private static final String FILE_NAME = "data/seoul_good_store.csv";
    private static final String FILE_NAME = "seoul_good_store.csv";


    @PostConstruct
    public void run() throws Exception {
        // 기존 데이터가 10만 개 이상이면 초기 업로드 로직을 스킵합니다.
        if (restaurantRepository.count() >= MAX_RESTAURANT_COUNT) {
            log.info("데이터가 {}개 이상 존재하므로 착한 가격 업소 업로드를 중지합니다.", MAX_RESTAURANT_COUNT);
            return;
        }

        log.info("### 착한 가격 업소 CSV 파일 읽기 시작 ###");
        ClassPathResource resource = new ClassPathResource(FILE_NAME);

        // CSV 파일은 보통 텍스트 파일이므로 BufferedReader를 사용합니다.
        try (InputStream inputStream = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("EUC-KR")))) {
            String headerLine = reader.readLine(); // 헤더 라인 (첫 줄) 스킵
            if (headerLine == null) {
                log.warn("WARN: CSV 파일에 데이터가 없습니다.");
                return;
            }

            List<Restaurant> restaurantsToSave = new ArrayList<>();
            String line;
            int rowCount = 0;
            int skipCount = 0;

            while ((line = reader.readLine()) != null) {
                rowCount++;

                String[] columns = splitCsvLine(line); // 더 안정적인 CSV 파싱 메서드 사용

                // 엑셀 데이터의 열 순서:
                // 0: 업소아이디 | 1: 업소명 | 2: 분류코드 | 3: 분류코드명 | 4: 업소 주소 | 5: 업소 전화번호 | 6: 찾아오시는 길 ...

                if (columns.length < 6) {
                    log.warn("WARN: {}번째 줄 데이터 컬럼 부족으로 스킵합니다. 라인: {}", rowCount, line);
                    skipCount++;
                    continue;
                }

                String name = columns[1].trim();
                String originalAddress = columns[4].trim(); // 원본 주소
                String phone = columns[5].trim(); // 전화번호

                if (originalAddress.isEmpty()) {
                    log.warn("WARN: {}번째 줄의 주소가 비어있어 스킵합니다. 라인: {}", rowCount, line);
                    skipCount++;
                    continue;
                }

                if (name.isEmpty()) {
                    log.warn("WARN: {}번째 줄의 가맹점명이 비어있어 스킵합니다. 라인: {}", rowCount, line);
                    skipCount++;
                    continue;
                }

                // 주소 전처리 로직 (기존 cleanAddress 재활용)
                String cleanAddress = cleanAddress(originalAddress);

                if (cleanAddress.isEmpty()) {
                    log.warn("WARN: {}번째 줄의 주소가 전처리 후 비어있어 스킵합니다. [원본] {}", rowCount, originalAddress);
                    skipCount++;
                    continue;
                }

                // API 호출 시 깨끗한 주소(cleanAddress) 사용
                double[] coords = getCoordinates(cleanAddress);
                Double latitude = (coords != null) ? coords[0] : null;
                Double longitude = (coords != null) ? coords[1] : null;

                if (coords == null) {
                    // API 호출이 실패(null)한 경우 로그
                    log.warn("API 변환 실패: [원본] {} -> [전처리] {}", originalAddress, cleanAddress);
                }

                restaurantsToSave.add(
                        Restaurant.builder()
                                .name(name)
                                .phone(phone.isEmpty() ? null : phone) // 전화번호가 비어있으면 null 저장
                                .zipCode(null) // CSV에 우편번호(zipCode) 컬럼은 없으므로 null 저장
                                .address(originalAddress) // DB에는 원본 주소 저장
                                .latitude(latitude)
                                .longitude(longitude)
                                .restaurantType(RestaurantType.GOOD_STORE) // 착한 가격 업소로 타입 지정!
                                .build()
                );

                // Kakao API의 Rate Limit (초당 30회) 준수 (40ms = 초당 25회 요청)
                Thread.sleep(40);
            }

            // 리스트에 모인 데이터를 DB에 일괄 저장
            if (!restaurantsToSave.isEmpty()) {
                log.info("### {}개의 착한 가격 업소 데이터 DB 저장 시도... ###", restaurantsToSave.size());
                restaurantRepository.saveAll(restaurantsToSave);
                log.info("### {}개의 착한 가격 업소 데이터 저장 성공! ###", restaurantsToSave.size());
            } else {
                log.warn("### 저장할 데이터가 없습니다. (총 {}개 Row 중 {}개 스킵됨) ###", rowCount, skipCount);
            }

        } catch (Exception e) {
            log.error("착한 가격 업소 데이터 업로드 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * CSV 라인을 파싱합니다. 큰 따옴표 안에 쉼표가 있을 경우를 대비해 정규 표현식을 사용합니다.
     * 제공된 데이터는 큰 따옴표가 없는 것으로 보이므로, 여기서는 임시로 '큰 따옴표'와 '쉼표'를 모두 고려하는
     * 범용적인 CSV 파서를 사용합니다.
     * 실제 데이터에 따라 (쉼표 또는 탭) 구분자를 변경해야 할 수 있습니다.
     */
    private String[] splitCsvLine(String line) {
        // 정규식: 쉼표 또는 줄의 시작/끝을 구분자로 사용하여 큰따옴표로 묶인 문자열 또는 묶이지 않은 문자열을 찾습니다.
        // 이 정규식은 일반적으로 잘 작동하는 CSV 파싱 방식 중 하나입니다.
        List<String> columns = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\"([^\"]*)\"|[^,]*),?");
        Matcher matcher = pattern.matcher(line);

        while (matcher.find()) {
            String match = matcher.group();
            // 마지막 쉼표 제거
            if (match.endsWith(",")) {
                match = match.substring(0, match.length() - 1);
            }
            // 큰따옴표 제거
            if (match.startsWith("\"") && match.endsWith("\"")) {
                match = match.substring(1, match.length() - 1);
            }
            columns.add(match);
        }

        // 마지막 컬럼이 빈 문자열인데, 줄 끝에 쉼표가 없어서 위의 반복문에서 빠지는 경우를 대비 (데이터 구조에 따라 다름)
        if (!line.endsWith(",")) {
            // 마지막 요소가 파싱되었는지 확인하고, 안 되었다면 빈 문자열 추가 (일반적인 CSV 파서의 동작을 단순화)
        }

        return columns.toArray(new String[0]);
    }

    // --- 기존 코드에서 재활용하는 메서드 ---

    /**
     * Kakao '주소 검색' API를 호출하여 위도, 경도를 받는 메서드
     **/
    private double[] getCoordinates(String address) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", KAKAO_API_KEY_RAW);

        URI uri = UriComponentsBuilder.fromHttpUrl(KAKAO_API_URL)
                .queryParam("query", address)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        try {
            HttpEntity<String> entity = new HttpEntity<>(headers);
            String responseBody = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class).getBody();

            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode documents = root.path("documents");

            if (documents.size() > 0) {
                JsonNode firstDoc = documents.get(0);
                JsonNode roadAddress = firstDoc.path("road_address");
                JsonNode lotAddress = firstDoc.path("address");

                double latitude;
                double longitude;

                if (!roadAddress.isMissingNode() && roadAddress.has("y") && roadAddress.has("x")) {
                    latitude = roadAddress.path("y").asDouble();
                    longitude = roadAddress.path("x").asDouble();
                } else if (!lotAddress.isMissingNode() && lotAddress.has("y") && lotAddress.has("x")) {
                    latitude = lotAddress.path("y").asDouble();
                    longitude = lotAddress.path("x").asDouble();
                } else {
                    log.error("좌표 파싱 실패 (x, y 없음): {} | 응답: {}", address, firstDoc.toString());
                    return null;
                }

                return new double[]{latitude, longitude};

            } else {
                log.warn("API 검색 결과 없음: {}", address);
            }
        } catch (Exception e) {
            log.error("주소 변환 API 호출 실패: {} | 오류: {}", address, e.getMessage());
        }
        return null;
    }

    /**
     * 원본 주소 문자열을 정제합니다. (가맹점에서의 전처리 함수 그대로 재활용)
     **/
    private static String cleanAddress(String originalAddress) {
        if (originalAddress == null || originalAddress.trim().isEmpty()) return "";

        String addr = originalAddress.trim();

        // 1. 괄호 () 및 [] 안의 내용 제거 (예: (신사동), [본점])
        addr = addr.replaceAll("\\([^)]+\\)", "").replaceAll("\\[[^\\]]+\\]", "").trim();

        // 2. 쉼표(,)가 있다면 쉼표 앞부분만 사용 (예: "서울시 강남구 ..., 101호" -> "서울시 강남구 ...")
        if (addr.contains(",")) {
            addr = addr.split(",")[0].trim();
        }

        // 3. '지하', '지상', '제', '상가', '~'가 나오면 그 단어와 뒷부분 모두 제거 (앞부분만 살림)
        addr = addr.replaceAll("\\s*(지하|지상|제|상가|~).*", "").trim();

        // 4. '층'과 그 앞의 '한 자리 숫자'가 나오면, 그 부분부터 *뒷부분 전체*를 제거
        addr = addr.replaceAll("\\s*\\d{1}\\s*층.*", "").trim();


        // 5. 기타 불필요한 단어 제거 (호, 번지, 빌딩 등)
        addr = addr.replaceAll("(호|번지|빌딩|상가|건물|관|호점|호동|호관|필지|점포호|일부|B|F|b|f)\\b", "");

        // 6. 여러 개의 공백을 하나로 축소 및 최종 공백 제거
        addr = addr.replaceAll("\\s+", " ").trim();

        return addr;
    }
}