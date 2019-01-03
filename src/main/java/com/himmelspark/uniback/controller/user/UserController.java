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
            userService.removeUserAndToken(createdUser);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("incorrect email");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Watch your mail, motherfucker!");
    }

    @GetMapping(path = "/registrationConfirm/{uID}/{token}")
    public ResponseEntity<?> confirmRegistration (
            @PathVariable("uID") String uID,
            @PathVariable("token") String token,
            HttpSession session
    ) {
        Tokens tokens = userService.getVerificationToken(token);
        if (tokens == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("invalid token");
        }
        Tokens token4security = userService.getVerificationTokenById(Long.parseLong(uID));
        if (tokens != token4security) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("invalid link");
        }

        UserModel user = tokens.getUser();
        Calendar cal = Calendar.getInstance();
        if (tokens.getExpires().getTime() - cal.getTime().getTime() <= 0) {
            userService.removeUserAndToken(user);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("token timeout passed");
        }
        userService.enableUser(user);
        sessionAuth(session, user);
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

    @PostMapping(path = "test")
    public ResponseEntity<?> test(
            @RequestBody UserModel user
    ) {
        userService.createUser(user);
        Tokens tokens = new Tokens(user.getId(), "adasdfsdas");
        tokens.setUser(user);
        tokens.setExpires();
        userService.createVerificationToken(tokens);
        return ResponseEntity.status(HttpStatus.OK).body("test done");
    }

    private static void sessionAuth(HttpSession session, UserModel user) {
        session.setAttribute("user", user.getUsername());
        session.setMaxInactiveInterval(30 * 60);
    }

}
