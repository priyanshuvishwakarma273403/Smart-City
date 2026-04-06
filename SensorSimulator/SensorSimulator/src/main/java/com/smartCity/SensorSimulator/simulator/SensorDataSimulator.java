package com.smartCity.SensorSimulator.simulator;

import com.smartCity.SensorSimulator.model.TrafficSensorData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SensorDataSimulator {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Random random = new Random();

    private final ExecutorService sensorThreadPool =
            Executors.newFixedThreadPool(3, r -> {
                Thread t = new Thread(r);
                t.setName("sensorThread-" + t.getId());
                t.setDaemon(true);
                return t;
            });

    private static final List<String> TRAFFIC_LOCATIONS = Arrays.asList(
            "Connaught Place", "Karol Bagh", "Lajpat Nagar",
            "Dwarka Sector 21", "Nehru Place", "Chandni Chowk"
    );

    private static final List<String> AIR_LOCATIONS = Arrays.asList(
            "Anand Vihar", "RK Puram", "Punjabi Bagh",
            "Jahangirpuri", "Okhla", "Rohini"
    );

    private static final String[] SLOT_NUMBERS = {"01", "02", "03", "04", "05"};


    @Scheduled(fixedDelay = 3000)
    public void simulateTrafficSensors(){
        CompletableFuture.runAsync(() -> {
            TRAFFIC_LOCATIONS.forEach(location -> {
                TrafficSensorData data = generateTrafficData(location);
                KafkaTemplate.send("traffic-sensor-data",
                        data.getSensorId(), data);
                log.info("[TRAFFIC] {} → vehicles: {}, speed: {} km/h, congestion: {}",
                        location, data.getVehicleCount(),
                        String.format("%.1f", data.getAvgSpeed()),
                        data.getCongestionLevel());
            });
        }, sensorThreadPool);
    }

    public void simulateAirQualitySensors(){
      CompletableFuture.runAsync(() -> {

      } )
    }


}
