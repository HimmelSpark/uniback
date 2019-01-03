package com.himmelspark.uniback.controller.user;

import com.himmelspark.uniback.model.Tokens;
import com.himmelspark.uniback.model.UserModel;
import com.himmelspark.uniback.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpSession;
import java.util.Calendar;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public UserController (
            UserService userService,
            ApplicationEventPublisher eventPublisher
    ) {
        this.userService = userService;
        this.eventPublisher = eventPublisher;
    }

    @PostMapping(path = "register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register (
            HttpSession session,
            @RequestBody UserModel user,
            WebRequest request
    ) {
        if (session.getAttribute("user") != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("logout to register");
        }
        UserModel createdUser = this.userService.createUser(user);
        if (createdUser == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("duplicate email");
        }
        try {
            String appURL = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(appURL, createdUser));
        } catch (MailAuthenticationException e) {
            //TODO залогировать красненьким, что почта недоступна
            userService.removeUserAndToken(createdUser);
            return ResponseEntity.status(HttpStatus.OK).body("registration not available");
        } catch (MailSendException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("incorrect email");
        }

        return ResponseEntity.status(HttpStatus.OK).body("Watch your mail, motherfucker!");
    }

    @GetMapping(path = "/registrationConfirm/{uID}/{token}")
    public ResponseEntity<?> confirmRegistration (
            @PathVariable("uID") String uID,
            @PathVariable("token") String token
    ) {
        Tokens tokens = userService.getVerificationToken(token);
        if (tokens == null) {
            //TODO если такого токена не нашлось, значит ссылка не валидна
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("your verification link is invalid like your mom!");
        }

        //TODO добавить проверку id пользователя. Чет писали про защиту от ЦЭЭСЭРЭФ атак

        UserModel user = tokens.getUser();
        Calendar cal = Calendar.getInstance();
//        if (tokens.getExpiryDate().getTime() - cal.getTime().getTime() <= 0) {
//            //TODO если срок жизни токена истек
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("So slow! Your token has expired!");
//        }

        user.setEnabled(true);
        userService.saveRegisteredUser(user);
        return ResponseEntity.status(HttpStatus.OK).body("account successfully activated");
    }

    @PostMapping(path = "auth")
    public ResponseEntity<?> auth(HttpSession session, @RequestBody UserModel user) {
        if (session.getAttribute("user") != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("already authenticated");
        }
        if (userService.checkUser(user.getEmail(), user.getPassword())) {
            sessionAuth(session, user);
            return ResponseEntity.status(HttpStatus.OK).body("successfully authenticated");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("wrong credentials");
    }

    private static void sessionAuth(HttpSession session, UserModel user) {
        session.setAttribute("user", user.getUsername());
        session.setMaxInactiveInterval(30 * 60);
    }

}
