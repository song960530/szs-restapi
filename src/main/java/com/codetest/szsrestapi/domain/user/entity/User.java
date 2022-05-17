package com.codetest.szsrestapi.domain.user.entity;

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

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "NAME")
    private String name;

    @Column(name = "REG_NO")
    private String regNo;

    @ManyToMany
    @JoinTable(
            name = "USER_ROLES"
            , joinColumns = @JoinColumn(name = "USER_NO")
            , inverseJoinColumns = @JoinColumn(name = "ROLE_NO")
    )
    private List<Role> roles = new ArrayList<>();

    public User(String userId, String password, String name, String regNo, List<Role> roles) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.regNo = regNo;
        this.roles = roles;
    }
}