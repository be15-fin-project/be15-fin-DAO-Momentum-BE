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
import com.dao.momentum.announcement.exception.AnnouncementAccessDeniedException;
import com.dao.momentum.announcement.exception.NoSuchAnnouncementException;
import com.dao.momentum.common.service.S3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

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
    void createAnnouncement_success() throws Exception {
        // given
        AnnouncementCreateRequest request = new AnnouncementCreateRequest();
        request.setTitle("공지사항 제목");
        request.setContent("공지사항 내용");

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("1");

        Announcement announcement = Announcement.builder()
                .title("공지사항 제목")
                .content("공지사항 내용")
                .empId(1L)
                .build();

        Announcement savedAnnouncement = Announcement.builder()
                .announcementId(1L)
                .title("공지사항 제목")
                .content("공지사항 내용")
                .empId(1L)
                .build();

        AnnouncementCreateResponse expectedResponse = new AnnouncementCreateResponse(1L);

        MockMultipartFile file = new MockMultipartFile(
                "files", "test file.txt", "text/plain", "파일의 내용입니다.".getBytes()
        );

        List<MultipartFile> files = List.of(file);

        when(announcementMapper.toCreateEntity(request, 1L)).thenReturn(announcement);
        when(announcementRepository.save(any())).thenReturn(savedAnnouncement);
        when(announcementMapper.toCreateResponse(savedAnnouncement)).thenReturn(expectedResponse);
        when(s3Service.sanitizeFilename(any())).thenReturn("test_file.txt");
        when(s3Service.uploadFile(any(), any(), eq("text/plain"))).thenReturn("https://cdn.test.com/announcements/1/uuid/test_file.txt");
        when(s3Service.extractFileExtension(any())).thenReturn("txt");

        // when
        AnnouncementCreateResponse actualResponse = announcementCommandService.create(request, files, mockUserDetails);

        // then
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getAnnouncementId(), actualResponse.getAnnouncementId());

        verify(announcementMapper).toCreateEntity(request, 1L);
        verify(announcementRepository).save(argThat(saved ->
                saved.getTitle().equals("공지사항 제목") &&
                        saved.getContent().equals("공지사항 내용") &&
                        saved.getEmpId().equals(1L)
        ));
        verify(announcementMapper).toCreateResponse(savedAnnouncement);

        verify(s3Service).sanitizeFilename("test file.txt");
        verify(s3Service).extractFileExtension("test file.txt");
        verify(s3Service).uploadFile(anyString(), any(), eq("text/plain"));

        verify(fileRepository).save(argThat(fileEntity ->
                fileEntity.getAnnouncementId().equals(1L) &&
                        fileEntity.getUrl().contains("test_file.txt") &&
                        fileEntity.getType().equals("txt")
        ));
    }

    @DisplayName("공지사항 수정 성공")
    @Test
    void modifyAnnouncement_success() throws Exception {
        // given
        Long announcementId = 1L;
        Long empId = 1L;

        AnnouncementModifyRequest request = new AnnouncementModifyRequest();
        request.setTitle("수정된 제목");
        request.setContent("수정된 내용");
        request.setRemainFileIdList(List.of(100L)); // 유지할 파일 ID

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
                .url("https://cdn.test.com/announcements/1/uuid/old.txt")
                .type("txt")
                .build();

        File deletedFile = File.builder()
                .attachmentId(200L)
                .announcementId(announcementId)
                .url("https://cdn.test.com/announcements/1/uuid/delete.txt")
                .type("txt")
                .build();

        MockMultipartFile newFile = new MockMultipartFile(
                "files", "new file.pdf", "application/pdf", "new content".getBytes()
        );

        List<MultipartFile> files = List.of(newFile);

        when(announcementRepository.findById(announcementId)).thenReturn(java.util.Optional.of(announcement));
        when(fileRepository.findAllByAnnouncementId(announcementId)).thenReturn(List.of(retainedFile, deletedFile));
        when(s3Service.sanitizeFilename(any())).thenReturn("new_file.pdf");
        when(s3Service.uploadFile(any(), any(), any())).thenReturn("https://cdn.test.com/announcements/1/uuid/new_file.pdf");
        when(s3Service.extractFileExtension(any())).thenReturn("pdf");

        when(announcementMapper.toModifyResponse(any())).thenReturn(
                new AnnouncementModifyResponse(announcementId)
        );

        // when
        AnnouncementModifyResponse response = announcementCommandService.modify(request, files, announcementId, mockUserDetails);

        // then
        assertNotNull(response);
        assertEquals(announcementId, response.getAnnouncementId());

        verify(s3Service).deleteFileFromS3(deletedFile.getUrl());
        verify(fileRepository).deleteById(deletedFile.getAttachmentId());
        verify(fileRepository).save(argThat(saved ->
                saved.getAnnouncementId().equals(announcementId) &&
                        saved.getType().equals("pdf") &&
                        saved.getUrl().contains("new_file.pdf")
        ));
        verify(announcementMapper).toModifyResponse(announcement);
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

        when(announcementRepository.findById(announcementId))
                .thenReturn(java.util.Optional.empty());

        // when & then
        assertThrows(NoSuchAnnouncementException.class, () ->
                announcementCommandService.modify(request, List.of(), announcementId, mockUserDetails)
        );
    }

    @Test
    @DisplayName("공지사항 수정 실패 - 작성자가 아님")
    void modifyAnnouncement_fail_notAuthor() {
        // given
        Long announcementId = 1L;
        Long empId = 2L; // 작성자가 아님

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn(empId.toString());

        Announcement announcement = Announcement.builder()
                .announcementId(announcementId)
                .title("기존 제목")
                .content("기존 내용")
                .empId(1L) // 실제 작성자 ID
                .build();

        when(announcementRepository.findById(announcementId)).thenReturn(java.util.Optional.of(announcement));

        AnnouncementModifyRequest request = new AnnouncementModifyRequest();
        request.setTitle("수정된 제목");
        request.setContent("수정된 내용");

        // when & then
        assertThrows(AnnouncementAccessDeniedException.class, () ->
                announcementCommandService.modify(request, List.of(), announcementId, mockUserDetails)
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

        File file1 = File.builder().attachmentId(101L).announcementId(announcementId).build();
        File file2 = File.builder().attachmentId(102L).announcementId(announcementId).build();

        when(announcementRepository.findById(announcementId)).thenReturn(Optional.of(announcement));
        when(fileRepository.findAllByAnnouncementId(announcementId)).thenReturn(List.of(file1, file2));

        // when & then
        assertDoesNotThrow(() -> announcementCommandService.delete(announcementId, mockUserDetails));

        verify(announcementRepository).findById(announcementId);
        verify(fileRepository).findAllByAnnouncementId(announcementId);
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
        Long otherUserId = 2L;

        UserDetails mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn(otherUserId.toString());

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
