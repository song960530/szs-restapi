package com.codetest.szsrestapi.global.config.security;

import com.codetest.szsrestapi.domain.user.entity.User;
import com.codetest.szsrestapi.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("ID를 찾을 수 없습니다."));

        return new org.springframework.security.core.userdetails.User(user.getUserId(), user.getPassword(), user.getRoles().stream()
                .map(m -> new SimpleGrantedAuthority(m.getRoles().name()))
                .collect(Collectors.toList()));
    }
}
