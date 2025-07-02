package com.dao.momentum.file.command.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DownloadUrlResponse {
    private String signedUrl;
    private String fileName;
}