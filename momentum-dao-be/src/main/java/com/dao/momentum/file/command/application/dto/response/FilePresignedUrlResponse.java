package com.dao.momentum.file.command.application.dto.response;

public record FilePresignedUrlResponse(String presignedUrl, String s3Key) {}
