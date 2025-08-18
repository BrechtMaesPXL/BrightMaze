package be.pxl.services.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import be.pxl.services.domain.EventImage;

public interface EventImageRepository extends MongoRepository<EventImage, String> {
    
}
