package com.vamonossoftware.simulator.s3;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Slf4j
@Component
@ConditionalOnProperty(value = "simulator.s3.enabled", havingValue = "true")
public class S3ServerSimulatorConfig {
    private int port;
    private Path path;

    public S3ServerSimulatorConfig(@Value("${simulator.s3.port}")  int port, @Value("${simulator.s3.path:}")  Path path) {
        this.port = port;
        this.path=path;
    }

    public int getPort() {
        return port;
    }

    public Path getPath() {
        return path;
    }
}
