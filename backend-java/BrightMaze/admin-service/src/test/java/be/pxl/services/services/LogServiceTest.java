package be.pxl.services.services;

import be.pxl.services.domain.Log;
import be.pxl.services.exceptions.LogFileReadException;
import be.pxl.services.repository.LogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogServiceTest {

    @Mock
    private LogRepository logRepository;

    @InjectMocks
    private LogService logService;

    @Test
    void extractServiceName_ShouldReturnCorrectServiceName() {
        assertEquals("logs", logService.extractServiceName(Path.of("admin-service/logs/app.log")));
        assertEquals("root", logService.extractServiceName(Path.of("logs/error.log")));
        assertEquals("root", logService.extractServiceName(Path.of("system.log")));
        assertEquals("deep", logService.extractServiceName(Path.of("parent-service/logs/deep/nested.log")));
        assertEquals("root", logService.extractServiceName(Path.of("invalid-structure/file.log")));
    }

    @Test
    void findAndStoreLogFiles_ShouldIgnoreNonLogFiles(@TempDir Path tempDir) throws IOException {
        // Maak een .txt bestand aan (geen logbestand)
        Files.createFile(tempDir.resolve("note.txt"));

        // Injecteer het tempDir pad als logBaseDir
        ReflectionTestUtils.setField(logService, "logBaseDir", tempDir.toString());

        when(logRepository.findAll()).thenReturn(List.of());

        List<Log> logs = logService.findAndStoreLogFiles();

        verify(logRepository).deleteAll();
        verify(logRepository, never()).save(any());
        assertTrue(logs.isEmpty(), "Non-log files should be ignored");
    }

    @Test
    void findAndStoreLogFiles_ShouldUpdateExistingFiles(@TempDir Path tempDir) throws IOException {
        // Create a log file
        Files.createFile(tempDir.resolve("app.log"));

        // Inject the tempDir path as logBaseDir
        ReflectionTestUtils.setField(logService, "logBaseDir", tempDir.toString());

        // Set up an existing log in the repository with the same name
        Log existingLog = new Log();
        existingLog.setId(1L);
        existingLog.setFileName("app.log");
        existingLog.setFilePath(tempDir.resolve("app.log").toString());
        existingLog.setServiceName("root");

        when(logRepository.findAll()).thenReturn(List.of(existingLog));

        // Act
        List<Log> logs = logService.findAndStoreLogFiles();

        // Assert
        verify(logRepository).deleteAll();
        verify(logRepository).save(any(Log.class));
        assertEquals(1, logs.size(), "Should find 1 log file");
    }

    @Test
    void getLogFileById_ShouldReturnEmptyWhenNotExists() {
        // Arrange
        long id = 999L;
        when(logRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        String result = logService.findLogById(id);

        // Assert
        assertNull(result, "Should return null when log doesn't exist");
        verify(logRepository, times(1)).findById(id);
    }
    
    @Test
    void findLogById_ShouldReturnFileContent() {
        // Arrange
        long id = 1L;
        String expectedContent = "This is log file content";
        
        // Create a log with a valid file path
        Log log = new Log();
        log.setId(id);
        log.setFileName("app.log");
        log.setFilePath("/tmp/app.log"); // Mock path
        
        when(logRepository.findById(id)).thenReturn(Optional.of(log));
        
        // Use mockStatic for Files class
        try (var mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.readString(any(Path.class))).thenReturn(expectedContent);
            
            // Act
            String result = logService.findLogById(id);
            
            // Assert
            assertEquals(expectedContent, result);
            verify(logRepository).findById(id);
            mockedFiles.verify(() -> Files.readString(any(Path.class)));
        }
    }
    
    @Test
    void findLogById_ShouldThrowLogFileReadException() {
        // Arrange
        long id = 1L;
        
        Log log = new Log();
        log.setId(id);
        log.setFileName("app.log");
        log.setFilePath("/nonexistent/path/app.log");
        
        when(logRepository.findById(id)).thenReturn(Optional.of(log));
        
        // Act & Assert
        assertThrows(LogFileReadException.class, () -> logService.findLogById(id));
        verify(logRepository).findById(id);
    }
    
    @Test
    void getAllLogsFromDatabase_ShouldReturnAllLogs() {
        // Arrange
        List<Log> expectedLogs = new ArrayList<>();
        expectedLogs.add(createLog(1L, "app.log", "/logs/app.log", "logs"));
        expectedLogs.add(createLog(2L, "error.log", "/logs/error.log", "logs"));
        
        when(logRepository.findAll()).thenReturn(expectedLogs);
        
        // Act
        List<Log> result = logService.getAllLogsFromDatabase();
        
        // Assert
        assertEquals(expectedLogs.size(), result.size());
        assertEquals(expectedLogs, result);
        verify(logRepository).findAll();
    }
    
    @Test
    void findAndStoreLogFiles_ShouldHandleEmptyDirectory(@TempDir Path tempDir) throws IOException {
        // Arrange - empty directory
        ReflectionTestUtils.setField(logService, "logBaseDir", tempDir.toString());
        when(logRepository.findAll()).thenReturn(List.of());
        
        // Act
        List<Log> logs = logService.findAndStoreLogFiles();
        
        // Assert
        verify(logRepository).deleteAll();
        verify(logRepository, never()).save(any());
        assertTrue(logs.isEmpty(), "Empty directory should not find any log files");
    }

    private Log createLog(Long id, String fileName, String filePath, String serviceName) {
        Log log = new Log();
        log.setId(id);
        log.setFileName(fileName);
        log.setFilePath(filePath);
        log.setServiceName(serviceName);
        log.setDateCreated(new Date());
        return log;
    }
}
