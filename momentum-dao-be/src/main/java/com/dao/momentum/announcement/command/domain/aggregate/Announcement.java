package com.dao.momentum.announcement.command.domain.aggregate;

import com.dao.momentum.announcement.exception.AnnouncementAccessDeniedException;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.file.command.domain.aggregate.IsDeleted;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE announcement SET is_deleted = true WHERE announcement_id = ?")
@EntityListeners(AuditingEntityListener.class)
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long announcementId;

    private Long empId;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private IsDeleted isDeleted;

    public void validateAuthor(Long empId) {
        if (!empId.equals(this.empId)) {
            throw new AnnouncementAccessDeniedException(ErrorCode.ANNOUNCEMENT_NOT_AUTHOR);
        }
    }

    public void modify(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
