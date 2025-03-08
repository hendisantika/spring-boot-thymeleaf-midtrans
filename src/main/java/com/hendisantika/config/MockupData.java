package com.hendisantika.config;

import org.springframework.stereotype.Component;

import java.sql.Timestamp;
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
 * Time: 09:25
 * To change this template use File | Settings | File Templates.
 */
@Component
public class MockupData {
    private List<String> listedPayment;
    private Map<String, String> creditCard;
    private String paymentType = "";

    public void enablePayments(List<String> listPayment) {
        listedPayment = new ArrayList<>();
        listedPayment.addAll(listPayment);
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public void creditCard(Map<String, String> params) {
        creditCard = new HashMap<>();
        creditCard.putAll(params);
    }

    public Map<String, Object> initDataMock() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Map<String, String> transDetail = new HashMap<>();
        transDetail.put("order_id", "JVM_WORKSHOP3_" + timestamp.getTime());
        transDetail.put("gross_amount", "250000");

        List<Map<String, String>> items = new ArrayList<>();
        Map<String, String> item1 = new HashMap<>();
        item1.put("id", "ID001");
        item1.put("price", "25000");
        item1.put("quantity", "1");
        item1.put("name", "Sendal Karet Rumahan");
        item1.put("brand", "Suwaslow");
        item1.put("category", "Sanitasi");
        item1.put("merchant_name", "SnowlID");

        Map<String, String> item2 = new HashMap<>();
        item2.put("id", "ID002");
        item2.put("price", "200000");
        item2.put("quantity", "1");
        item2.put("name", "Mantel Hujan");
        item2.put("brand", "Excel");
        item2.put("category", "Sanitasi");
        item2.put("merchant_name", "SnowlID");

        Map<String, String> item3 = new HashMap<>();
        item3.put("id", "ID003");
        item3.put("price", "25000");
        item3.put("quantity", "1");
        item3.put("name", "Sarung Tangan Karet");
        item3.put("brand", "Cap Anti Sobek");
        item3.put("category", "Sanitasi");
        item3.put("merchant_name", "SnowlID");

        items.add(item1);
        items.add(item2);
        items.add(item3);

        Map<String, Object> billingAddress = new HashMap<>();
        billingAddress.put("first_name", "Hendi");
        billingAddress.put("last_name", "Santika");
        billingAddress.put("email", "hendi@mailnesia.com");
        billingAddress.put("phone", "081321411800");
        billingAddress.put("address", "Jalan Buahbatu");
        billingAddress.put("city", "Jakarta Selatan");
        billingAddress.put("postal_code", "10120");
        billingAddress.put("country_code", "IDN");

        Map<String, Object> custDetail = new HashMap<>();
        custDetail.put("first_name", "Hendi");
        custDetail.put("last_name", "Santika");
        custDetail.put("email", "hendi@mailnesia.com");
        custDetail.put("phone", "081321411800");
        custDetail.put("billing_address", billingAddress);

        Map<String, Object> body = new HashMap<>();
        if (creditCard != null) {
            body.put("credit_card", creditCard);
        }
        body.put("transaction_details", transDetail);
        body.put("item_details", items);
        body.put("customer_details", custDetail);
        if (!paymentType.isEmpty()) {
            body.put("payment_type", paymentType);
        }
        if (listedPayment != null) {
            body.put("enabled_payments", listedPayment);
        }

        return body;
    }
}
