package com.codetest.szsrestapi;

import com.codetest.szsrestapi.domain.user.EnumRole;
import com.codetest.szsrestapi.domain.user.dto.request.JoinReqDto;
import com.codetest.szsrestapi.domain.user.entity.Role;
import com.codetest.szsrestapi.domain.user.repository.RoleRepository;
import com.codetest.szsrestapi.domain.user.service.UserService;
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
        initService.userInit();
    }
}

@Component
@Transactional
@RequiredArgsConstructor
class InitService {
    private final UserService userService;
    private final RoleRepository roleRepository;

    public void roleInit() {
        Role role1 = new Role(EnumRole.ROLE_GUEST);
        Role role2 = new Role(EnumRole.ROLE_USER);
        roleRepository.save(role1);
        roleRepository.save(role2);
    }

    public void userInit() {
        JoinReqDto joinReqDto1 = new JoinReqDto();
        joinReqDto1.setUserId("test1");
        joinReqDto1.setPassword("123456");
        joinReqDto1.setName("홍길동");
        joinReqDto1.setRegNo("860824-1655068");
        userService.signup(joinReqDto1);
        JoinReqDto joinReqDto2 = new JoinReqDto();
        joinReqDto2.setUserId("test2");
        joinReqDto2.setPassword("123456");
        joinReqDto2.setName("김둘리");
        joinReqDto2.setRegNo("921108-1582816");
        userService.signup(joinReqDto2);
        JoinReqDto joinReqDto3 = new JoinReqDto();
        joinReqDto3.setUserId("test3");
        joinReqDto3.setPassword("123456");
        joinReqDto3.setName("마징가");
        joinReqDto3.setRegNo("880601-2455116");
        userService.signup(joinReqDto3);
        JoinReqDto joinReqDto4 = new JoinReqDto();
        joinReqDto4.setUserId("test4");
        joinReqDto4.setPassword("123456");
        joinReqDto4.setName("베지터");
        joinReqDto4.setRegNo("910411-1656116");
        userService.signup(joinReqDto4);
        JoinReqDto joinReqDto5 = new JoinReqDto();
        joinReqDto5.setUserId("test5");
        joinReqDto5.setPassword("123456");
        joinReqDto5.setName("손오공");
        joinReqDto5.setRegNo("820326-2715702");
        userService.signup(joinReqDto5);

    }
}
