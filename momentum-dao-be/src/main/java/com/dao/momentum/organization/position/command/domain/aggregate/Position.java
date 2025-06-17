package com.dao.momentum.organization.position.command.domain.aggregate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "`position`")
@Builder
@SQLDelete(sql = "update `position` set is_deleted = 'Y' where position_id = ?")
public class Position {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer positionId;

    @NotNull
    @Column(unique = true)
    private String name;

    @NotNull
    private Integer level;

    @Enumerated(EnumType.STRING)
    private IsDeleted isDeleted = IsDeleted.N;
}
