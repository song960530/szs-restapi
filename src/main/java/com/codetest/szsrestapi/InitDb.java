package com.codetest.szsrestapi;

import com.codetest.szsrestapi.domain.user.EnumRole;
import com.codetest.szsrestapi.domain.user.entity.Role;
import com.codetest.szsrestapi.domain.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class InitDb {
    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.roleInit();
    }
}

@Component
@Transactional
@RequiredArgsConstructor
class InitService {
    private final RoleRepository roleRepository;

    public void roleInit() {
        Role role1 = new Role(EnumRole.ROLE_GUEST);
        Role role2 = new Role(EnumRole.ROLE_USER);
        roleRepository.save(role1);
        roleRepository.save(role2);
    }
}
