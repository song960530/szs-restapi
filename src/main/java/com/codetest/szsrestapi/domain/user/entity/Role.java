package com.codetest.szsrestapi.domain.user.entity;

import com.codetest.szsrestapi.domain.user.EnumRole;
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
            name = "USER_ROLES"
            , joinColumns = @JoinColumn(name = "ROLE_NO")
            , inverseJoinColumns = @JoinColumn(name = "USER_NO")
    )
    private List<User> users;

    public Role(EnumRole roles) {
        this.roles = roles;
    }
}