package com.example.mansshop_boot.annotation.swagger;

import com.example.mansshop_boot.config.customException.ExceptionEntity;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "성공"),
        @ApiResponse(responseCode = "401", description = "토큰 만료"
                , content = @Content(schema = @Schema(implementation = ExceptionEntity.class))
        ),
        @ApiResponse(responseCode = "403", description = "권한 정보 불일치"
                , content = @Content(schema = @Schema(implementation = ExceptionEntity.class))
        ),
        @ApiResponse(responseCode = "400", description = "존재하지 않는 데이터"
                , content = @Content(schema = @Schema(implementation = ExceptionEntity.class))
        ),
        @ApiResponse(responseCode = "500", description = "NullPointerException"
                , content = @Content(schema = @Schema(implementation = ExceptionEntity.class))
        ),
        @ApiResponse(responseCode = "800", description = "토큰 탈취"
                , content = @Content(schema = @Schema(implementation = ExceptionEntity.class))
        )
})
public @interface DefaultApiResponse {
}
