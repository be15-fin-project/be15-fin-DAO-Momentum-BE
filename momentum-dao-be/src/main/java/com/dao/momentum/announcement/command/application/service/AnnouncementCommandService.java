package com.dao.momentum.announcement.command.application.service;

import com.dao.momentum.announcement.command.application.dto.request.AnnouncementCreateRequest;
import com.dao.momentum.announcement.command.application.dto.request.AnnouncementModifyRequest;
import com.dao.momentum.announcement.command.application.dto.response.AnnouncementCreateResponse;
import com.dao.momentum.announcement.command.application.dto.response.AnnouncementModifyResponse;
import com.dao.momentum.announcement.command.application.mapper.AnnouncementMapper;
import com.dao.momentum.announcement.command.domain.aggregate.Announcement;
import com.dao.momentum.announcement.command.domain.repository.AnnouncementRepository;
import com.dao.momentum.announcement.exception.NoSuchAnnouncementException;
import com.dao.momentum.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementCommandService {

    private final AnnouncementMapper announcementMapper;
    private final AnnouncementRepository announcementRepository;

    @Transactional
    public AnnouncementCreateResponse create(AnnouncementCreateRequest announcementCreateRequest,
                                             List<MultipartFile> files,
                                             UserDetails userDetails) {
        Long empId = Long.valueOf(userDetails.getUsername());

        Announcement announcement = announcementMapper.toCreateEntity(announcementCreateRequest, empId);
        Announcement savedAnnouncement = announcementRepository.save(announcement);

        // TODO : CloudFront + S3 활용한 파일 업로드 구현

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
