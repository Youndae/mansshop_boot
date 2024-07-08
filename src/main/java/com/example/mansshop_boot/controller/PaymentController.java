package com.example.mansshop_boot.controller;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 아임포트 결제 API 처리를 위한 컨트롤러
 */
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Value("${iamport.key}")
    private String apiKey;

    @Value("${iamport.secret}")
    private String apiSecret;

    private IamportClient iamportClient;

    @PostConstruct
    public void init() {
        this.iamportClient = new IamportClient(apiKey, apiSecret);
    }

    @PostMapping("/iamport/{imp_uid}")
    public IamportResponse<Payment> paymentIamportResponse(@PathVariable(name = "imp_uid") String imp_uid) throws IamportResponseException, IOException {

        return iamportClient.paymentByImpUid(imp_uid);
    }
}
