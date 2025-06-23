package com.dao.momentum.file.command.application.dto.request;

public record FilePresignedUrlRequest(String fileName, long sizeInBytes, String contentType) {}
