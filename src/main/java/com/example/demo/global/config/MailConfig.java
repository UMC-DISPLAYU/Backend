package com.example.demo.global.config;

import java.util.Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@RequiredArgsConstructor
public class MailConfig {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;


    @Bean
    public JavaMailSender javaMailSender() {

        JavaMailSenderImpl mailSender =
                new JavaMailSenderImpl();

        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        mailSender.setJavaMailProperties(
                mailProperties());

        return mailSender;
    }


    private Properties mailProperties() {

        Properties properties = new Properties();

        properties.setProperty(
                "mail.transport.protocol",
                "smtp");

        properties.setProperty(
                "mail.smtp.auth",
                "true");

        properties.setProperty(
                "mail.smtp.starttls.enable",
                "true");

        properties.setProperty(
                "mail.debug",
                "true");

        properties.setProperty(
                "mail.smtp.ssl.trust",
                host);

        return properties;
    }
}
