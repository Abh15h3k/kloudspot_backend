package com.example.project.Models.Repository;

import java.util.List;

import com.example.project.Models.Trip;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRepository extends MongoRepository<Trip, String> {
//    List<Trip> find5ByUserAadhar(String userAadhar);
    List<Trip> findAllByUserAadhar(String userAadhar);
    List<Trip> findAllByVehicleRegistration(String vehicleRegistration);
}
