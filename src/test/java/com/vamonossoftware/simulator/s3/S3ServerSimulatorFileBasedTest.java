package com.vamonossoftware.simulator.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.vamonossoftware.core.DirectoryManager;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import static org.assertj.core.api.Assertions.assertThat;

public class S3ServerSimulatorFileBasedTest {

    private AmazonS3 client;

    private DirectoryManager directoryManager;

    private S3ServerSimulator simulator;

    @BeforeEach
    public void before() {
        directoryManager = new DirectoryManager();

        final int port = 8011;

        simulator = new S3ServerSimulator(port, directoryManager.directoryPath());
        simulator.start();

        AwsClientBuilder.EndpointConfiguration endpoint = new AwsClientBuilder.EndpointConfiguration("http://localhost:" + port, "us-west-2");

        client = AmazonS3ClientBuilder
                .standard()
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(endpoint)
                .withCredentials(new AWSStaticCredentialsProvider(new AnonymousAWSCredentials()))
                .build();

    }

    @AfterEach
    public void after() {
        simulator.stop();
        directoryManager.clean();
    }

    @Test
    public void GIVEN_file_exists_THEN_get_should_return_content() throws Exception {

        final String expectedContents = "contents";
        final String bucketName = "test-bucket";
        final String filePath = "file/name";

        // create file via directory manager
        directoryManager.writeFile(bucketName + "/" + filePath, expectedContents);

        // retrieve file via s3
        try (S3ObjectInputStream inputStream = client.getObject(bucketName, filePath).getObjectContent()) {
            final String actualContents = IOUtils.toString(inputStream, Charset.forName("UTF-8"));
            assertThat(actualContents).isEqualTo(expectedContents);
        }

    }

    @Test
    public void GIVEN_file_does_not_exist_THEN_put_should_create_file() throws Exception {

        final String expectedContents = "contents";
        final String bucketName = "test-bucket";
        final String filePath = "file/name";

        // create file via s3
        client.createBucket(bucketName);
        client.putObject(bucketName, filePath, expectedContents);

        // retrieve file via directory manager
        final File file = directoryManager.getFile(bucketName + "/" + filePath);

        try (InputStream inputStream = new FileInputStream(file)) {
            final String actualContents = IOUtils.toString(inputStream, Charset.forName("UTF-8"));
            assertThat(actualContents).isEqualTo(expectedContents);
        }

    }

}
