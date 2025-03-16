package com.example.mansshop_boot.service;

import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.config.customException.exception.CustomAccessDeniedException;
import com.example.mansshop_boot.domain.dto.response.UserStatusDTO;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalServiceImpl implements PrincipalService {

    private final MemberRepository memberRepository;

    /**
     *
     * @param principal
     *
     * Principal을 통해 사용자 닉네임을 조회해 반환
     */
    @Override
    public String getNicknameByPrincipal(Principal principal) {
        if(principal == null)
            return null;

        String uid = getUserNameOrNickname(principal.getName());

        if(uid == null)
            throw new CustomAccessDeniedException(ErrorCode.ACCESS_DENIED, ErrorCode.ACCESS_DENIED.getMessage());


        return uid;

    }

    /**
     *
     * @param userId
     *
     * 사용자 아이디를 통해 닉네임을 조회해 반환
     */
    @Override
    public String getNicknameByUserId(String userId) {
        return getUserNameOrNickname(userId);
    }

    /**
     *
     * @param userId
     *
     * 매개변수와 일치하는 사용자 정보를 조회. 닉네임이 존재한다면 닉네임을 반환하고
     * 존재하지 않는다면 사용자 이름을 반환.
     */
    private String getUserNameOrNickname(String userId) {

        if(userId == null)
            return null;

        Member member = memberRepository.findById(userId).orElse(null);

        if(member == null)
            return null;

        return member.getNickname() == null ? member.getUserName() : member.getNickname();
    }

    /**
     *
     * @param principal
     *
     * Principal을 통해 사용자 아이디를 반환.
     */
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

    /**
     *
     * @param principal
     *
     * Principal을 통해 사용자 닉네임을 조회하고 UserStatusDTO를 생성해 반환.
     */
    @Override
    public UserStatusDTO getUserStatusDTOByPrincipal(Principal principal) {

        return new UserStatusDTO(getNicknameByPrincipal(principal));
    }
}
