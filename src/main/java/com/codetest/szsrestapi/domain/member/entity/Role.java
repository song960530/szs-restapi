package com.codetest.szsrestapi.domain.member.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name = "SEQ_ROLE_GENERATOR"
        , sequenceName = "SEQ_ROLE"
        , initialValue = 1
        , allocationSize = 1
)
public class Role {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "SEQ_ROLE_GENERATOR")
    @Column(name = "ROLE_NO")
    private Long roleNo;

    @Column(name = "ROLES")
    private String roles;

    @ManyToMany
    @JoinTable(
            name = "MEMBER_ROLES"
            , joinColumns = @JoinColumn(name = "ROLE_NO")
            , inverseJoinColumns = @JoinColumn(name = "MEMBER_NO")
    )
    private List<Member> members;

    public Role(Long roleNo, String roles) {
        this.roleNo = roleNo;
        this.roles = roles;
    }
}