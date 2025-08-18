package be.pxl.services.repository;

import be.pxl.services.domain.Video;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VideoRepository extends MongoRepository<Video, Long> {
}
