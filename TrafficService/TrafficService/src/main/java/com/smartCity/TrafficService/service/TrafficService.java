package com.smartCity.TrafficService.service;

import com.smartCity.TrafficService.dto.TrafficDto;
import com.smartCity.TrafficService.entity.TrafficReading;
import com.smartCity.TrafficService.repository.TrafficRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrafficService {

    private final TrafficRepository trafficRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final ExecutorService dbWritePool =
            Executors.newFixedThreadPool(4, r -> {
                Thread t = new Thread(r, "traffic-db-writer");
                t.setDaemon(true);
                return t;
            });

    @Transactional
    @CacheEvict(value = {"traffic-summary", "critical-zones"} , allEntries = true)
    public void saveReading(TrafficDto.TrafficSensorData data){
        TrafficReading reading = TrafficReading.builder()
                .sensorId(data.getSensorId())
                .location(data.getLocation())
                .vehicleCount(data.getVehicleCount())
                .avgSpeed(data.getAvgSpeed())
                .congestionLevel(data.getCongestionLevel())
                .timestamp(data.getTimestamp())
                .build();

        TrafficReading saved = trafficRepository.save(reading);
        log.info("Saved traffic reading id={} for {}" , saved.getId(), data.getLocation());

        if("CRITICAL".equals(data.getCongestionLevel())){}

    }


}
