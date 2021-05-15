package com.example.project.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "transaction")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    private String orderId;

    @Indexed
    private String paymentId;

    private String tripId;
    private String userAadhar;
    private String vehicleRegistration;
    private Double amount;
    private LocalDateTime dateInitiated;
    private LocalDateTime datePaid;
    private TransactionStatus status;
}
