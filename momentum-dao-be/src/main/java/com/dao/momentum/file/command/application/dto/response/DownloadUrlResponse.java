package com.dao.momentum.file.command.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DownloadUrlResponse {
    private String signedUrl;
}