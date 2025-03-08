package com.hendisantika.controller;

import com.hendisantika.config.MockupData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-thymeleaf-midtrans
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 9/9/23
 * Time: 09:36
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MockupData dataMockup;
    String clientKey = "YOR_CLIENT_KEY";
    @Value("${midtrans.clientkey}")
    private String sandboxClientKey;

    @GetMapping(value = "/")
    private String index() {
        return "index";
    }

    @GetMapping(value = "/mobile-sdk")
    private String mobileSdk() {
        return "mobile/mobile-sdk";
    }

    @GetMapping(value = "/api/core-api")
    public String coreApi(Model model) {
        Map<String, Object> objectMap = dataMockup.initDataMock();
        model.addAttribute("data", objectMap);
        return "coreapi/core-api";
    }

    @GetMapping(value = "/api/checkout")
    public String checkOut(@RequestParam(value = "paymentType") String typePayment,
                           Model model) {
        Map<String, Object> result = dataMockup.initDataMock();
        model.addAttribute("result", result);
        model.addAttribute("clientKey", clientKey);
        if (typePayment.equals("cc")) {
            return "coreapi/credit-card";
        } else if (typePayment.equals("gopay")) {
            return "coreapi/gopay";
        }
        return "redirect:/api/core-api";
    }

}
