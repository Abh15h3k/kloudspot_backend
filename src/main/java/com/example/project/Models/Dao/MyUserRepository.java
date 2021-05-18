package com.example.project.Models.Dao;

import com.example.project.Models.Count;
import com.example.project.Models.MyUser;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MyUserRepository extends MongoRepository<MyUser, String> {
    public Optional<MyUser> findByEmailId(String emailId);
    @Aggregation(pipeline = {"{$project: {_id: 0, month: {$month: '$joinDate'}, year: {$year: '$joinDate'}}}","{$match: {year: ?0}}","{$match: {month: ?1}}", "{$group: {_id: 0, count: {$sum:1}}}"})
    public AggregationResults<Count> countUsersJoinedOnMonth(int year, int month);
}
