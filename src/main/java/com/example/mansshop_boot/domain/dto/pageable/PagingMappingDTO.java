package com.example.mansshop_boot.domain.dto.pageable;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class PagingMappingDTO{

    private Long totalElements;

    private boolean empty;

    private long number;

    private long totalPages;


    public PagingMappingDTO(Long totalElements, int page, int amount) {
        long totalPages = 0;

        if(totalElements != 0){
            totalPages = totalElements / amount;

            if(totalElements % amount != 0)
                totalPages += 1;
        }

        this.totalElements = totalElements;
        this.empty = totalElements == 0;
        this.number = page;
        this.totalPages = totalPages;
    }

    @Builder
    public PagingMappingDTO(Long totalElements, boolean empty, long number, long totalPages) {
        this.totalElements = totalElements;
        this.empty = empty;
        this.number = number;
        this.totalPages = totalPages;
    }
}
