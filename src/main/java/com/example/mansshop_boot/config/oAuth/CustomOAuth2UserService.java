package com.example.mansshop_boot.config.oAuth;

import com.example.mansshop_boot.domain.dto.oAuth.*;
import com.example.mansshop_boot.domain.entity.Auth;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.enumuration.OAuthProvider;
import com.example.mansshop_boot.domain.enumuration.Role;
import com.example.mansshop_boot.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        if(registrationId.equals(OAuthProvider.GOOGLE.getKey()))
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        else if(registrationId.equals(OAuthProvider.NAVER.getKey()))
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        else if(registrationId.equals(OAuthProvider.KAKAO.getKey()))
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());

        String userId = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();
        Member existsData = memberRepository.findById(userId).orElse(null);
        OAuth2DTO oAuth2DTO;

        if(existsData == null) {
            Member member = Member.builder()
                    .userId(userId)
                    .userEmail(oAuth2Response.getEmail())
                    .userName(oAuth2Response.getName())
                    .provider(oAuth2Response.getProvider())
                    .build();

            Auth auth = Auth.builder()
                            .auth(Role.MEMBER.getKey())
                            .build();

            member.addAuth(auth);

            memberRepository.save(member);

            oAuth2DTO = OAuth2DTO.builder()
                    .userId(userId)
                    .username(oAuth2Response.getName())
                    .authList(Collections.singletonList(auth))
                    .nickname(null)
                    .build();
        }else {
            existsData.setUserEmail(oAuth2Response.getEmail());
            existsData.setUserName(oAuth2Response.getName());

            memberRepository.save(existsData);

            oAuth2DTO = OAuth2DTO.builder()
                    .userId(existsData.getUserId())
                    .username(existsData.getUserName())
                    .authList(existsData.getAuths())
                    .nickname(existsData.getNickname())
                    .build();
        }

        return new CustomOAuth2User(oAuth2DTO);
    }
}
