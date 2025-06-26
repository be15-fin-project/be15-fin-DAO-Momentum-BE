package com.dao.momentum.approve.command.domain.repository;

import com.dao.momentum.approve.command.domain.aggregate.ApproveLineList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApproveLineListRepository extends JpaRepository<ApproveLineList, Long> {

    /* 결재선 목록 아이디로 결재선 목록 가져오기 */
    Optional<ApproveLineList> getApproveLineListById(Long approveLineListId);

    /* 결재선 아이디로 결재선 목록의 상태 가져오기 */
    @Query("SELECT all.statusId " +
            "FROM ApproveLineList all " +
            "WHERE all.approveLineId = :approveLineId")
    List<Integer> getAssigneeStatusByApproveLineId(Long approveLineId);

    /* 결재선 아이디로 해당 결재선의 결재자 목록 가져오기 */
    List<ApproveLineList> findByApproveLineId(Long approveLineId);

}
