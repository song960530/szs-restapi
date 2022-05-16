package com.codetest.szsrestapi.domain.member.entity;

import com.codetest.szsrestapi.domain.member.EnumRole;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @Enumerated(EnumType.STRING)
    private EnumRole roles;

    @ManyToMany
    @JoinTable(
            name = "MEMBER_ROLES"
            , joinColumns = @JoinColumn(name = "ROLE_NO")
            , inverseJoinColumns = @JoinColumn(name = "MEMBER_NO")
    )
    private List<Member> members;

    public Role(EnumRole roles) {
        this.roles = roles;
    }
}