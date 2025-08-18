package be.pxl.services.domain;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class LogTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        Log log = new Log();
        log.setId(1L);
        log.setFileName("app.log");
        log.setFilePath("/var/log/app.log");
        log.setServiceName("myservice");
        Date now = new Date();
        log.setDateCreated(now);

        assertThat(log)
                .extracting(Log::getId, Log::getFileName, Log::getFilePath, Log::getServiceName, Log::getDateCreated)
                .containsExactly(1L, "app.log", "/var/log/app.log", "myservice", now);
    }

    @Test
    void testAllArgsConstructor() {
        Date now = new Date();
        Log log = new Log(2L, "error.log", "/var/log/error.log", "errorservice", now);

        assertThat(log)
                .extracting(Log::getId, Log::getFileName, Log::getFilePath, Log::getServiceName, Log::getDateCreated)
                .containsExactly(2L, "error.log", "/var/log/error.log", "errorservice", now);
    }

    @Test
    void testBuilder() {
        Date now = new Date();
        Log log = Log.builder()
                .id(3L)
                .fileName("debug.log")
                .filePath("/var/log/debug.log")
                .serviceName("debugservice")
                .dateCreated(now)
                .build();

        assertThat(log)
                .extracting(Log::getId, Log::getFileName, Log::getFilePath, Log::getServiceName, Log::getDateCreated)
                .containsExactly(3L, "debug.log", "/var/log/debug.log", "debugservice", now);
    }

    @Test
    void testEqualsAndHashCode() {
        Date now = new Date();
        Log log1 = Log.builder()
                .id(1L)
                .fileName("a.log")
                .filePath("/logs/a.log")
                .serviceName("svc")
                .dateCreated(now)
                .build();

        Log log2 = Log.builder()
                .id(1L)
                .fileName("a.log")
                .filePath("/logs/a.log")
                .serviceName("svc")
                .dateCreated(now)
                .build();

        Log log3 = Log.builder()
                .id(2L)
                .fileName("b.log")
                .filePath("/logs/b.log")
                .serviceName("svc2")
                .dateCreated(now)
                .build();

        assertThat(log1).satisfies(log -> {
            assertThat(log).isEqualTo(log2);
            assertThat(log).hasSameHashCodeAs(log2);
            assertThat(log).isNotEqualTo(log3);
        });
    }
}
