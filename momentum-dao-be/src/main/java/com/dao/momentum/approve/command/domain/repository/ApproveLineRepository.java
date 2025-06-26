package com.dao.momentum.approve.command.domain.repository;

import com.dao.momentum.approve.command.domain.aggregate.ApproveLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApproveLineRepository extends JpaRepository<ApproveLine, Long> {

    /* 결재선 아이디로 결재선을 가져오는 메소드*/
    Optional<ApproveLine> getApproveLineById(Long approveLindId);

    /* 결재 아이디로 해당 결재에 속한 결재선의 상태 가져오기 */
    @Query("SELECT a.statusId FROM ApproveLine a WHERE a.approveId = :approveId")
    List<Integer> getApproveLinesByApproveId(Long approveId);

    /* 이전 결재선 찾기 */
    @Query("SELECT a FROM ApproveLine a WHERE a.approveId = :approveId AND a.approveLineOrder < :currentOrder")
    List<ApproveLine> getApproveLinesBeforeOrder(Long approveId, int currentOrder);

    /* 다음 결재선 찾기 */
    @Query("""
       SELECT al
       FROM ApproveLine al
       WHERE al.approveId = :approveId
         AND al.approveLineOrder > :currentOrder
       ORDER BY al.approveLineOrder ASC
       """)
    Optional<ApproveLine> findNextLine(Long approveId, int currentOrder);

    /* 결재 아이디로 해당 결재의 첫번째 결재선 찾기 */
    @Query("""
       SELECT al
       FROM ApproveLine al
       WHERE al.approveId = :approveId
       ORDER BY al.approveLineOrder ASC
       """)
    Optional<ApproveLine> findFirstLine(Long approveId);

}
