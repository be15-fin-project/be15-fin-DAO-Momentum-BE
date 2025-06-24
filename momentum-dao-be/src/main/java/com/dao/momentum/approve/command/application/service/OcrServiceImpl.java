package com.dao.momentum.approve.command.application.service;

import com.dao.momentum.approve.command.application.dto.response.ReceiptOcrResultResponse;
import com.dao.momentum.approve.command.infrastructure.api.NaverOcrApi;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OcrServiceImpl implements OcrService{

    private final NaverOcrApi naverOcrApi;

    /* 영수증 처리 결과를 받는 메서드 */
    @Transactional(readOnly = true)
    public ReceiptOcrResultResponse extractReceiptData(MultipartFile multipartFile) {
        try {
            // 파일 이름 얻기
            String originalFilename = multipartFile.getOriginalFilename();

            // 확장자 얻기
            String ext = getExtension(originalFilename);

            // 파일로 변환하기
            File file = convertToFile(multipartFile, ext);

            log.info("영수증 분석 요청");
            log.info("파일명: {}, 확장자: {}", originalFilename, ext);

            String result =  naverOcrApi.requestOcrApi(file, originalFilename, ext);

            log.info("OCR 원본 응답: {}", result);

            return parseToReceiptOcrResult(result);

        } catch (IOException e) {
            log.info("파일 처리 중 오류 발생", e);
            throw new RuntimeException("파일 처리 실패", e);
        }
    }

    /* 파일을 변환 해주는 메서드 */
    private File convertToFile(MultipartFile multipartFile, String ext) throws IOException {
        File convFile = File.createTempFile("ocr-", "." + ext);
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(multipartFile.getBytes());
        }
        return convFile;
    }

    /* 파일의 확장자를 얻기 위한 메서드 */
    private String getExtension(String filename) {
        // 파일 확장자가 없는 경우 예외 처리
        if (filename == null || !filename.contains(".")) {
            throw new IllegalArgumentException("파일 확장자가 없습니다.");
        }

        // 파일의 확장자는 마지막에 '.'뒤에 붙어 있기 때문에 설정
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    /* 원하는 결과값만 없기 위한 메서드 : 매장명, 사용일시, 금액 */
    public ReceiptOcrResultResponse parseToReceiptOcrResult(String json) {
        try {
            // 문자를 json 형태로 받기
            ObjectMapper objectMapper = new ObjectMapper();
            // 트리 형태로 파싱 한 다음에 사용하려고 함
            JsonNode root = objectMapper.readTree(json);
            JsonNode receipt = root.path("images").get(0).path("receipt").path("result");

            // 가게명
            String name = receipt.path("storeInfo").path("name").path("text").asText();
            String subName = receipt.path("storeInfo").path("subName").path("text").asText();
            String storeName = name + " " + subName;

            // 가게 주소
            String address = extractAddresses(receipt);

            // 날짜 : 날짜 정보 포맷 변경하기
            String dateText = receipt.path("paymentInfo").path("date").path("text").asText();
            LocalDate usedAt = parseDate(dateText);

            // 총 금액
            String strAmount = receipt.path("totalPrice").path("price").path("text").asText();
            strAmount = strAmount.replace(",", "");
            Integer amount = Integer.parseInt(strAmount);

            log.info("영수증 분석 결과");
            log.info("가게명: {}, 가게 주소: {}, 날짜: {}, 금액: {}", storeName, address, usedAt, amount);

            return ReceiptOcrResultResponse.builder()
                    .storeName(storeName)
                    .amount(amount)
                    .usedAt(usedAt)
                    .address(address)
                    .build();

        } catch (Exception e) {
            log.warn("OCR 처리 실패: {}", e.getMessage());

            return ReceiptOcrResultResponse.builder()
                    .storeName(null)
                    .amount(null)
                    .usedAt(null)
                    .address(null)
                    .build();
        }
    }

    /* 날짜 정보를 추출하는 메서드 */
    private LocalDate parseDate(String rawDate) {
        // 영수증 포맷이 여러 개 있기 때문에 이렇게 넣음
        List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.ofPattern("yyyyMMdd"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("yyyy.MM.dd")
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(rawDate, formatter);
            } catch (Exception e) {
                // 실패하는 경우에는 그냥 무시 (사용자가 입력하게 작성할 예정)
            }
        }

        return null;
    }

    /* 주소 정보를 추출하는 메서드 */
    private String extractAddresses(JsonNode receipt) {
        JsonNode addressArray = receipt.path("storeInfo").path("addresses");
        if (!addressArray.isArray()) return "";

        List<String> addressTexts = new ArrayList<>();
        for (JsonNode address : addressArray) {
            String text = address.path("text").asText();
            if (!text.isBlank()) addressTexts.add(text);
        }

        return String.join(", ", addressTexts);
    }

}
