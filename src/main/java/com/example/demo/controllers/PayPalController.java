package com.example.demo.controllers;

import com.example.demo.configurations.PayPalConfig;
import com.example.demo.dto.CreatePaymentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.util.*;

@RestController
@RequestMapping("/api/paypal")
public class PayPalController {

    @Autowired
    private PayPalConfig payPalConfig;

    private final RestTemplate restTemplate;
    private String cachedAccessToken;
    private long tokenExpirationTime;

    public PayPalController() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000); // 10 giây timeout kết nối
        factory.setReadTimeout(10000);    // 10 giây timeout đọc dữ liệu
        this.restTemplate = new RestTemplate(factory);
    }

    @PostMapping("/create-payment")
    public ResponseEntity<Map<String, String>> createPayment(@RequestBody CreatePaymentRequest request) {
        Map<String, String> responseMap = new HashMap<>();
        try {
            // Validate input
            if (request.getAmount() <= 0 || request.getCurrency() == null ||
                request.getReturnUrl() == null || request.getCancelUrl() == null) {
                responseMap.put("error", "Dữ liệu đầu vào không hợp lệ");
                return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
            }

            String accessToken = getAccessToken();
            String paypalApiUrl = getPaypalApiUrl("v2/checkout/orders");

            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            // Build request body
            Map<String, Object> orderRequest = buildOrderRequest(request);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(orderRequest, headers);
            ResponseEntity<Map> response = restTemplate.exchange(paypalApiUrl, HttpMethod.POST, entity, Map.class);

            // Handle response
            Map<String, Object> responseBody = response.getBody();
            if (!response.getStatusCode().is2xxSuccessful() || responseBody == null) {
                throw new Exception(getErrorMessage(responseBody));
            }

            String approvalUrl = extractLink(responseBody, "approve");
            String orderId = (String) responseBody.get("id");
            if (approvalUrl == null || orderId == null) {
                throw new Exception("Không tìm thấy approval_url hoặc orderId");
            }

            responseMap.put("approvalUrl", approvalUrl);
            responseMap.put("orderId", orderId);
            return new ResponseEntity<>(responseMap, HttpStatus.OK);

        } catch (RestClientException e) {
            responseMap.put("error", "Lỗi kết nối tới PayPal: " + e.getMessage());
            return new ResponseEntity<>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            responseMap.put("error", "Lỗi khi tạo giao dịch: " + e.getMessage());
            return new ResponseEntity<>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/confirm-payment")
    public ResponseEntity<Map<String, String>> confirmPayment(@RequestBody Map<String, String> request) {
        Map<String, String> responseMap = new HashMap<>();
        try {
            String orderId = request.get("orderId");
            if (orderId == null || orderId.trim().isEmpty()) {
                responseMap.put("error", "Thiếu orderId trong yêu cầu");
                return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
            }

            String accessToken = getAccessToken();
            String captureUrl = getPaypalApiUrl("v2/checkout/orders/" + orderId + "/capture");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(captureUrl, HttpMethod.POST, entity, Map.class);

            Map<String, Object> responseBody = response.getBody();
            if (!response.getStatusCode().is2xxSuccessful() || responseBody == null) {
                throw new Exception(getErrorMessage(responseBody));
            }

            String status = (String) responseBody.get("status");
            if (!"COMPLETED".equals(status)) {
                throw new Exception("Giao dịch không hoàn tất, trạng thái: " + status);
            }

            responseMap.put("status", status);
            responseMap.put("orderId", orderId);
            // Thêm logic lưu booking nếu cần
            return new ResponseEntity<>(responseMap, HttpStatus.OK);

        } catch (RestClientException e) {
            responseMap.put("error", "Lỗi kết nối tới PayPal: " + e.getMessage());
            return new ResponseEntity<>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            responseMap.put("error", "Lỗi khi xác nhận giao dịch: " + e.getMessage());
            return new ResponseEntity<>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/cancel-payment")
    public ResponseEntity<Map<String, String>> cancelPayment(@RequestBody Map<String, String> request) {
        Map<String, String> responseMap = new HashMap<>();
        try {
            String orderId = request.get("orderId");
            if (orderId == null || orderId.trim().isEmpty()) {
                responseMap.put("error", "Thiếu orderId trong yêu cầu");
                return new ResponseEntity<>(responseMap, HttpStatus.BAD_REQUEST);
            }

            // Không cần gọi API PayPal để hủy vì giao dịch chưa capture sẽ tự hết hạn
            responseMap.put("status", "CANCELLED");
            responseMap.put("message", "Giao dịch đã bị hủy bởi người dùng");
            responseMap.put("orderId", orderId);
            return new ResponseEntity<>(responseMap, HttpStatus.OK);

        } catch (Exception e) {
            responseMap.put("error", "Lỗi khi hủy giao dịch: " + e.getMessage());
            return new ResponseEntity<>(responseMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String getAccessToken() throws Exception {
        if (cachedAccessToken != null && System.currentTimeMillis() < tokenExpirationTime) {
            return cachedAccessToken;
        }

        String authUrl = getPaypalApiUrl("v1/oauth2/token");
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(payPalConfig.getClientId(), payPalConfig.getClientSecret());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>("grant_type=client_credentials", headers);
        ResponseEntity<Map> response = restTemplate.exchange(authUrl, HttpMethod.POST, entity, Map.class);

        Map<String, Object> responseBody = response.getBody();
        if (!response.getStatusCode().is2xxSuccessful() || responseBody == null) {
            throw new Exception("Không thể lấy access token: " + getErrorMessage(responseBody));
        }

        cachedAccessToken = (String) responseBody.get("access_token");
        Integer expiresIn = (Integer) responseBody.get("expires_in");
        tokenExpirationTime = System.currentTimeMillis() + (expiresIn - 60) * 1000; // Trừ 1 phút để an toàn
        return cachedAccessToken;
    }

    private String getPaypalApiUrl(String endpoint) {
        String baseUrl = payPalConfig.getMode().equals("sandbox")
            ? "https://api-m.sandbox.paypal.com/"
            : "https://api-m.paypal.com/";
        return baseUrl + endpoint;
    }

    private Map<String, Object> buildOrderRequest(CreatePaymentRequest request) {
        Map<String, Object> orderRequest = new HashMap<>();
        orderRequest.put("intent", "CAPTURE");

        List<Map<String, Object>> purchaseUnits = new ArrayList<>();
        Map<String, Object> purchaseUnit = new HashMap<>();
        Map<String, String> amount = new HashMap<>();
        amount.put("currency_code", request.getCurrency());
        amount.put("value", String.format(Locale.US, "%.2f", request.getAmount()));
        purchaseUnit.put("amount", amount);
        purchaseUnit.put("description", "Thanh toán đơn hàng đặt vé");
        purchaseUnits.add(purchaseUnit);
        orderRequest.put("purchase_units", purchaseUnits);

        Map<String, Object> applicationContext = new HashMap<>();
        applicationContext.put("return_url", request.getReturnUrl());
        applicationContext.put("cancel_url", request.getCancelUrl());
        orderRequest.put("application_context", applicationContext);

        return orderRequest;
    }

    private String extractLink(Map<String, Object> responseBody, String rel) {
        List<Map<String, String>> links = (List<Map<String, String>>) responseBody.get("links");
        if (links != null) {
            for (Map<String, String> link : links) {
                if (rel.equals(link.get("rel"))) {
                    return link.get("href");
                }
            }
        }
        return null;
    }

    private String getErrorMessage(Map<String, Object> responseBody) {
        if (responseBody == null) return "Không có phản hồi từ PayPal";
        String message = (String) responseBody.get("message");
        Object details = responseBody.get("details");
        return message != null ? message + (details != null ? ": " + details.toString() : "") : "Lỗi không xác định";
    }
}