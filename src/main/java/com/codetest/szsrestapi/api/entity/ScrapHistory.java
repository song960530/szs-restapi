package com.codetest.szsrestapi.api.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name = "SEQ_SCRAPHISTORY_GENERATOR"
        , sequenceName = "SEQ_SCRAPHISTORY"
        , initialValue = 1
        , allocationSize = 1
)
public class ScrapHistory {
    @Id
    @Column(name = "HISTORY_NO")
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE
            , generator = "SEQ_SCRAPHISTORY_GENERATOR"
    )
    private Long historyNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_NO")
    private User user;

    @Column(name = "STATUS")
    private String status;

    @Lob
    @Column(name = "RESPONSE")
    private String response;

    @OneToOne(mappedBy = "scrapHistory", fetch = FetchType.LAZY)
    private Scrap scrap;

    public ScrapHistory(User user, String status, String response) {
        this.user = user;
        this.status = status;
        this.response = response;
    }
}
