package com.hendisantika.controller;

import com.midtrans.Config;
import com.midtrans.ConfigFactory;
import com.midtrans.httpclient.error.MidtransError;
import com.midtrans.service.MidtransIrisApi;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.hendisantika.entity.Constant.sandboxApproverKey;
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

    @GetMapping(value = "/iris/index")
    public String iris(Model model) throws MidtransError {
        LocalDate localDate = LocalDate.now();
        String fromDate = DateTimeFormatter.ofPattern("yyy-MM-dd").format(localDate);
        String toDate = DateTimeFormatter.ofPattern("yyy-MM-dd").format(localDate);

        JSONObject currentBalance = irisApi.getBalance();
        JSONArray transactionHistory = irisApi.getTransactionHistory(fromDate, toDate);

        List<Map<String, Object>> rows = new ArrayList<>();
        List<String> headers = Arrays.asList("Name", "Amount", "Type", "Reference", "Account", "Bank", "Status");
        for (int i = 0; i < transactionHistory.length(); i++) {
            Map<String, Object> value = new HashMap<>();
            if (transactionHistory.getJSONObject(i).has("beneficiary_name")) {
                value.put("Name", transactionHistory.getJSONObject(i).getString("beneficiary_name"));
            }
            if (transactionHistory.getJSONObject(i).has("amount")) {
                value.put("Amount", transactionHistory.getJSONObject(i).getString("amount"));
            }
            if (transactionHistory.getJSONObject(i).has("type")) {
                value.put("Type", transactionHistory.getJSONObject(i).getString("type"));
            }
            if (transactionHistory.getJSONObject(i).has("reference_no")) {
                value.put("Reference", transactionHistory.getJSONObject(i).getString("reference_no"));
            }
            if (transactionHistory.getJSONObject(i).has("beneficiary_account")) {
                value.put("Account", transactionHistory.getJSONObject(i).getString("beneficiary_account"));
            }
            if (transactionHistory.getJSONObject(i).has("account")) {
                value.put("Bank", transactionHistory.getJSONObject(i).getString("account"));
            }
            if (transactionHistory.getJSONObject(i).has("status")) {
                value.put("Status", transactionHistory.getJSONObject(i).getString("status"));
            }
            rows.add(i, value);
        }
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("headers", headers);
        model.addAttribute("rows", rows);
        model.addAttribute("balance", currentBalance.getString("balance"));
        return "iris/index";
    }

    private static String getRandomNumberString() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }

    @GetMapping(value = "/iris/payouts")
    public String payout(Model model) throws MidtransError {
        JSONArray response = irisApi.getBeneficiaries();
        List<String> listNameBeneficiaries = new ArrayList<>();
        if (response != null) {
            int len = response.length();
            for (int i = 0; i < len; i++) {
                listNameBeneficiaries.add((response.getJSONObject(i).getString("name")));
            }
        }
        model.addAttribute("amounts", getRandomNumberString());
        model.addAttribute("names", listNameBeneficiaries);
        return "iris/create-payout";
    }

    @PostMapping(value = "/iris/payouts/create")
    public ResponseEntity<String> createPayout(@RequestBody Map<String, String> params) throws MidtransError {
        ArrayList<Map<String, String>> payoutBeneficiaries = new ArrayList<>();
        payoutBeneficiaries.add(getBeneficiaries(params));

        Map<String, Object> payouts = new HashMap<>();
        payouts.put("payouts", payoutBeneficiaries);
        irisApi.apiConfig().setServerKey(sandboxCreatorKey);
        JSONObject result = irisApi.createPayouts(payouts);

        return new ResponseEntity<>(result.toString(), HttpStatus.OK);
    }

    private Map<String, String> getBeneficiaries(Map<String, String> params) throws MidtransError {
        irisApi.apiConfig().setServerKey(sandboxCreatorKey);
        JSONArray result = irisApi.getBeneficiaries();
        Map<String, String> beneficiary = new HashMap<>();
        if (result != null) {
            for (int i = 0; i < result.length(); i++) {
                String resultName = result.getJSONObject(i).getString("name");
                String paramName = params.get("name");
                if (resultName.equals(paramName)) {
                    beneficiary.put("beneficiary_name", result.getJSONObject(i).getString("name"));
                    beneficiary.put("beneficiary_account", result.getJSONObject(i).getString("account"));
                    beneficiary.put("beneficiary_bank", result.getJSONObject(i).getString("bank"));
                    beneficiary.put("beneficiary_email", result.getJSONObject(i).getString("email"));
                    beneficiary.put("amount", params.get("amount"));
                    beneficiary.put("notes", params.get("notes"));
                    break;
                }
            }
        }
        return beneficiary;
    }

    @PostMapping(value = "/iris/payouts/approve")
    public ResponseEntity<String> approve(@RequestBody Map<String, Object> params) throws MidtransError {
        irisApi.apiConfig().setServerKey(sandboxApproverKey);
        JSONObject result = irisApi.approvePayouts(params);
        return new ResponseEntity<>(result.toString(), HttpStatus.OK);
    }


    @PostMapping(value = "/iris/payouts/reject")
    public ResponseEntity<String> reject(@RequestBody Map<String, Object> params) throws MidtransError {
        irisApi.apiConfig().setServerKey(sandboxApproverKey);
        JSONObject result = irisApi.rejectPayouts(params);
        return new ResponseEntity<>(result.toString(), HttpStatus.OK);
    }

    @PostMapping(value = "/iris/payouts/details")
    public ResponseEntity<String> payoutDetails(@RequestBody Map<String, String> params) throws MidtransError {
        String referenceNo = params.get("reference_no");
        irisApi.apiConfig().setServerKey(sandboxCreatorKey);
        JSONObject result = irisApi.getPayoutDetails(referenceNo);
        return new ResponseEntity<>(result.toString(), HttpStatus.OK);
    }

    @PostMapping(value = "/iris/notifications")
    public ResponseEntity<String> notifications(@RequestHeader("Iris-Signature") String irisSignature, HttpEntity<String> httpEntity) {
        // Get json body request
        String jsonBodyRequest = httpEntity.getBody();

        // Create hash Signature from payload + iris-merchant-key
        String hashParam = jsonBodyRequest + irisApi.apiConfig().getIRIS_MERCHANT_KEY();
        String hashSignature = sha512(hashParam);

        JSONObject jsonObject = new JSONObject(jsonBodyRequest);

        // 1. Validate the value header Iris-Signature
        if (irisSignature.equals(hashSignature)) {

            if (jsonObject.getString("status").equals("approved")) {
                // TODO set payouts status on your database to 'approved' e.g: STATUS 'Payout status approved
                log.info("IRIS NOTIFICATION RECEIVED  : STATUS APPROVED");
            } else if (jsonObject.getString("status").equals("rejected")) {
                // TODO set payouts status on your database to 'rejected'
                log.info("IRIS NOTIFICATION RECEIVED  : STATUS REJECTED");
            } else if (jsonObject.getString("status").equals("processed")) {
                // TODO set payouts status on your database to 'processed'
                log.info("IRIS NOTIFICATION RECEIVED  : STATUS PROCESSED");
            } else if (jsonObject.getString("status").equals("completed")) {
                // TODO set payouts status on your database to 'completed'
                log.info("IRIS NOTIFICATION RECEIVED  : STATUS COMPLETED");
            } else if (jsonObject.getString("status").equals("failed")) {
                // TODO set payouts status on your database to 'failed'
                log.info("IRIS NOTIFICATION RECEIVED  : STATUS FAILED");
            } else if (jsonObject.getString("status").equals("topup")) {
                // TODO set topup status on your database to 'topup'
                log.info("IRIS NOTIFICATION RECEIVED  : TOPUP");
            }

            /*
            For testing purpose from Iris dashboard
             */
            else if (jsonObject.getString("status").equals("test")) {
                log.info("IRIS NOTIFICATION RECEIVED : STATUS TEST");
            }
        } else {
            log.info("IRIS NOTIFICATION RECEIVED : SIGNATURE NOT VALID");
            return new ResponseEntity<>("SIGNATURE NOT VALID", HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    private static String sha512(String input) {
        try {
            // getInstance() method is called with algorithm SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            /*
            digest() method is called to calculate message digest of the input string.
            returned as array of byte
             */
            byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String signature = no.toString(16);

            // Add preceding 0s to make it 32 bit
            while (signature.length() < 32) {
                signature = "0" + signature;
            }
            // return the HashString
            return signature;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


}
