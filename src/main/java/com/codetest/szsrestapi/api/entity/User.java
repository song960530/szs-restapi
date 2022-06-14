package com.codetest.szsrestapi.api.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name = "SEQ_USER_GENERATOR"
        , sequenceName = "SEQ_USER"
        , initialValue = 1
        , allocationSize = 1
)
@Table(name = "USERS")
public class User {
    @Id
    @Column(name = "USER_NO")
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE
            , generator = "SEQ_USER_GENERATOR"
    )
    private Long userNo;

    @Column(name = "USER_ID", unique = true)
    private String userId;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "NAME")
    private String name;

    @Column(name = "REG_NO", unique = true)
    private String regNo;

    @ManyToMany
    @JoinTable(
            name = "USER_ROLES"
            , joinColumns = @JoinColumn(name = "USER_NO")
            , inverseJoinColumns = @JoinColumn(name = "ROLE_NO")
    )
    private List<Role> roles = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ScrapHistory> scrapHistories = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Scrap> scraps = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserIp userIp;

    public User(String userId, String password, String name, String regNo, List<Role> roles) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.regNo = regNo;
        this.roles = roles;
    }
}
