package com.codetest.szsrestapi.domain.user.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name = "SEQ_USER_IP_GENERATOR"
        , sequenceName = "SEQ_USER_IP"
        , initialValue = 1
        , allocationSize = 1
)
public class UserIp {
    @Id
    @Column(name = "USER_IP_NO")
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE
            , generator = "SEQ_USER_IP_GENERATOR"
    )
    private Long userIpNo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_NO")
    private User user;

    @Column(name = "LOGIN_IP")
    private String loginIp;

    public UserIp(User user, String loginIp) {
        this.user = user;
        this.loginIp = loginIp;
    }

    public void changeLoginIp(String ip) {
        this.loginIp = ip;
    }
}
