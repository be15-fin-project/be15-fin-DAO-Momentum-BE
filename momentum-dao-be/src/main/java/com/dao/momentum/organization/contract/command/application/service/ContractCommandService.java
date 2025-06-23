package com.dao.momentum.organization.contract.command.application.service;

import com.dao.momentum.announcement.command.application.dto.request.AttachmentRequest;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.common.service.S3Service;
import com.dao.momentum.file.command.domain.aggregate.File;
import com.dao.momentum.file.command.domain.repository.FileRepository;
import com.dao.momentum.organization.contract.command.application.dto.request.ContractCreateRequest;
import com.dao.momentum.organization.contract.command.application.dto.response.ContractCreateResponse;
import com.dao.momentum.organization.contract.command.application.dto.response.ContractDeleteResponse;
import com.dao.momentum.organization.contract.command.domain.aggregate.Contract;
import com.dao.momentum.organization.contract.command.domain.aggregate.ContractType;
import com.dao.momentum.organization.contract.command.domain.repository.ContractRepository;
import com.dao.momentum.organization.contract.exception.ContractException;
import com.dao.momentum.organization.employee.command.application.service.EmployeeCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractCommandService {

    private final ContractRepository contractRepository;
    private final FileRepository fileRepository;
    private final EmployeeCommandService employeeCommandService;
    private final S3Service s3Service;

    @Transactional
    public ContractCreateResponse createContract(ContractCreateRequest contractCreateRequest, UserDetails userDetails) {
        long adminId = Long.parseLong(userDetails.getUsername());
        employeeCommandService.validateActiveAdmin(adminId);

        long empId = contractCreateRequest.getEmpId();
        LocalDateTime now = LocalDateTime.now();

        ContractType contractType = contractCreateRequest.getType();
        BigDecimal salary = contractCreateRequest.getSalary();

        if (contractType == ContractType.SALARY_AGREEMENT && salary == null) {
            log.warn("유효하지 않은 연봉 계약 요청 - salary: {}", salary);
            throw new ContractException(ErrorCode.INVALID_SALARY_AGREEMENT);
        }
        if (contractType != ContractType.SALARY_AGREEMENT && salary != null) {
            log.warn("유효하지 않은 계약 요청 - type: {}, salary: {}", contractType, salary);
            throw new ContractException(ErrorCode.INVALID_SALARY_AGREEMENT);
        }

        Contract contract = Contract.builder()
                .empId(empId)
                .type(contractType)
                .salary(salary)
                .createdAt(now)
                .build();

        AttachmentRequest attachment = contractCreateRequest.getAttachment();
        if (attachment == null) {
            throw new ContractException(ErrorCode.ATTACHMENT_REQUIRED);
        }

        File file = File.builder()
                .contractId(contract.getContractId())
                .url(attachment.getS3Key())
                .type(attachment.getType())
                .build();

        fileRepository.save(file);
        contractRepository.save(contract);

        log.info("계약서 등록 완료: 계약서 ID - {}, 등록자 ID - {}, 계약 대상자 ID - {}, 등록일시 - {}", contract.getContractId(), adminId, empId, now);

        return ContractCreateResponse.builder()
                .contractId(contract.getContractId())
                .message("계약서 등록 완료")
                .build();
    }

    @Transactional
    public ContractDeleteResponse deleteContract(long contractId, UserDetails userDetails) {
        Contract contractToDelete = contractRepository.findById(contractId)
                .orElseThrow(() -> {
                    log.warn("계약서 조회 실패 - contractId: {}", contractId);
                    return new ContractException(ErrorCode.CONTRACT_NOT_FOUND);
                });

        long adminId = Long.parseLong(userDetails.getUsername());
        employeeCommandService.validateActiveAdmin(adminId);

        long empId = contractToDelete.getEmpId();
        contractRepository.delete(contractToDelete);

        // 첨부파일 hard delete
        File file = fileRepository.findByContractId(contractId)
                        .orElseThrow(() -> {
                            log.warn("계약서 첨부파일 조회 실패 - contractId: {}", contractId);
                            return new ContractException(ErrorCode.ATTACHMENT_NOT_FOUND);
                        });
        s3Service.deleteFileFromS3(file.getUrl());
        fileRepository.deleteById(file.getAttachmentId());

        log.info("계약서 삭제 완료: 삭제자 ID - {}, 계약서 ID - {}, 계약 대상자 ID - {}, 삭제일시 - {}", adminId, contractId, empId, LocalDateTime.now());

        return ContractDeleteResponse.builder()
                .contractId(contractId)
                .message("계약서 삭제 완료")
                .build();
    }
}
