package com.example.project.Models.Dao;

import org.springframework.stereotype.Repository;

@Repository
public interface TripTemplate {
    public long countTripsWithDurationLessThan(double duration);
}
