package com.andersen.pc.portal.repository;

import com.andersen.pc.common.model.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    @Query("SELECT p.awsKey FROM Photo p WHERE p.trip.user.id = :userId")
    List<String> findAwsKeysByUserId(@Param("userId") Long userId);

    @Query("SELECT p.awsKey FROM Photo p WHERE p.trip.id = :tripId")
    List<String> findAwsKeysByTripId(@Param("tripId") Long tripId);
}
