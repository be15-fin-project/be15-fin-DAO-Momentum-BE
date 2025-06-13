package com.dao.momentum.announcement.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AnnouncementCreateRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
}
