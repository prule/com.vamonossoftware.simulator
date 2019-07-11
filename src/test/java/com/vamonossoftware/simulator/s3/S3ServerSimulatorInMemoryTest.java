package com.vamonossoftware.simulator.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;

import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "/application-simulator-s3-memory.properties") // @ActiveProfiles({"simulator-s3"})
public class S3ServerSimulatorInMemoryTest {

    @Value("${simulator.s3.port}") private int port;

    private AmazonS3 client;

    @BeforeEach
    public void before() {

        AwsClientBuilder.EndpointConfiguration endpoint = new AwsClientBuilder.EndpointConfiguration("http://localhost:"+port, "us-west-2");

        client = AmazonS3ClientBuilder
                .standard()
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(endpoint)
                .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                .build();

    }

    @Test
    public void GIVEN_file_is_put_THEN_get_should_return_contents() throws Exception {

        final String expectedContents = "contents";
        final String bucketName = "test-bucket";
        final String filePath = "file/name";

        client.createBucket(bucketName);
        client.putObject(bucketName, filePath, expectedContents);

        try (S3ObjectInputStream inputStream = client.getObject(bucketName, filePath).getObjectContent()) {
            final String actualContents = IOUtils.toString(inputStream, Charset.forName("UTF-8"));
            assertThat(actualContents).isEqualTo(expectedContents);
        }

    }
}
