package com.dao.momentum.work.command.application.validator;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.work.exception.WorkException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.util.SubnetUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class IpValidator {

    public void validateIp(String ip, List<String> allowedIps) {
        boolean allowed = allowedIps.stream().anyMatch(allowedIp -> {
            try {
                if (allowedIp.contains("/")) {
                    SubnetUtils subnet = new SubnetUtils(allowedIp);
                    subnet.setInclusiveHostCount(true);
                    return subnet.getInfo().isInRange(ip);
                }
                return allowedIp.equals(ip);
            } catch (IllegalArgumentException e) {
                log.warn("잘못된 IP 형식: {}", allowedIp);
                return false;
            }
        });

        if (!allowed) throw new WorkException(ErrorCode.IP_NOT_ALLOWED);
    }

}
