package com.dao.momentum.announcement.command.application.service;

import com.dao.momentum.announcement.command.application.dto.request.AnnouncementCreateRequest;
import com.dao.momentum.announcement.command.application.dto.response.AnnouncementCreateResponse;
import com.dao.momentum.announcement.command.application.mapper.AnnouncementMapper;
import com.dao.momentum.announcement.command.domain.aggregate.Announcement;
import com.dao.momentum.announcement.command.domain.repository.AnnouncementRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementCommandService {

    private final AnnouncementMapper announcementMapper;
    private final AnnouncementRepository announcementRepository;

    @Transactional
    public AnnouncementCreateResponse create(@RequestPart @Valid AnnouncementCreateRequest announcementCreateRequest,
                       @RequestPart List<MultipartFile> files,
                       UserDetails userDetails)
    {
        Long empId = Long.valueOf(userDetails.getUsername());
        Announcement announcement = announcementMapper.toCreateEntity(announcementCreateRequest, empId);
        Announcement savedAnnouncement = announcementRepository.save(announcement);

        // TODO : CloudFront + S3 활용한 파일 업로드 구현

        log.info("공지사항 작성 - empId={}, title={}", empId, announcementCreateRequest.getTitle());
        
        return announcementMapper.toCreateResponse(savedAnnouncement);
    }
}
