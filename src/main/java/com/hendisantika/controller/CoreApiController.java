package com.hendisantika.controller;

import com.midtrans.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import static com.hendisantika.entity.Constant.isProduction;
import static com.hendisantika.entity.Constant.sandboxClientKey;
import static com.hendisantika.entity.Constant.sandboxServerKey;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-thymeleaf-midtrans
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 9/10/23
 * Time: 11:10
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
@RestController
public class CoreApiController {

    Config configOptions = Config.builder()
            .enableLog(true)
            .setIsProduction(isProduction)
            .setServerKey(sandboxServerKey)
            .setClientKey(sandboxClientKey)
            .build();


}
