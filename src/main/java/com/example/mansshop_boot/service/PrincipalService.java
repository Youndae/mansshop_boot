package com.example.mansshop_boot.service;

import java.security.Principal;

public interface PrincipalService {

    String getNicknameByPrincipal(Principal principal);

//    String getNicknameByUserId(String userId);

    String getUserIdByPrincipal(Principal principal);

//    UserStatusDTO getUserStatusDTOByPrincipal(Principal principal);
}
