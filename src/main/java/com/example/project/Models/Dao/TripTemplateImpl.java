package com.example.project.Models.Dao;

import com.example.project.Models.MyUser;
import com.example.project.Models.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.querydsl.QuerydslUtils;
import org.springframework.stereotype.Component;

@Component
public class TripTemplateImpl implements TripTemplate {
    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public long countTripsWithDurationLessThan(double duration) {
        Query query = new BasicQuery("{'$expr':{'$lte':[{'$subtract':[{'$ifNull':['$endDateTime',{'$date':" + System.currentTimeMillis() + "}]},'$startDateTime']}," + (duration * 60 * 60 * 1000) + "]}}");
        long result = mongoTemplate.count(query, Trip.class);
        return result;
    }
}
