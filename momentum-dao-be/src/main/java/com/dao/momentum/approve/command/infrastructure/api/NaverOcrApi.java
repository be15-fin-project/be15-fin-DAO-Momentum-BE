package com.dao.momentum.approve.command.infrastructure.api;

import com.dao.momentum.approve.exception.OcrRequestFailedException;
import com.dao.momentum.common.exception.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class NaverOcrApi {

    @Value("${naver.service.url}")
    private String url;

    @Value("${naver.service.secret-key}")
    private String naverSecretKey;

    /* ocr api와 통신 후 결과 값을 반환하는 메서드 */
    public String requestOcrApi(File file, String fileName, String format) {
        try {
            // HTTP 통신을 위해 외부로 데이터를 전송할 때 사용되는 클래스
            RestTemplate restTemplate = new RestTemplate();

            /* message 구성 정보
            * (1) version : 버전 정보
            * (2) requestId : API 호출 UUID
            * (3) timestamp : API 호출 timestamp
            * (4) images
            *     (4-1) format : 파일 형식
            *     (4-2) name : 파일 이름
            * */
            String jsonMessage = new ObjectMapper().writeValueAsString(Map.of(
                    "version", "V2",
                    "requestId", UUID.randomUUID().toString(),
                    "timestamp", System.currentTimeMillis(),
                    "images", List.of(Map.of("format", format, "name", fileName))
            ));

            // 헤더에 실어서 보내줘야 하는 정보 : content-type, secret-key
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("X-OCR-SECRET", naverSecretKey);


            // body에 실어서 보내줘야 하는 정보 : message, file
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("message", new HttpEntity<>(jsonMessage, createJsonHeader()));
            body.add("file", new FileSystemResource(file));

            // 요청 보내기
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 응답을 받아오는 객체
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            return response.getBody();
        } catch (OcrRequestFailedException | JsonProcessingException e) {
            throw new OcrRequestFailedException(ErrorCode.FAILED_OCR_CALL);
        }
    }

    /* message는 json 형식으로 들어가기 때문에 존재하는 메서드 */
    private HttpHeaders createJsonHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

}
