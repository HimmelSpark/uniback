package com.himmelspark.uniback.service.mail;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

//TODO где-то надо указать логин пароль от почты, может тут в конфиге, а может тут магия протоколов, пока хз

/**
 * public class MyConstants {
 * * public static String EMAIL = "somemail@mail.ru;
 * * public static String PASSWORD = "somepassword";
 * }
 */
@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost("smtp.mail.ru");
        javaMailSender.setPort(587);
        javaMailSender.setUsername(MyConstants.EMAIL);
        javaMailSender.setPassword(MyConstants.PASSWORD);
        javaMailSender.setJavaMailProperties(getMailProperties());

        return javaMailSender;
    }

    private Properties getMailProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.debug", "true");
        return properties;
    }
}
