package com.vamonossoftware.simulator.email;

import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.mail.internet.MimeMessage;

@Slf4j
@Service
@ConditionalOnProperty(value = "simulator.email.enabled", havingValue = "true")
public class EmailServerSimulator {

    private String defaultEmail;
    private GreenMail greenMail;

    public EmailServerSimulator(@Value("${simulator.email.default-address}") String defaultEmail) {
        log.info("Email Server Simulator starting");
        this.defaultEmail = defaultEmail;
    }

    @PostConstruct
    public void start() {
        greenMail = new GreenMail(ServerSetupTest.ALL);
        greenMail.start();

        if (defaultEmail != null) {
            GreenMailUtil.sendTextEmailTest(defaultEmail, defaultEmail, "Email Server Started", "Email server simulator has been started");
        }
    }

    @PreDestroy
    public void stop() {
        greenMail.stop();
    }

    public void clear() {
        try {
            greenMail.purgeEmailFromAllMailboxes();
        }
        catch (FolderException e) {
            throw new RuntimeException(e);
        }
    }

    public MimeMessage[] getReceivedMessages() {
        return greenMail.getReceivedMessages();
    }


}
