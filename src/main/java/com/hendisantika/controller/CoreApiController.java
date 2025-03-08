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

    // Core API Controller for fetch Gopay transaction
    @PostMapping(value = "/gopay/charge", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> goPay() throws MidtransError {
        dataMockup.setPaymentType("gopay");

        Map<String, Object> body = new HashMap<>(dataMockup.initDataMock());

        coreApi.apiConfig().paymentOverrideNotification("https://midtrans-java.herokuapp.com/notif/override1,https://midtrans-java.herokuapp.com/notif/override2");
        JSONObject object = coreApi.chargeTransaction(body);
        String result = object.toString();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/transactions/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> checkTransaction(@RequestBody Map<String, String> transaction) throws MidtransError {
        JSONObject result = coreApi.checkTransaction(transaction.get("transaction_id"));
        return new ResponseEntity<>(result.toString(), HttpStatus.OK);
    }

    @PostMapping(value = "/transactions/approve", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> approveTransaction(@RequestBody Map<String, String> transaction) throws MidtransError {
        JSONObject result = coreApi.approveTransaction(transaction.get("transaction_id"));
        return new ResponseEntity<>(result.toString(), HttpStatus.OK);
    }

    @PostMapping(value = "/transactions/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> cancelTransaction(@RequestBody Map<String, String> transaction) throws MidtransError {
        JSONObject result = coreApi.cancelTransaction(transaction.get("transaction_id"));
        return new ResponseEntity<>(result.toString(), HttpStatus.OK);
    }

    @PostMapping(value = "/transactions/expire", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> expireTransaction(@RequestBody Map<String, String> transaction) throws MidtransError {
        JSONObject result = coreApi.expireTransaction(transaction.get("transaction_id"));
        return new ResponseEntity<>(result.toString(), HttpStatus.OK);
    }

    // Midtrans Handling Notification
    @PostMapping(value = "/notification", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> handleNotification(@RequestBody Map<String, Object> response) throws MidtransError {
        String notifResponse = null;
        if (!(response.isEmpty())) {
            //Get Order ID from notification body
            String orderId = (String) response.get("order_id");

            // Get status transaction to api with order id
            JSONObject transactionResult = coreApi.checkTransaction(orderId);

            String transactionStatus = (String) transactionResult.get("transaction_status");
            String fraudStatus = (String) transactionResult.get("fraud_status");

            notifResponse = "Transaction notification received. Order ID: " + orderId + ". Transaction status: " + transactionStatus + ". Fraud status: " + fraudStatus;
            log.info("notifResponse --> {}", notifResponse);

            if (transactionStatus.equals("capture")) {
                if (fraudStatus.equals("challenge")) {
                    // TODO set transaction status on your database to 'challenge' e.g: 'Payment status challenged. Please take action on your Merchant Administration Portal
                } else if (fraudStatus.equals("accept")) {
                    // TODO set transaction status on your database to 'success'
                }
            } else if (transactionStatus.equals("cancel") || transactionStatus.equals("deny") || transactionStatus.equals("expire")) {
                // TODO set transaction status on your database to 'failure'
            } else if (transactionStatus.equals("pending")) {
                // TODO set transaction status on your database to 'pending' / waiting payment
            }
        }
        return new ResponseEntity<>(notifResponse, HttpStatus.OK);
    }

    /*
     * Sample for append / override notifications
     */
    @PostMapping(value = "/notif/append1", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> appendNotif1(@RequestBody Map<String, Object> response) throws MidtransError {
        String append1 = "################# TEST - Received Append Notification 1 ###################";
        log.info("append1 --> {}", append1);
        return new ResponseEntity<>(append1, HttpStatus.OK);
    }

    @PostMapping(value = "/notif/append2", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> appendNotif2(@RequestBody Map<String, Object> response) throws MidtransError {
        String append2 = "################# TEST - Received Append Notification 2 ###################";
        log.info(" --> {}", append2);
        return new ResponseEntity<>(append2, HttpStatus.OK);
    }

    @PostMapping(value = "/notif/override1", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> overrideNotif1(@RequestBody Map<String, Object> response) throws MidtransError {
        String append1 = "################# TEST - Received Override Notification 1 ###################";
        log.info("append1 --> {}", append1);
        return new ResponseEntity<>(append1, HttpStatus.OK);
    }

    @PostMapping(value = "/notif/override2", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<String> overrideNotif2(@RequestBody Map<String, Object> response) throws MidtransError {
        String append2 = "################# TEST - Received Override Notification 2 ###################";
        log.info("append2 --> {}", append2);
        return new ResponseEntity<>(append2, HttpStatus.OK);
    }
}
