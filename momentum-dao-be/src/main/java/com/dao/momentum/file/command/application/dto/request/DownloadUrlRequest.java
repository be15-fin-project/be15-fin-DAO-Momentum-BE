package com.dao.momentum.file.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DownloadUrlRequest {
    @NotBlank
    private String key;
    @NotBlank
    private String fileName;
}