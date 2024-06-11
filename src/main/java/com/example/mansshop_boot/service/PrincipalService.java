package com.example.mansshop_boot.service;

import java.security.Principal;

public interface PrincipalService {

    String getPrincipalUid(Principal principal);

    String getUidByUserId(String userId);

    String getUserIdByPrincipal(Principal principal);
}
