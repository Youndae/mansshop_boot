package com.example.mansshop_boot.domain.enumuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Success {

   OK("OK");

   private final String message;
}
