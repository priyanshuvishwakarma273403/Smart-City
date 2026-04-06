package com.smartCity.TrafficService.kafka;

import com.smartCity.TrafficService.dto.TrafficDto;
import com.smartCity.TrafficService.service.TrafficService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrafficKafkaConsumer {
    private final TrafficService trafficService;

    @KafkaListener(
            topics = "traffic-sensor-data",
            groupId = "traffic-service-group",
            concurrency = "3"   // 3 threads parallel consume karenge
    )
    public void consumeTrafficData(
            @Payload TrafficDto.TrafficSensorData data,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ){
        try{
            log.debug("Received traffic event from partition={} offset={}", partition, offset);
            trafficService.saveReading(data);
        }catch (Exception e){
            log.error("Error processing traffic data for sensorId={}: {}",
                    data.getSensorId(), e.getMessage());
        }
    }
}
