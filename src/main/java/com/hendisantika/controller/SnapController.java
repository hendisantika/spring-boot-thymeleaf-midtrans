package com.hendisantika.controller;

import com.hendisantika.config.MockupData;
import com.midtrans.Midtrans;
import com.midtrans.httpclient.SnapApi;
import com.midtrans.httpclient.error.MidtransError;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-thymeleaf-midtrans
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 9/9/23
 * Time: 09:24
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequiredArgsConstructor
public class SnapController {
    //Data transaction Mockup
    private final MockupData dataMockup;

    @Value("${midtrans.serverkey}")
    private String sandboxServerKey;

    @Value("${midtrans.clientkey}")
    private String sandboxClientKey;

    @GetMapping(value = "/snap")
    public String snap(Model model) {
        Map<String, Object> objectMap = dataMockup.initDataMock();
        model.addAttribute("data", objectMap);
        return "snap/snap";
    }

    @PostMapping(value = "/snap/check-out")
    public String checkout(@RequestParam(value = "enablePay", required = false) List<String> listPay,
                           @RequestParam(value = "snapType") String snapType,
                           Model model) throws MidtransError {

        Midtrans.clientKey = sandboxClientKey;
        Midtrans.serverKey = sandboxServerKey;
        // Get ClientKey from Midtrans Configuration class
        String clientKey = Midtrans.getClientKey();

        // New Map Object for JSON raw request body
        Map<String, Object> requestBody = new HashMap<>();

        // Add enablePayment from @RequestParam to dataMockup
        List<String> paymentList = new ArrayList<>();
        if (listPay != null) {
            paymentList.addAll(listPay);
        }
        Map<String, String> creditCard = new HashMap<>();
        creditCard.put("secure", "true");
        dataMockup = new MockupData();
        dataMockup.creditCard(creditCard);
        dataMockup.enablePayments(paymentList);

        // PutAll data mockUp to requestBody
        requestBody.putAll(dataMockup.initDataMock());

        // send data to frontEnd snapPopUp
        if (snapType.equals("snap")) {
            model.addAttribute("result", requestBody);
            model.addAttribute("clientKey", clientKey);
            // token object getData token to API with createTransactionToken() method return String token
            model.addAttribute("transactionToken", SnapApi.createTransactionToken(requestBody));
            return "snap/check-out";

            // send data to frontEnd redirect-url
        } else {
            model.addAttribute("result", requestBody);
            // redirectURL get url redirect to API with createTransactionRedirectUrl() method, with return String url redirect
            model.addAttribute("redirectURL", SnapApi.createTransactionRedirectUrl(requestBody));
            return "snap/check-out";
        }
    }
}
