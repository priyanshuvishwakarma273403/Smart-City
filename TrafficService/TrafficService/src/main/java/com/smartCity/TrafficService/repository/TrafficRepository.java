package com.smartCity.TrafficService.repository;

import com.smartCity.TrafficService.entity.TrafficReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrafficRepository extends JpaRepository<TrafficRepository, Long> {

    // Latest reading for each sensor
    @Query("SELECT t FROM TrafficReading t WHERE t.timestamp = " +
            "(SELECT MAX(t2.timestamp) FROM TrafficReading t2 WHERE t2.sensorId = t.sensorId)")
    List<TrafficReading> findLatestReadingPerSensor();

    List<TrafficReading> findByLocationAndTimestampBetweenOrderByTimestampDesc(
            String location, LocalDateTime from, LocalDateTime to);

    @Query("SELECT t FROM TrafficReading t WHERE t.congestionLevel IN ('HIGH','CRITICAL') " +
            "AND t.timestamp >= :since ORDER BY t.timestamp DESC")
    List<TrafficReading> findCriticalZonesSince(@Param("since") LocalDateTime since);

    @Query("SELECT t.location, AVG(t.avgSpeed), AVG(t.vehicleCount) " +
            "FROM TrafficReading t WHERE t.timestamp >= :since " +
            "GROUP BY t.location ORDER BY AVG(t.avgSpeed) ASC")
    List<Object[]> findLocationStatsAggregated(@Param("since") LocalDateTime since);

    Optional<TrafficReading> findTopBySensorIdOrderByTimestampDesc(String sensorId);
}
