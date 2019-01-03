package com.himmelspark.uniback.controller.user;

import com.himmelspark.uniback.model.Tokens;
import com.himmelspark.uniback.model.UserModel;
import com.himmelspark.uniback.service.mail.MyConstants;
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
        Tokens vToken = new Tokens(user.getId(), token);
        vToken.setUser(user);
        vToken.setExpires();
        userService.createVerificationToken(vToken);

        String recipientAddress = user.getEmail();
        String subject = "Registration Confirmation";
        String confirmationURL = MyConstants.APP_URL_DEBUG + "/users/registrationConfirm/" + user.getId().toString() + "/" + token;
        String body = event.getAppURL() + confirmationURL + "\n" + event.getUser().getUsername();

        mailSender.sendMail (
            MyConstants.EMAIL,
            recipientAddress,
            subject,
            body
        );
    }
}
