package com.dao.momentum.file.command.application.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDto {
    private String url;
    private String name;
}