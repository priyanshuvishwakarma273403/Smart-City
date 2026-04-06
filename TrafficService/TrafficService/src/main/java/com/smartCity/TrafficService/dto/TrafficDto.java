package com.smartCity.TrafficService.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class TrafficDto {

    public static class TrafficSensorData {
        private String sensorId;
        private String location;
        private int vehicleCount;
        private double avgSpeed;
        private String congestionLevel;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime timestamp;
    }
}
