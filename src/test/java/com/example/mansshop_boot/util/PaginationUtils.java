package com.example.mansshop_boot.util;

public class PaginationUtils {

    public static int getTotalPages(int listSize, int amount) {
        return (int) Math.ceil((double) listSize / amount);
    }
}
