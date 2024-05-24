package com.jiandong.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Integer> {

    /**
     * DELIMITER ;;
     * CREATE PROCEDURE `GET_TOTAL_CARS_BY_MODEL`(IN model_in VARCHAR(50), OUT count_out INT)
     * BEGIN
     *     SELECT COUNT(*) into count_out from car WHERE model = model_in;
     * END;;
     * DELIMITER ;
     */
    @Procedure("GET_TOTAL_CARS_BY_MODEL")
    int getTotalCarsByModel(String model);

    /**
     * DELIMITER ;;
     * CREATE PROCEDURE `FIND_CARS_AFTER_YEAR`(IN year_in INT)
     * BEGIN
     * 	SELECT * FROM car WHERE year >= year_in ORDER BY year;
     * END;;
     * DELIMITER ;
     */
    @Query(value = "CALL FIND_CARS_AFTER_YEAR(:year_in);", nativeQuery = true)
    List<Car> findCarsAfterYear(@Param("year_in") Integer year_in);
}
