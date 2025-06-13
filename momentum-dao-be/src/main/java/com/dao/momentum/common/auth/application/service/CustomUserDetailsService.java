package com.dao.momentum.common.auth.application.service;


import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.employee.command.domain.aggregate.employee.Employee;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.dao.momentum.organization.employee.exception.EmployeeException;
import com.dao.momentum.organization.employee.query.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final EmployeeRepository employeeRepository;
    private final UserRoleMapper userRoleMapper;

    @Override
    public UserDetails loadUserByUsername(String empId) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByEmpId(Long.valueOf(empId))
                .orElseThrow(() -> new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND));

        List<String> employeeRoles = userRoleMapper.findByEmpId(employee.getEmpId());

        List<SimpleGrantedAuthority> authorities = employeeRoles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new org.springframework.security.core.userdetails.User(
                String.valueOf(employee.getEmpId()),
                employee.getPassword(),
                authorities
        );
    }
}