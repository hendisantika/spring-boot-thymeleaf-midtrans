package com.hendisantika.controller;

import com.hendisantika.config.MockupData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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


}
