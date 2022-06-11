package com.codetest.szsrestapi.global.config.security.adapter;


import com.codetest.szsrestapi.api.entity.User;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.stream.Collectors;

@Getter
public class UserAdapter extends org.springframework.security.core.userdetails.User {
    private User user;

    public UserAdapter(User user) {
        super(
                user.getUserId()
                , user.getPassword()
                , user.getRoles().stream()
                        .map(m -> new SimpleGrantedAuthority(m.getRoles().name()))
                        .collect(Collectors.toList())
        );
        this.user = user;
    }
}
