package com.example.mansshop_boot.domain.entity;

import com.example.mansshop_boot.domain.dto.mypage.qna.in.MemberQnAModifyDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "memberQnA")
@ToString
public class MemberQnA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "qnaClassificationId", nullable = false)
    private QnAClassification qnAClassification;

    @Column(length = 200,
            nullable = false
    )
    private String memberQnATitle;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String memberQnAContent;

    @CreationTimestamp
    @Column(nullable = false, columnDefinition = "DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3)")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false, columnDefinition = "DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3)")
    private LocalDateTime updatedAt;

    @Column(columnDefinition = "TINYINT(1) DEFAULT 0",
            nullable = false
    )
    private boolean memberQnAStat;

    public void setModifyData(MemberQnAModifyDTO modifyDTO, QnAClassification qnaClassification) {
        this.qnAClassification = qnaClassification;
        this.memberQnATitle = modifyDTO.title();
        this.memberQnAContent = modifyDTO.content();
    }

    public void setMemberQnAStat(boolean memberQnAStat) {
        this.memberQnAStat = memberQnAStat;
    }
}
