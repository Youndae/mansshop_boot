package com.example.mansshop_boot.service;

import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.customException.exception.CustomAccessDeniedException;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalServiceImpl implements PrincipalService {

    private final MemberRepository memberRepository;

    @Override
    public String getPrincipalUid(Principal principal) {
        if(principal == null)
            return null;

        String uid = getUid(principal.getName());

        if(uid == null)
            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getMessage());


        return uid;

    }

    @Override
    public String getUidByUserId(String userId) {
        return getUid(userId);
    }

    private String getUid(String userId) {

        if(userId == null)
            return null;

        Member member = memberRepository.findById(userId).orElse(null);

        if(member == null)
            return null;

        return member.getNickname() == null ? member.getUserName() : member.getNickname();
    }

    @Override
    public String getUserIdByPrincipal(Principal principal) {

        String userId = null;

        try{
            userId = principal.getName();
        }catch (Exception e) {
            log.info("PrincipalService.getUserIdByPrincipal :: principal Error");
            e.printStackTrace();
            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getMessage());
        }

        return userId;
    }
}
