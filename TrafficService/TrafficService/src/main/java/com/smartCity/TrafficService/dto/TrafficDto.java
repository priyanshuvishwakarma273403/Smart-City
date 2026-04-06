package com.smartCity.TrafficService.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class TrafficDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrafficSensorData {
        private String sensorId;
        private String location;
        private int vehicleCount;
        private double avgSpeed;
        private String congestionLevel;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime timestamp;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrafficReadingResponse{
        private Long id;
        private String sensorId;
        private String location;
        private int vehicleCount;
        private double avgSpeed;
        private String congestionLevel;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime timestamp;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CityTrafficSummary{
        private int totalActiveSensors;
        private long criticalZones;
        private long highCongestionZones;
        private double avgSpeed;
        private List<TrafficReadingResponse> latestReadings;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime generatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class LocationStat{
        private String location;
        private double avgSpeed;
        private double avgVehicleCount;
        private String status;

    }

}
