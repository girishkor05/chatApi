package com.example.app.repositories;

import com.example.app.models.QueueUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueueUserRepository extends MongoRepository<QueueUser, String> {
    List<QueueUser> findByMatchedFalseOrderByJoinedAt();

    List<QueueUser> findByMatchedFalse();
}
