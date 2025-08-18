package be.pxl.services.repository;

import be.pxl.services.domain.CordiLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CordiLocationRepository extends JpaRepository<CordiLocation, Integer> {
}
