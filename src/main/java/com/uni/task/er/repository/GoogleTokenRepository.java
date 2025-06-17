package com.uni.task.er.repository;

import com.uni.task.er.model.GoogleToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoogleTokenRepository extends JpaRepository<GoogleToken, Long> {
    
    Optional<GoogleToken> findByUserId(String userId);
    
    void deleteByUserId(String userId);
}
