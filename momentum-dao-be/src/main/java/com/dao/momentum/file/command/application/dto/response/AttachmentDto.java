package com.dao.momentum.file.command.application.dto.response;

import lombok.*;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "첨부파일 정보 DTO")
public class AttachmentDto {

    @Schema(description = "파일 ID", example = "100")
    private Long fileId;

    @Schema(description = "파일 접근 S3 Key", example = "announcement/1858ddb8-7d8f-4bcd-bda3-96b3aa661516/work.txt")
    private String url;

    @Schema(description = "파일 이름", example = "공지사항.pdf")
    private String name;
}
