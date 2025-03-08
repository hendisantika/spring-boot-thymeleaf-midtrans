package com.hendisantika.controller;

import com.midtrans.Config;
import com.midtrans.ConfigFactory;
import com.midtrans.httpclient.error.MidtransError;
import com.midtrans.service.MidtransIrisApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import static com.hendisantika.entity.Constant.sandboxCreatorKey;

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
@Controller
@Slf4j
public class IrisController {

    MidtransIrisApi irisApi = new ConfigFactory(
            new Config(sandboxCreatorKey,
                    null,
                    false))
            .getIrisApi();

    public IrisController() {
        String irisSandboxMerchantKey = "YOUR-SANDBOX-MERCHANT-KEY";
        irisApi.apiConfig().setIRIS_MERCHANT_KEY(irisSandboxMerchantKey);
    }

    @GetMapping(value = "/iris/ping")
    public ResponseEntity<String> ping() throws MidtransError {
        String result = irisApi.ping();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
