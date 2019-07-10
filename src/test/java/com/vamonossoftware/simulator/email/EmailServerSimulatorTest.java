package com.vamonossoftware.simulator.email;

import com.icegreen.greenmail.util.GreenMailUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"simulator-email"})
@Configuration
public class EmailServerSimulatorTest {

    @Autowired EmailServerSimulator simulator;

    @Test
    public void testSend() {

        simulator.clear();

        GreenMailUtil.sendTextEmailTest(
                "to@localhost.com",
                "from@localhost.com",
                "some subject",
                "some body");

        assertThat("some body")
                .isEqualTo(GreenMailUtil.getBody(simulator.getReceivedMessages()[0]));
    }
}
