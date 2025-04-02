package com.example.mansshop_boot.domain.dto.cache;

import com.example.mansshop_boot.domain.dto.pageable.AdminOrderPageDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CacheRequest {
    private AdminOrderPageDTO pageDTO;

    private String listType;

    public CacheRequest(AdminOrderPageDTO pageDTO) {
        this.pageDTO = pageDTO;
    }
}
