package com.himmelspark.uniback.controller.user;

import com.himmelspark.uniback.model.UserModel;
import com.himmelspark.uniback.model.VerificationToken;
import com.himmelspark.uniback.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
            //TODO регистрация не прошла, может с таким email уже было
            return ResponseEntity.status(HttpStatus.CONFLICT).body("duplicate email");
        }
        try {
            String appURL = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(appURL, createdUser));
        } catch (Exception e) {
            //TODO чет пошло не так, может несуществующая почта
            //TODO хотя конечно надо из хэндлера кидать эксепшоны и тут ловить
            return ResponseEntity.status(HttpStatus.CONFLICT).body("your GF email");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Watch your mail, motherfucker!");
    }

    @GetMapping(path = "/registrationConfirm/{uID}/{token}")
    public ResponseEntity<?> confirmRegistration (
            @PathVariable("uID") String uID,
            @PathVariable("token") String token
    ) {
        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            //TODO если такого токена не нашлось, значит ссылка не валидна
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("your verification link is invalid like your mom!");
        }

        //TODO добавить проверку id пользователя. Чет писали про защиту от ЦЭЭСЭРЭФ атак

        UserModel user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if (verificationToken.getExpiryDate().getTime() - cal.getTime().getTime() <= 0) {
            //TODO если срок жизни токена истек
            return ResponseEntity.status(HttpStatus.CONFLICT).body("So slow! Your token has expired!");
        }

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
