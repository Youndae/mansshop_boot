package com.example.mansshop_boot.auth.oAuth;

import com.example.mansshop_boot.auth.oAuth.response.*;
import com.example.mansshop_boot.domain.entity.Auth;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.domain.enumeration.OAuthProvider;
import com.example.mansshop_boot.domain.enumeration.Role;
import com.example.mansshop_boot.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
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

        if(existsData == null) {
            Member member = OAuth2ResponseEntityConverter.toEntity(oAuth2Response, userId);
            member.addMemberAuth(
                            Auth.builder()
                                .auth(Role.MEMBER.getKey())
                                .build()
                    );

            memberRepository.save(member);

            existsData = member;
        }else {
            existsData.setUserEmail(oAuth2Response.getEmail());
            existsData.setUserName(oAuth2Response.getName());

            memberRepository.save(existsData);
        }

        OAuth2DTO oAuth2DTO = new OAuth2DTO(existsData);

        return new CustomOAuth2User(oAuth2DTO);
    }
}
