package com.dao.momentum.announcement.command.application.dto.response;

public record FilePresignedUrlResponse(String presignedUrl, String s3Key) {}
