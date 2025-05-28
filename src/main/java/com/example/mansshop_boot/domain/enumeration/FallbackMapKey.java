package com.example.mansshop_boot.domain.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FallbackMapKey {

    ORDER("order");

    private final String key;
}
