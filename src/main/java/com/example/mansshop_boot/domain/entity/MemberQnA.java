package com.example.mansshop_boot.domain.entity;

import com.example.mansshop_boot.domain.dto.mypage.MemberQnAModifyDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberQnA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "qnaClassificationId")
    private QnAClassification qnAClassification;

    private String memberQnATitle;

    private String memberQnAContent;

    @CreationTimestamp
    private LocalDate createdAt;

    @CreationTimestamp
    private LocalDate updatedAt;

    private int memberQnAStat;

    public void setModifyData(MemberQnAModifyDTO modifyDTO, QnAClassification qnaClassification) {
        this.qnAClassification = qnaClassification;
        this.memberQnATitle = modifyDTO.title();
        this.memberQnAContent = modifyDTO.content();
    }
}
