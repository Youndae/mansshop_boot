package com.example.mansshop_boot.service.unit.fixture;

import com.example.mansshop_boot.domain.dto.admin.out.AdminQnAListResponseDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyDTO;
import com.example.mansshop_boot.domain.dto.mypage.qna.in.QnAReplyInsertDTO;
import com.example.mansshop_boot.domain.entity.MemberQnA;
import com.example.mansshop_boot.domain.entity.ProductQnA;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class QnADTOUnitFixture {

    public static QnAReplyInsertDTO getQnAReplyInsertDTO() {
        return new QnAReplyInsertDTO(1L, "testReplyContent");
    }

    public static QnAReplyDTO getQnAReplyDTO() {
        return new QnAReplyDTO(1L, "testReplyContent");
    }

    public static List<AdminQnAListResponseDTO> getProductQnAResponseDTO(List<ProductQnA> productQnAList) {
        return productQnAList.stream()
                .map(v -> new AdminQnAListResponseDTO(
                        v.getId(),
                        v.getProduct().getClassification().getId(),
                        v.getProduct().getProductName(),
                        v.getMember().getUserId(),
                        LocalDateTime.now(),
                        true
                ))
                .limit(20)
                .toList();
    }

    public static List<AdminQnAListResponseDTO> getMemberQnAResponseDTO(List<MemberQnA> memberQnAList) {
        return memberQnAList.stream()
                .map(v -> new AdminQnAListResponseDTO(
                        v.getId(),
                        v.getQnAClassification().getQnaClassificationName(),
                        v.getMemberQnATitle(),
                        v.getMember().getUserId(),
                        LocalDate.now(),
                        true
                ))
                .limit(20)
                .toList();
    }
}
