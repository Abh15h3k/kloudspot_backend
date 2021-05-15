package com.example.project.Util;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class Payment {
    private RazorpayClient razorpayClient;
    public Payment() {
        try {
            this.razorpayClient = new RazorpayClient("rzp_test_js32jG7gWjkxYV", "i8PyKm47AOzshBSlCU950a93");
        } catch (RazorpayException razorpayException) {
            System.out.println("Failed to create a RazorpayClient: " + razorpayException.getMessage());
        }
    }

    public String createOrder(double amount) {
        JSONObject options = new JSONObject();
        options.put("amount", 20000); // Note: The amount should be in paise.
        options.put("currency", "INR");
        options.put("receipt", "txn_123456");
        Order order = null;
        try {
            order = razorpayClient.Orders.create(options);
        } catch (RazorpayException razorpayException) {
            System.out.println("Failed to create a RazorPay Order " + razorpayException.getMessage());
            return null;
        }
        return order.get("id");
    }

    public boolean completed(String orderId) {

        Order order = null;
        try {
            order = razorpayClient.Orders.fetch(orderId);
        } catch (RazorpayException razorpayException) {
            System.out.println("Failed to get RazorPay Order " + razorpayException.getMessage());
        }

        String orderStatus = order.get("status");

        return orderStatus.equalsIgnoreCase("paid");
    }
}
