package com.uni.task.er.repository;

import com.uni.task.er.model.User;
import com.uni.task.er.model.Webhook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WebhookRepository extends JpaRepository<Webhook, Long> {
    List<Webhook> findByUserId(long userId);
}