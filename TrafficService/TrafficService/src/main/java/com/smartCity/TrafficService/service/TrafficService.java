package com.smartCity.TrafficService.service;

import com.smartCity.TrafficService.repository.TrafficRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


}
