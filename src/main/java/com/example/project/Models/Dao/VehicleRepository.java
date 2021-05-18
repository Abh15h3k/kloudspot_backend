package com.example.project.Models.Dao;

import com.example.project.Models.Vehicle;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface VehicleRepository extends MongoRepository<Vehicle, String> {

}
