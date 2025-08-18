package be.pxl.services.repository;

import be.pxl.services.domain.Chatsessie;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ChatSessieRepository extends MongoRepository<Chatsessie, String> {
    Optional<Chatsessie> findById(Long id);
}
