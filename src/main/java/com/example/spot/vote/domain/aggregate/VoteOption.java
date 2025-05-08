package com.example.spot.vote.domain.aggregate;
import com.example.spot.common.entity.BaseEntity;
import com.example.spot.vote.domain.Vote;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoteOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id", nullable = false)
    private Vote vote;

    @Setter
    private String content;

    @OneToMany(mappedBy = "voteOption", cascade = CascadeType.ALL)
    private List<VoteParticipant> voteParticipants;

/* ----------------------------- 생성자 ------------------------------------- */

    @Builder
    public VoteOption(Vote vote, String content) {
        this.vote = vote;
        this.content = content;
        this.voteParticipants = new ArrayList<>();
    }

/* ----------------------------- 연관관계 메소드 ------------------------------------- */

    public void addMemberVote(VoteParticipant voteParticipant) {
        if (voteParticipants == null) {
            voteParticipants = new ArrayList<>();
        }
        voteParticipants.add(voteParticipant);
        voteParticipant.setVoteOption(this);
    }

    public void deleteAllMemberVotes() {
        voteParticipants.clear();
    }

}
