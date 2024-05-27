package com.example.mansshop_boot.config.security;

import com.example.mansshop_boot.config.customException.exception.CustomBadCredentialsException;
import com.example.mansshop_boot.config.customException.ErrorCode;
import com.example.mansshop_boot.domain.entity.Member;
import com.example.mansshop_boot.repository.MemberRepository;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    @Setter(onMethod_ = {@Autowired})
    private MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findById(username).orElse(null);

        if(member == null)
            throw new CustomBadCredentialsException(ErrorCode.BAD_CREDENTIALS, ErrorCode.BAD_CREDENTIALS.getMessage());
        else
            return new CustomUser(member);
    }
}
