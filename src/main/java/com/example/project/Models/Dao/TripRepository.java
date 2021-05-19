package com.example.project.Models.Dao;

import java.util.List;

import com.example.project.Models.Count;
import com.example.project.Models.Trip;

import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRepository extends MongoRepository<Trip, String> {
//    List<Trip> find5ByUserAadhar(String userAadhar);
    List<Trip> findAllByUserAadhar(String userAadhar);
    List<Trip> findAllByVehicleRegistration(String vehicleRegistration);
    @Aggregation(pipeline = {"{$project: {_id: 0, month: {$month: '$startDateTime'}, year: {$year: '$startDateTime'}}}","{$match: {year: ?0}}","{$match: {month: ?1}}", "{$group: {_id: 0, count: {$sum:1}}}"})
    public AggregationResults<Count> countTripsInYearMonth(int year, int month);
}
