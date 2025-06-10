package com.example.mansshop_boot.controller.admin;

import com.example.mansshop_boot.annotation.swagger.DefaultApiResponse;
import com.example.mansshop_boot.annotation.swagger.SwaggerAuthentication;
import com.example.mansshop_boot.domain.dto.rabbitMQ.FailedQueueDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseIdDTO;
import com.example.mansshop_boot.domain.dto.response.ResponseMessageDTO;
import com.example.mansshop_boot.service.admin.AdminFailedDataService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize(value = "hasRole('ROLE_ADMIN')")
public class AdminFailedDataController {

    private final AdminFailedDataService adminFailedDataService;

    /**
     *
     * 각 DLQ에 담긴 실패한 메시지 수량 반환.
     */
    @Operation(summary = "RabbitMQ 처리 중 실패한 메시지를 담고 있는 각 DLQ의 메시지 개수 반환. 실패 메시지가 존재하는 DLQ만 반환")
    @DefaultApiResponse
    @SwaggerAuthentication
    @GetMapping("/message")
    public ResponseEntity<List<FailedQueueDTO>> getFailedQueueCount() {
        List<FailedQueueDTO> responseDTO = adminFailedDataService.getFailedMessageList();

        return ResponseEntity.status(HttpStatus.OK)
                .body(responseDTO);
    }

    /**
     *
     * @param failedQueueDTO
     *
     * DLQ 메시지 재시도 요청
     * 반환된 데이터를 그대로 받아서 처리
     */
    @Operation(summary = "DLQ 데이터 재시도 요청")
    @DefaultApiResponse
    @SwaggerAuthentication
    @PostMapping("/message")
    public ResponseEntity<ResponseMessageDTO> retryDLQMessages(@RequestBody List<FailedQueueDTO> failedQueueDTO) {
        String responseMessage = adminFailedDataService.retryFailedMessages(failedQueueDTO);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }

    /**
     *
     * Redis에 저장된 실패한 주문 데이터 개수 조회
     */
    @GetMapping("/message/order")
    public ResponseEntity<ResponseIdDTO<Long>> getFailedOrderDataByRedis() {
        long response = adminFailedDataService.getFailedOrderDataByRedis();

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseIdDTO<>(response));
    }

    /**
     *
     * redis에 저장된 실패한 주문 데이터 재처리
     */
    @PostMapping("/message/order")
    public ResponseEntity<ResponseMessageDTO> retryRedisOrderMessage() {
        String responseMessage = adminFailedDataService.retryFailedOrderDataByRedis();

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseMessageDTO(responseMessage));
    }
}
