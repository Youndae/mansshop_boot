package com.example.mansshop_boot.service.unit.fixture;

import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.entity.MemberQnA;
import com.example.mansshop_boot.domain.entity.MemberQnAReply;
import com.example.mansshop_boot.domain.entity.QnAClassification;

import java.util.ArrayList;
import java.util.List;

public class MemberQnAUnitFixture {

    public static List<MemberQnA> createMemberQnAList(List<Member> memberList, QnAClassification qnAClassification) {
        List<MemberQnA> memberQnAList = new ArrayList<>();

        for(int i = 0; i < memberList.size(); i++) {
            Member member = memberList.get(i);
            memberQnAList.add(createMemberQnA(i, member, qnAClassification));
        }

        return memberQnAList;
    }

    public static MemberQnA createMemberQnA(int id, Member member, QnAClassification qnAClassification) {
        return MemberQnA.builder()
                .id((long) id)
                .member(member)
                .qnAClassification(qnAClassification)
                .memberQnATitle(member.getUserId() + "'s QnA title")
                .memberQnAContent(member.getUserId() + "'s QnA content")
                .build();
    }

    public static List<MemberQnAReply> createMemberQnAReplyList(MemberQnA memberQnA) {
        List<MemberQnAReply> memberQnAReplyList = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            Member member;
            if(i % 2 == 0) {
                member = Member.builder().userId("admin").build();
            }else {
                member = memberQnA.getMember();
            }

            memberQnAReplyList.add(createMemberQnAReply(memberQnA, member));
        }

        return memberQnAReplyList;
    }

    public static MemberQnAReply createMemberQnAReply(MemberQnA memberQnA, Member member) {

        return MemberQnAReply.builder()
                .member(member)
                .memberQnA(memberQnA)
                .replyContent(member.getUserId() + "'s reply")
                .build();
    }
}
