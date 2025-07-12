package com.dao.momentum.approve.command.domain.repository;

import com.dao.momentum.approve.command.domain.aggregate.ApproveLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
    List<Integer> getApproveLinesStatusByApproveId(Long approveId);

    /* 이전 결재선 찾기 */
    @Query("SELECT a FROM ApproveLine a WHERE a.approveId = :approveId AND a.approveLineOrder < :currentOrder")
    List<ApproveLine> getApproveLinesBeforeOrder(Long approveId, int currentOrder);

    /* 다음 결재선 찾기 */
    @Query(
            value = """
        SELECT *
        FROM approve_line
        WHERE approve_id = :approveId
          AND approve_line_order > :currentOrder
        ORDER BY approve_line_order ASC
        LIMIT 1
        """,
            nativeQuery = true
    )
    Optional<ApproveLine> findNextLine(Long approveId, int currentOrder);

    /* 결재 아이디로 해당 결재의 첫번째 결재선 찾기 */
    @Query(value = """
        SELECT * FROM approve_line
        WHERE approve_id = :approveId
        ORDER BY approve_line_order ASC
        LIMIT 1
    """, nativeQuery = true)
    Optional<ApproveLine> findFirstLine(Long approveId);

    /* 결재 아이디로 해당 결재에 속한 결재선의 아이디 가져오기 */
    @Query("SELECT a.id FROM ApproveLine a WHERE a.approveId = :approveId")
    List<Long> getApproveLinesByApproveId(Long approveId);

    @Modifying
    @Query("DELETE FROM ApproveLine al WHERE al.approveId = :approveId")
    void deleteApproveLinesByApproveId(Long approveId);

}
