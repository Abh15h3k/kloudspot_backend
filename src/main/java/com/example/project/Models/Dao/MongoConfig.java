package com.example.project.Models.Dao;

import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

public class MongoConfig extends AbstractMongoClientConfiguration {

    @Override
    protected String getDatabaseName() {
        return "my_project";
    }

    @Override
    protected boolean autoIndexCreation() {
        return true;
    }
}
