package be.pxl.services.controller;


import be.pxl.services.domain.Log;
import be.pxl.services.services.LogService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
@AllArgsConstructor
public class LogController {
    private static final Logger loging = LoggerFactory.getLogger(LogController.class);
    private final LogService logService;

    @GetMapping("/all")
    public ResponseEntity<List<Log>> getAllLogs() {
        loging.info("getAllLogs");
        try {
            logService.findAndStoreLogFiles();
            List<Log> logs = logService.getAllLogsFromDatabase();
            if (loging.isInfoEnabled()) {
                loging.info("getAllLogs returned {}", logs.size());
            }
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            loging.info("getAllLogs failed", e);
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getLogById(@PathVariable("id") Long id) {
        loging.info("getLogById {}", id);
        String logs = logService.findLogById(id);
        if (loging.isInfoEnabled()) {
            loging.info("getLogById returned length {}", logs.length());
        }
        return ResponseEntity.ok(logs);
    }
}

