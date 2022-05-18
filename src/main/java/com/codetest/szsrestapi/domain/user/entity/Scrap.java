package com.codetest.szsrestapi.domain.user.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name = "SEQ_SCRAP_GENERATOR"
        , sequenceName = "SEQ_SCRAP"
        , initialValue = 1
        , allocationSize = 1
)
public class Scrap {
    @Id
    @Column(name = "SCRAP_NO")
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE
            , generator = "SEQ_SCRAP_GENERATOR"
    )
    private Long scrapNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_NO")
    private User user;

    @Column(name = "SALARY")
    private int salary;

    @Column(name = "USE_AMOUNT")
    private int useAmount;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "HISTORY_NO")
    private ScrapHistory scrapHistory;

    public Scrap(User user, int salary, int useAmount, ScrapHistory scrapHistory) {
        this.user = user;
        this.salary = salary;
        this.useAmount = useAmount;
        this.scrapHistory = scrapHistory;
    }
}
