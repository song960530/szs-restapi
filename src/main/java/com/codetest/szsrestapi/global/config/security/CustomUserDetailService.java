package com.codetest.szsrestapi.global.config.security;

import com.codetest.szsrestapi.api.entity.User;
import com.codetest.szsrestapi.api.repository.UserRepository;
import com.codetest.szsrestapi.global.config.security.adapter.UserAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("ID를 찾을 수 없습니다."));

        return new UserAdapter(user);
    }
}
