package com.hendisantika.controller;

import com.hendisantika.config.MockupData;
import com.midtrans.Config;
import com.midtrans.ConfigFactory;
import com.midtrans.httpclient.error.MidtransError;
import com.midtrans.service.MidtransCoreApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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
@RequiredArgsConstructor
public class CoreApiController {

    Config configOptions = Config.builder()
            .enableLog(true)
            .setIsProduction(isProduction)
            .setServerKey(sandboxServerKey)
            .setClientKey(sandboxClientKey)
            .build();

    private final MockupData dataMockup;
    /**
     * Midtrans java sample use `com.midtrans`: Using Midtrans Config class {@link Config}.
     * The config will use method from Object MidtransCoreAPI class
     * {@link MidtransCoreApi}
     * Sample use on Charge Controller @line 59
     */
    private final MidtransCoreApi coreApi = new ConfigFactory(configOptions).getCoreApi();

    // Core API Controller for fetch credit card transaction
    @PostMapping(value = "/cards/charge", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> charge(@RequestBody Map<String, String> cc) throws MidtransError {
        dataMockup.setPaymentType("credit_card");
        Map<String, String> creditCard = new HashMap<>(cc);
        creditCard.put("authentication", "true");
        dataMockup.creditCard(creditCard);
        Map<String, Object> body = new HashMap<>(dataMockup.initDataMock());

        coreApi.apiConfig().paymentAppendNotification("https://midtrans-java.herokuapp.com/notif/append1,https://midtrans-java.herokuapp.com/notif/append2");
        JSONObject object = coreApi.chargeTransaction(body);

        String result = object.toString();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}
