package com.dao.momentum.common.file.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DownloadUrlResponse {
    private String signedUrl;
}