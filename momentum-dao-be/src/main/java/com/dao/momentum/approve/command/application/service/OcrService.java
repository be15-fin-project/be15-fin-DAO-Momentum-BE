package com.dao.momentum.approve.command.application.service;

import com.dao.momentum.approve.command.application.dto.response.ReceiptOcrResultResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface OcrService {

    ReceiptOcrResultResponse extractReceiptData(MultipartFile multipartFile);

}
