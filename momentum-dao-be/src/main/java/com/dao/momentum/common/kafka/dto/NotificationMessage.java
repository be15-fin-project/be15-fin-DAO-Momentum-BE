package com.dao.momentum.common.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationMessage {
    private String title;
    private String content;
    private String type;
    private String url;
}
