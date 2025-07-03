package com.dao.momentum.announcement.command.application.service;

import com.dao.momentum.announcement.command.application.dto.request.AnnouncementCreateRequest;
import com.dao.momentum.announcement.command.application.dto.request.AnnouncementModifyRequest;
import com.dao.momentum.file.command.application.dto.request.AttachmentRequest;
import com.dao.momentum.announcement.command.application.dto.response.AnnouncementCreateResponse;
import com.dao.momentum.announcement.command.application.dto.response.AnnouncementModifyResponse;
import com.dao.momentum.announcement.command.application.mapper.AnnouncementMapper;
import com.dao.momentum.announcement.command.domain.aggregate.Announcement;
import com.dao.momentum.announcement.command.domain.repository.AnnouncementRepository;
import com.dao.momentum.announcement.exception.AnnouncementAccessDeniedException;
import com.dao.momentum.announcement.exception.NoSuchAnnouncementException;
import com.dao.momentum.common.s3.S3Service;
import com.dao.momentum.file.command.domain.aggregate.File;
import com.dao.momentum.file.command.domain.repository.FileRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnnouncementCommandServiceTest {

    @Mock
    private AnnouncementMapper announcementMapper;

    @Mock
    private AnnouncementRepository announcementRepository;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private AnnouncementCommandService announcementCommandService;

    @Test
    @DisplayName("공지사항 생성 성공")
    void createAnnouncement_success() {
        // given
        AnnouncementCreateRequest request = new AnnouncementCreateRequest();
        request.setTitle("공지사항 제목");
        request.setContent("공지사항 내용");

        AttachmentRequest attachment = new AttachmentRequest();
        attachment.setS3Key("announcements/uuid/test_file.png");
        attachment.setType("png");
        attachment.setName("test_file.png");
        request.setAttachments(List.of(attachment));

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("1");

        Announcement announcement = Announcement.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .empId(1L)
                .build();

        Announcement savedAnnouncement = Announcement.builder()
                .announcementId(1L)
                .title(request.getTitle())
                .content(request.getContent())
                .empId(1L)
                .build();

        AnnouncementCreateResponse expectedResponse = new AnnouncementCreateResponse(1L);

        when(announcementMapper.toCreateEntity(request, 1L)).thenReturn(announcement);
        when(announcementRepository.save(any())).thenReturn(savedAnnouncement);
        when(announcementMapper.toCreateResponse(savedAnnouncement)).thenReturn(expectedResponse);

        // when
        AnnouncementCreateResponse actualResponse = announcementCommandService.create(request, mockUserDetails);

        // then
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getAnnouncementId(), actualResponse.getAnnouncementId());

        verify(fileRepository).save(argThat(file ->
                file.getAnnouncementId().equals(1L)
                        && file.getS3Key().equals("announcements/uuid/test_file.png")
                        && file.getType().equals("png")
        ));
    }

    @Test
    @DisplayName("공지사항 수정 성공")
    void modifyAnnouncement_success() {
        // given
        Long announcementId = 1L;
        Long empId = 1L;

        AnnouncementModifyRequest request = new AnnouncementModifyRequest();
        request.setTitle("수정된 제목");
        request.setContent("수정된 내용");
        request.setRemainFileIdList(List.of(100L));

        AttachmentRequest newAttachment = new AttachmentRequest();
        newAttachment.setS3Key("announcements/uuid/new_file.pdf");
        newAttachment.setType("pdf");
        newAttachment.setName("new_file.pdf");
        request.setAttachments(List.of(newAttachment));

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn(empId.toString());

        Announcement announcement = Announcement.builder()
                .announcementId(announcementId)
                .title("기존 제목")
                .content("기존 내용")
                .empId(empId)
                .build();

        File retainedFile = File.builder()
                .attachmentId(100L)
                .announcementId(announcementId)
                .s3Key("announcements/uuid/old_file.pdf")
                .type("pdf")
                .build();

        File deletedFile = File.builder()
                .attachmentId(200L)
                .announcementId(announcementId)
                .s3Key("announcements/uuid/delete_file.pdf")
                .type("pdf")
                .build();

        when(announcementRepository.findById(announcementId)).thenReturn(Optional.of(announcement));
        when(fileRepository.findAllByAnnouncementId(announcementId)).thenReturn(List.of(retainedFile, deletedFile));
        when(announcementMapper.toModifyResponse(announcement)).thenReturn(new AnnouncementModifyResponse(announcementId));

        // when
        AnnouncementModifyResponse response = announcementCommandService.modify(request, announcementId, mockUserDetails);

        // then
        assertNotNull(response);
        assertEquals(announcementId, response.getAnnouncementId());

        verify(s3Service).deleteFileFromS3(deletedFile.getS3Key());
        verify(fileRepository).deleteById(deletedFile.getAttachmentId());

        verify(fileRepository).save(argThat(file ->
                file.getAnnouncementId().equals(announcementId)
                        && file.getType().equals("pdf")
                        && file.getS3Key().equals("announcements/uuid/new_file.pdf")
        ));
    }

    @Test
    @DisplayName("공지사항 수정 실패 - 존재하지 않는 ID")
    void modifyAnnouncement_fail_notFound() {
        // given
        Long announcementId = 999L;

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("1");

        AnnouncementModifyRequest request = new AnnouncementModifyRequest();
        request.setTitle("수정된 제목");
        request.setContent("수정된 내용");
        request.setRemainFileIdList(List.of(1L));

        AttachmentRequest newAttachment = new AttachmentRequest();
        newAttachment.setS3Key("announcements/uuid/new.pdf");
        newAttachment.setType("pdf");
        newAttachment.setName("new.pdf");
        request.setAttachments(List.of(newAttachment));

        when(announcementRepository.findById(announcementId))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(NoSuchAnnouncementException.class, () ->
                announcementCommandService.modify(request, announcementId, mockUserDetails)
        );
    }

    @Test
    @DisplayName("공지사항 수정 실패 - 작성자가 아님")
    void modifyAnnouncement_fail_notAuthor() {
        // given
        Long announcementId = 1L;
        Long empId = 2L; // 로그인된 사용자
        Long actualAuthorId = 1L; // 실제 작성자

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn(empId.toString());

        Announcement announcement = Announcement.builder()
                .announcementId(announcementId)
                .title("기존 제목")
                .content("기존 내용")
                .empId(actualAuthorId)
                .build();

        when(announcementRepository.findById(announcementId)).thenReturn(Optional.of(announcement));

        AnnouncementModifyRequest request = new AnnouncementModifyRequest();
        request.setTitle("수정된 제목");
        request.setContent("수정된 내용");

        // when & then
        assertThrows(AnnouncementAccessDeniedException.class, () ->
                announcementCommandService.modify(request, announcementId, mockUserDetails)
        );
    }

    @Test
    @DisplayName("공지사항 삭제 성공")
    void deleteAnnouncement_success() {
        // given
        Long announcementId = 1L;
        Long empId = 1L;

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn(empId.toString());

        Announcement announcement = Announcement.builder()
                .announcementId(announcementId)
                .empId(empId)
                .title("삭제할 제목")
                .build();

        File file1 = File.builder()
                .attachmentId(101L)
                .announcementId(announcementId)
                .s3Key("announcements/uuid/file1.png")
                .type("png")
                .build();

        File file2 = File.builder()
                .attachmentId(102L)
                .announcementId(announcementId)
                .s3Key("announcements/uuid/file2.pdf")
                .type("pdf")
                .build();

        when(announcementRepository.findById(announcementId)).thenReturn(Optional.of(announcement));
        when(fileRepository.findAllByAnnouncementId(announcementId)).thenReturn(List.of(file1, file2));

        // when & then
        assertDoesNotThrow(() -> announcementCommandService.delete(announcementId, mockUserDetails));

        verify(announcementRepository).findById(announcementId);
        verify(fileRepository).findAllByAnnouncementId(announcementId);
        verify(s3Service).deleteFileFromS3(file1.getS3Key());
        verify(s3Service).deleteFileFromS3(file2.getS3Key());
        verify(fileRepository).deleteById(file1.getAttachmentId());
        verify(fileRepository).deleteById(file2.getAttachmentId());
        verify(announcementRepository).delete(announcement);
    }

    @Test
    @DisplayName("공지사항 삭제 실패 - 존재하지 않는 ID")
    void deleteAnnouncement_fail_notFound() {
        // given
        Long announcementId = 999L;
        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("1");

        when(announcementRepository.findById(announcementId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NoSuchAnnouncementException.class, () ->
                announcementCommandService.delete(announcementId, mockUserDetails)
        );

        verify(announcementRepository).findById(announcementId);
        verifyNoMoreInteractions(fileRepository, announcementRepository);
    }

    @Test
    @DisplayName("공지사항 삭제 실패 - 작성자가 아님")
    void deleteAnnouncement_fail_notAuthor() {
        // given
        Long announcementId = 1L;
        Long actualAuthorId = 1L;
        Long loggedInUserId = 2L;

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn(loggedInUserId.toString());

        Announcement announcement = Announcement.builder()
                .announcementId(announcementId)
                .empId(actualAuthorId)
                .title("공지사항")
                .build();

        when(announcementRepository.findById(announcementId)).thenReturn(Optional.of(announcement));

        // when & then
        assertThrows(AnnouncementAccessDeniedException.class, () ->
                announcementCommandService.delete(announcementId, mockUserDetails)
        );

        verify(announcementRepository).findById(announcementId);
        verifyNoMoreInteractions(fileRepository, announcementRepository);
    }
}
