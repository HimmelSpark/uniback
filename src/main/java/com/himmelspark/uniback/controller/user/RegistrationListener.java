package com.himmelspark.uniback.controller.user;

import com.himmelspark.uniback.model.UserModel;
import com.himmelspark.uniback.service.mail.MyMailSender;
import com.himmelspark.uniback.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    private final UserService userService;

    private final MyMailSender mailSender;

    @Autowired
    public RegistrationListener(UserService userService, MyMailSender mailSender) {
        this.userService = userService;
        this.mailSender = mailSender;
    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        UserModel user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user, token);

        String recipientAddress = user.getEmail();
        String subject = "Registration Confirmation";
        String confirmationURL = event.getAppURL() + "/registrationConfirm/" +
                user.getId().toString() + "/" + token;
        String body = "http://localhost:5000/users" + confirmationURL;

        mailSender.sendMail(
            "adam404pet@gmail.com",
            recipientAddress,
            subject,
            body
        );
    }
}
