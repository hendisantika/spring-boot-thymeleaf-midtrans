package com.hendisantika.controller;

import com.hendisantika.config.MockupData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
    public String home() {
        return index();
    }

}
