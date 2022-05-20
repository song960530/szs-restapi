package com.codetest.szsrestapi;

import com.codetest.szsrestapi.api.enums.EnumRole;
import com.codetest.szsrestapi.api.dto.request.JoinReqDto;
import com.codetest.szsrestapi.api.entity.Role;
import com.codetest.szsrestapi.api.entity.User;
import com.codetest.szsrestapi.api.repository.RoleRepository;
import com.codetest.szsrestapi.api.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile({"local"})
public class InitDb {
    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.roleInit();
//        List<User> users = initService.userInit();
    }
}

@Component
@Transactional
@RequiredArgsConstructor
class InitService {
    private final UserServiceImpl userService;
    private final RoleRepository roleRepository;

    public void roleInit() {
        Role role1 = new Role(EnumRole.ROLE_GUEST);
        Role role2 = new Role(EnumRole.ROLE_USER);
        roleRepository.save(role1);
        roleRepository.save(role2);
    }

    public List<User> userInit() {
        List<User> users = new ArrayList<>();

        JoinReqDto joinReqDto1 = new JoinReqDto();
        joinReqDto1.setUserId("test1");
        joinReqDto1.setPassword("123456");
        joinReqDto1.setName("홍길동");
        joinReqDto1.setRegNo("860824-1655068");
        users.add(userService.signup(joinReqDto1));

        JoinReqDto joinReqDto2 = new JoinReqDto();
        joinReqDto2.setUserId("test2");
        joinReqDto2.setPassword("123456");
        joinReqDto2.setName("김둘리");
        joinReqDto2.setRegNo("921108-1582816");
        users.add(userService.signup(joinReqDto2));

        JoinReqDto joinReqDto3 = new JoinReqDto();
        joinReqDto3.setUserId("test3");
        joinReqDto3.setPassword("123456");
        joinReqDto3.setName("마징가");
        joinReqDto3.setRegNo("880601-2455116");
        users.add(userService.signup(joinReqDto3));

        JoinReqDto joinReqDto4 = new JoinReqDto();
        joinReqDto4.setUserId("test4");
        joinReqDto4.setPassword("123456");
        joinReqDto4.setName("베지터");
        joinReqDto4.setRegNo("910411-1656116");
        users.add(userService.signup(joinReqDto4));

        JoinReqDto joinReqDto5 = new JoinReqDto();
        joinReqDto5.setUserId("test5");
        joinReqDto5.setPassword("123456");
        joinReqDto5.setName("손오공");
        joinReqDto5.setRegNo("820326-2715702");
        users.add(userService.signup(joinReqDto5));

        return users;
    }
}
