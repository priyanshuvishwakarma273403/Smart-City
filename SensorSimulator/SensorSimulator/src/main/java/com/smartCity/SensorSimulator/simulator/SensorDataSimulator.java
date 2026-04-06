package com.smartCity.SensorSimulator.simulator;

import com.smartCity.SensorSimulator.model.AirQualitySensorData;
import com.smartCity.SensorSimulator.model.ParkingSensorData;
import com.smartCity.SensorSimulator.model.TrafficSensorData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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

    private static final List<String[]> PARKING_ZONES = Arrays.asList(
            new String[]{"ZONE-A", "Connaught Place"},
            new String[]{"ZONE-B", "Karol Bagh"},
            new String[]{"ZONE-C", "Lajpat Nagar"}
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

    @Scheduled(fixedDelay = 5000)
    public void simulateAirQualitySensors(){
      CompletableFuture.runAsync(() -> {
          AIR_LOCATIONS.forEach(location -> {
              AirQualitySensorData data = generateAirQualityData(location);
              KafkaTemplate.send("airquality-sensor-data",
                      data.getSensorId(), data);
              log.info("[AIR] {} → AQI: {}, PM2.5: {}, quality: {}",
                      location, data.getAqi(),
                      String.format("%.1f", data.getPm25()),
                      data.getQualityLabel());
          });

      } , sensorThreadPool);
    }

    @Scheduled(fixedDelay = 8000)
    public void simulateParkingSensors(){
        CompletableFuture.runAsync(() -> {
            String[] zone = PARKING_ZONES.get(random.nextInt(PARKING_ZONES.size()));
            String slotNum = SLOT_NUMBERS[random.nextInt(SLOT_NUMBERS.length)];
            boolean isOccupied = random.nextBoolean();

            ParkingSensorData data = ParkingSensorData.builder()
                    .sensorId("PARK-" + zone[0] + "-" + slotNum)
                    .zoneId(zone[0])
                    .slotNumber(zone[0].replace("ZONE-", "") + "-" +slotNum)
                    .location(zone[1])
                    .occupied(isOccupied)
                    .vehicleNumber(isOccupied ? "VEHICLE_IN" : "VEHICLE_OUT")
                    .timestamp(LocalDateTime.now())
                    .build();

            KafkaTemplate.send("parking-sensor-data", data.getSensorId(), data);
            log.info("[PARKING] {} slot {} → {}",
                    zone[1], data.getSlotNumber(), data.getEventType());
        }, sensorThreadPool);
    }

    private TrafficSensorData generateTrafficData(String location){

        int hour = LocalDateTime.now().getHour();
        boolean isPeakHour  = (hour >= 8 && hour <= 10) || (hour >= 17 && hour <= 20);

        int vehicleCount = isPeakHour
                ? 80 + random.nextInt(120)
                : 10 + random.nextInt(60);

        double avgSpeed = isPeakHour
                ? 5 + random.nextDouble() * 25
                : 30 + random.nextDouble() * 40;

        String congestion = calculateCongestion(vehicleCount, avgSpeed);

        return TrafficSensorData.builder()
                .sensorId("TRAF-" + location.replaceAll("\\s+", "").toUpperCase().substring(0, 4)
                +"-" + String.format("%03",random.nextInt(999)))
                .location(location)
                .vehicleCount(vehicleCount)
                .avgSpeed(Math.round(avgSpeed * 10.0) / 10.0)
                .congestionLevel(congestion)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private String calculateCongestion(int vehicles, double speed){
        if(vehicles > 150 || speed < 10) return "CRITICAL";
        if(vehicles > 100 || speed <20) return "HIGH";
        if(vehicles > 50 || speed < 40) return "MEDIUM";

        return "LOW";
    }

    private AirQualitySensorData generateAirQualityData(String location){

        int aqi = 50 + random.nextInt(400);
        double pm25 = 10 + random.nextDouble() * 200;
        double pm10 =pm25 * 1.5 + random.nextDouble() * 50;
        double co2 = 400 + random.nextDouble() * 600;
        String label = aqiLabel(aqi);

        return AirQualitySensorData.builder()
                .sensorId("AIR-" +location.replaceAll("\\s+", "").toUpperCase().substring(0, 4)
                        + "-" + String.format("%03d",random.nextInt(999)))
                .location(location)
                .aqi(aqi)
                .pm25(Math.round(pm25 * 10.0) / 10.0)
                .pm10(Math.round(pm10 * 10.0) / 10.0)
                .co2(Math.round(co2 * 10.0) / 10.0)
                .qualityLabel(label)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private String aqiLabel(int aqi){
        if(aqi <= 50) return "GOOD";
        if(aqi <= 100) return "MODERATE";
        if(aqi <= 200) return "UNHEALTHY";
        if(aqi <= 300) return "VERY_UNHEALTHY";
        return "HAZARDOUS";
    }

    private String generateVehicleNumber(){
        String[] states = {"DL", "UP", "HR", "MH", "RJ"};
        String state = states[random.nextInt(states.length)];
        int district = 1 + random.nextInt(99);
        char c1 = (char) ('A' + random.nextInt(26));
        char c2 = (char) ('A' + random.nextInt(26));
        int num = 1000 + random.nextInt(9000);
        return String.format("%s%02d%c%c%d", state, district, c1, c2, num);
    }

}
