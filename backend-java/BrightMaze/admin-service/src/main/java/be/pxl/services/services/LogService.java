package be.pxl.services.services;

import be.pxl.services.domain.Log;
import be.pxl.services.exceptions.LogFileReadException;
import be.pxl.services.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService implements ILogService {

    private final LogRepository logRepository;

    @Value("${log.base.dir:}") // Deze waarde kun je instellen in application.properties of testen
    private String logBaseDir;

    @Override
    public List<Log> findAndStoreLogFiles() throws IOException {
        logRepository.deleteAll();

        Path rootDir = Paths.get(logBaseDir);
        Files.walkFileTree(rootDir, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".log")) {
                    Log log = Log.builder()
                            .fileName(file.getFileName().toString())
                            .filePath(file.toString())
                            .serviceName(extractServiceName(file))
                            .dateCreated(new Date(attrs.creationTime().toMillis()))
                            .build();
                    logRepository.save(log);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        return logRepository.findAll();
    }

    @Override
    public List<Log> getAllLogsFromDatabase() {
        return logRepository.findAll();
    }

    @Override
    public String findLogById(Long id) {
        Log log = logRepository.findById(id).orElse(null);
        if (log == null) {
            return null;
        }

        Path filePath = Paths.get(log.getFilePath());
        try {
            return Files.readString(filePath);
        } catch (IOException e) {
            throw new LogFileReadException("Failed to read log file content from: " + filePath, e);
        }
    }

    @Override
    public String extractServiceName(Path filePath) {
        Path parentDir = filePath.getParent();
        if (parentDir != null && parentDir.getParent() != null) {
            return parentDir.getFileName().toString();
        }
        return "root";
    }
}
