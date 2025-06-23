package com.dao.momentum.announcement.command.application.service;

import com.dao.momentum.announcement.command.application.dto.request.AnnouncementCreateRequest;
import com.dao.momentum.announcement.command.application.dto.request.AnnouncementModifyRequest;
import com.dao.momentum.announcement.command.application.dto.request.AttachmentRequest;
import com.dao.momentum.announcement.command.application.dto.request.FilePresignedUrlRequest;
import com.dao.momentum.announcement.command.application.dto.response.AnnouncementCreateResponse;
import com.dao.momentum.announcement.command.application.dto.response.AnnouncementModifyResponse;
import com.dao.momentum.announcement.command.application.dto.response.FilePresignedUrlResponse;
import com.dao.momentum.announcement.command.application.mapper.AnnouncementMapper;
import com.dao.momentum.announcement.command.domain.aggregate.Announcement;
import com.dao.momentum.announcement.command.domain.repository.AnnouncementRepository;
import com.dao.momentum.announcement.exception.FileUploadFailedException;
import com.dao.momentum.announcement.exception.NoSuchAnnouncementException;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.common.service.S3Service;
import com.dao.momentum.file.command.domain.aggregate.File;
import com.dao.momentum.file.command.domain.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public AnnouncementCreateResponse create(AnnouncementCreateRequest request,
                                             UserDetails userDetails) {
        Long empId = Long.valueOf(userDetails.getUsername());

        // 1. 공지사항 엔티티 생성 및 저장
        Announcement announcement = announcementMapper.toCreateEntity(request, empId);
        Announcement savedAnnouncement = announcementRepository.save(announcement);

        // 2. 첨부파일 S3 업로드 및 DB 저장
        List<AttachmentRequest> attachments = request.getAttachments();
        if (attachments != null && !attachments.isEmpty()) {
            for (AttachmentRequest attachment : attachments) {
                File file = File.builder()
                        .announcementId(savedAnnouncement.getAnnouncementId())
                        .approveId(null)
                        .contractId(null)
                        .url(attachment.getS3Key()) // 이미 S3 업로드된 URL
                        .type(attachment.getType())
                        .build();
                fileRepository.save(file);
            }
        }


        log.info("공지사항 작성 - empId={}, announcementId={}, title={}", empId, savedAnnouncement.getAnnouncementId(), savedAnnouncement.getTitle());

        return announcementMapper.toCreateResponse(savedAnnouncement);
    }

    @Transactional
    public AnnouncementModifyResponse modify(AnnouncementModifyRequest request,
                                             Long announcementId,
                                             UserDetails userDetails) {
        Long empId = Long.valueOf(userDetails.getUsername());

        // 공지사항 존재 여부 확인
        Announcement announcement = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new NoSuchAnnouncementException(ErrorCode.ANNOUNCEMENT_NOT_FOUND));

        // 작성자 검증
        announcement.validateAuthor(empId);

        // 제목/내용 수정
        announcement.modify(request.getTitle(), request.getContent());

        // 파일 유지 리스트
        List<Long> remainFileIdList = request.getRemainFileIdList();
        // 기존에 공지사항에 첨부되어 있던 파일 리스트
        List<File> existingFiles = fileRepository.findAllByAnnouncementId(announcementId);

        // 삭제 대상: 유지 리스트에 포함되지 않은 기존 파일
        List<File> filesToDelete = existingFiles.stream()
                .filter(f -> remainFileIdList == null || !remainFileIdList.contains(f.getAttachmentId()))
                .toList();

        // 삭제 처리: S3 + DB
        filesToDelete.forEach(file -> {
            s3Service.deleteFileFromS3(file.getUrl()); // file.url에서 key 추출하여 삭제
            fileRepository.deleteById(file.getAttachmentId());
        });

        // 새로 추가된 파일 업로드 및 저장
        List<AttachmentRequest> newAttachments = request.getAttachments();
        if (newAttachments != null && !newAttachments.isEmpty()) {
            for (AttachmentRequest attachment : newAttachments) {
                try {
                    File fileEntity = File.builder()
                            .announcementId(announcementId)
                            .approveId(null)
                            .contractId(null)
                            .url(attachment.getS3Key())  // 현재는 URL 필드를 s3Key로 사용 중
                            .type(attachment.getType())
                            .build();

                    fileRepository.save(fileEntity);
                } catch (Exception e) {
                    log.error("첨부파일 저장 중 예외 발생 - s3Key={}, type={}, error={}",
                            attachment.getS3Key(), attachment.getType(), e.getMessage(), e);
                    throw new FileUploadFailedException(ErrorCode.FILE_UPLOAD_FAIL);
                }
            }
        }

        log.info("공지사항 수정 - empId={}, announcementId={}, title={}", empId, announcement.getAnnouncementId(), request.getTitle());

        return announcementMapper.toModifyResponse(announcement);
    }

    @Transactional
    public void delete(Long announcementId, UserDetails userDetails) {
        Long empId = Long.valueOf(userDetails.getUsername());

        // 공지사항 존재 여부 확인
        Announcement announcement = announcementRepository.findById(announcementId)
                        .orElseThrow(() -> new NoSuchAnnouncementException(ErrorCode.ANNOUNCEMENT_NOT_FOUND));

        // 작성자 검증
        announcement.validateAuthor(empId);

        // 공지사항에 첨부된 파일들 hard delete
        List<File> files = fileRepository.findAllByAnnouncementId(announcementId);
        files.forEach(file -> {
            s3Service.deleteFileFromS3(file.getUrl());
            fileRepository.deleteById(file.getAttachmentId());
        });

        log.info("공지사항 삭제 - empId={}, announcementId={}, title={}", empId, announcement.getAnnouncementId(), announcement.getTitle());

        announcementRepository.delete(announcement);
    }

    public FilePresignedUrlResponse generatePresignedUrl(FilePresignedUrlRequest request) {
        final long MAX_SIZE = 10 * 1024 * 1024; // 10MB 제한
        if (request.sizeInBytes() > MAX_SIZE) {
            throw new IllegalArgumentException("파일은 10MB 이하만 업로드 가능합니다.");
        }

        String extension = s3Service.extractFileExtension(request.fileName());
        if (extension == null || !List.of(
                "jpg", "jpeg", "png",   // 이미지
                "pdf", "docx", "txt",   // 문서
                "hwp", "hwpx",          // 한글
                "xlsx", "xls",          // 엑셀
                "pptx", "ppt"           // 파워포인트
        ).contains(extension)) {
            throw new IllegalArgumentException("허용되지 않은 파일 확장자입니다.");
        }

        String sanitizedFilename = s3Service.sanitizeFilename(request.fileName());
        String key = "announcements/" + UUID.randomUUID() + "/" + sanitizedFilename;

        return s3Service.generatePresignedUploadUrlWithKey(key, request.contentType());
    }

}
