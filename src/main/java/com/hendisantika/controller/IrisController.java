package com.hendisantika.controller;

import com.midtrans.Config;
import com.midtrans.ConfigFactory;
import com.midtrans.httpclient.error.MidtransError;
import com.midtrans.service.MidtransIrisApi;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.hendisantika.entity.Constant.sandboxCreatorKey;
import static com.midtrans.httpclient.IrisApi.getBeneficiaries;

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
}
