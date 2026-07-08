package com.pkos.backend.service;

import org.springframework.stereotype.Service;

@Service
public class HealthService {

    public String getHealthStatus() {
        return "PKOS Backend is Running!";
    }

}