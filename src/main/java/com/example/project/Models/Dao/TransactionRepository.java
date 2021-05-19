package com.example.project.Models.Dao;

import com.example.project.Models.Count;
import com.example.project.Models.Transaction;
import com.example.project.Models.Trip;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    public Transaction findByPaymentId();
    public Transaction findByUserAadhar();
    public Transaction findByVehicleRegistration();
    public Transaction findByTripId();
    @Aggregation(pipeline = {"{$project: {_id: 0, amount: 1, month: {$month: '$datePaid'}, year: {$year: '$datePaid'}}}","{$match: {year: ?0}}","{$match: {month: ?1}}"})
    public List<Transaction> getTransactionsOffYearMonth(int year, int month);
}
