package com.vamonossoftware.simulator.s3;

import io.findify.s3mock.S3Mock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.file.Path;

@Slf4j
@Component
@ConditionalOnProperty(value = "simulator.s3.enabled", havingValue = "true")
public class S3ServerSimulator {

    private final int port;
    private final Path path;

    private S3Mock api;

    public S3ServerSimulator(@Value("${simulator.s3.port}") int port, @Value("${simulator.s3.path:}") Path path) {
        this.port = port;
        this.path = path;
    }

    @PostConstruct
    public void start() {
        S3Mock.Builder builder = new S3Mock.Builder().withPort(port);

        if (path != null) {
            builder.withFileBackend(path.toString());
        }
        else {
            builder.withInMemoryBackend();
        }

        api = builder.build();
        api.start();
    }

    @PreDestroy
    public void stop() {
        api.stop();
        api.shutdown();
    }
}
