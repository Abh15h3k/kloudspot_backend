package com.example.project.Models.Repository;

import com.example.project.Models.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    public Transaction findByPaymentId();
    public Transaction findByUserAadhar();
    public Transaction findByVehicleRegistration();
    public Transaction findByTripId();
}
