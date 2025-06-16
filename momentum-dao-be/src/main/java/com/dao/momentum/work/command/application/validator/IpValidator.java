package com.dao.momentum.work.command.application.validator;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.work.exception.WorkException;
import inet.ipaddr.AddressStringException;
import inet.ipaddr.IPAddress;
import inet.ipaddr.IPAddressString;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class IpValidator {

    public void validateIp(String ip, List<String> allowedIps) {
        IPAddress inputIp;

        try {
            inputIp = new IPAddressString(ip).toAddress();
        } catch (Exception e) {
            log.warn("유효하지 않은 입력 IP 형식: {}", ip, e);
            throw new WorkException(ErrorCode.IP_NOT_ALLOWED);
        }

        boolean allowed = allowedIps.stream().anyMatch(allowedIp -> {
            try {
                    IPAddress allowedRange = new IPAddressString(allowedIp).toAddress();
                    return allowedRange.contains(inputIp);
            } catch (AddressStringException e) {
                log.warn("잘못된 IP 형식: {}", allowedIp);
                return false;
            }
        });

        if (!allowed) throw new WorkException(ErrorCode.IP_NOT_ALLOWED);
    }

}
