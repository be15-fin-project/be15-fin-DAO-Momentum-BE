package com.dao.momentum.announcement.command.application.dto.request;

public record FilePresignedUrlRequest(String fileName, long sizeInBytes, String contentType) {}
