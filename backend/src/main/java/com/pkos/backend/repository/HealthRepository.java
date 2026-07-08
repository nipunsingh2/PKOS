package com.pkos.backend.repository;

import org.springframework.stereotype.Repository;

@Repository
public class HealthRepository {

    public String getHealthMessage() {
        return "PKOS Backend is Running!";
    }

}