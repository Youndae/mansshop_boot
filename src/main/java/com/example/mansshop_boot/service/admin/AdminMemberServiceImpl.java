package com.example.mansshop_boot.service.admin;

import com.example.mansshop_boot.domain.dto.admin.in.AdminPostPointDTO;
import com.example.mansshop_boot.domain.dto.admin.out.AdminMemberDTO;
import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.enumeration.Result;
import com.example.mansshop_boot.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminMemberServiceImpl implements AdminMemberService {

    private final MemberRepository memberRepository;

    /**
     *
     * @param pageDTO
     *
     * 회원 목록 조회
     * 가입일이 최근인 기준으로 정렬
     */
    @Override
    public Page<AdminMemberDTO> getMemberList(AdminOrderPageDTO pageDTO) {
        Pageable pageable = PageRequest.of(pageDTO.page() - 1
                , pageDTO.amount()
                , Sort.by("createdAt").descending());

        return memberRepository.findMember(pageDTO, pageable);
    }

    /**
     *
     * @param pointDTO
     *
     * 회원 포인트 지급
     */
    @Override
    public String postPoint(AdminPostPointDTO pointDTO) {
        Member member = memberRepository.findById(pointDTO.userId()).orElseThrow(IllegalArgumentException::new);
        member.setMemberPoint(pointDTO.point());
        memberRepository.save(member);

        return Result.OK.getResultKey();
    }
}
