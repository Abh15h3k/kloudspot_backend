package com.example.project.Models.Repository;

import com.example.project.Models.Vehicle;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface VehicleRepository extends MongoRepository<Vehicle, String> {

}
