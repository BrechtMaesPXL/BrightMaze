package be.pxl.services.services;

import be.pxl.services.domain.Log;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
public interface ILogService {
    List<Log> getAllLogsFromDatabase();
    String findLogById(Long id);
    List<Log> findAndStoreLogFiles() throws IOException;
    String extractServiceName(Path filePath);
}
