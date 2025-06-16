package com.dao.momentum.announcement.command.application.service;

import com.dao.momentum.announcement.command.application.dto.request.AnnouncementCreateRequest;
import com.dao.momentum.announcement.command.application.dto.request.AnnouncementModifyRequest;
import com.dao.momentum.announcement.command.application.dto.response.AnnouncementCreateResponse;
import com.dao.momentum.announcement.command.application.dto.response.AnnouncementModifyResponse;
import com.dao.momentum.announcement.command.application.mapper.AnnouncementMapper;
import com.dao.momentum.announcement.command.domain.aggregate.Announcement;
import com.dao.momentum.announcement.command.domain.aggregate.File;
import com.dao.momentum.announcement.command.domain.repository.AnnouncementRepository;
import com.dao.momentum.announcement.command.domain.repository.FileRepository;
import com.dao.momentum.announcement.exception.FileUploadFailedException;
import com.dao.momentum.announcement.exception.NoSuchAnnouncementException;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.common.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementCommandService {

    private final AnnouncementMapper announcementMapper;
    private final AnnouncementRepository announcementRepository;
    private final S3Service s3Service;
    private final FileRepository fileRepository;

    @Transactional
    public AnnouncementCreateResponse create(AnnouncementCreateRequest announcementCreateRequest,
                                             List<MultipartFile> files,
                                             UserDetails userDetails) {
        Long empId = Long.valueOf(userDetails.getUsername());

        // 1. 공지사항 엔티티 생성 및 저장
        Announcement announcement = announcementMapper.toCreateEntity(announcementCreateRequest, empId);
        Announcement savedAnnouncement = announcementRepository.save(announcement);

        // 2. 첨부파일 S3 업로드 및 DB 저장
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                String originalFilename = file.getOriginalFilename();
                String sanitizedFilename = s3Service.sanitizeFilename(originalFilename);
                String key = "announcements/" + savedAnnouncement.getAnnouncementId() + "/" + UUID.randomUUID() + "/" + sanitizedFilename;

                try (InputStream inputStream = file.getInputStream()) {
                    String fileUrl = s3Service.uploadFile(key, inputStream, file.getContentType());
                    String fileExtension = s3Service.extractFileExtension(originalFilename);

                    File fileEntity = File.builder()
                            .announcementId(savedAnnouncement.getAnnouncementId())
                            .approveId(null)
                            .contractId(null)
                            .url(fileUrl)
                            .type(fileExtension)
                            .build();

                    fileRepository.save(fileEntity);
                } catch (IOException e) {
                    throw new FileUploadFailedException(ErrorCode.FILE_UPLOAD_FAIL);
                }
            }
        }

        log.info("공지사항 작성 - empId={}, title={}", empId, announcementCreateRequest.getTitle());

        return announcementMapper.toCreateResponse(savedAnnouncement);
    }

    @Transactional
    public AnnouncementModifyResponse modify(AnnouncementModifyRequest announcementModifyRequest,
                                             List<MultipartFile> files,
                                             Long announcementId,
                                             UserDetails userDetails) {
        Long empId = Long.valueOf(userDetails.getUsername());

        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new NoSuchAnnouncementException(ErrorCode.ANNOUNCEMENT_NOT_FOUND));

        announcement.validateAuthor(empId);

        announcement.modify(announcementModifyRequest.getTitle(), announcementModifyRequest.getContent());

        // TODO : CloudFront + S3 활용한 파일 업로드, 첨부파일 교체 로직(remainFileIdList 활용)

        log.info("공지사항 수정 - empId={}, title={}", empId, announcementModifyRequest.getTitle());

        return announcementMapper.toModifyResponse(announcement);
    }
}
