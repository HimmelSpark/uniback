package com.himmelspark.uniback.controller;

import com.himmelspark.uniback.model.UserModel;
import com.himmelspark.uniback.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "register")
    public ResponseEntity<?> register(HttpSession session, @RequestBody UserModel user) {
        if (session.getAttribute("user") != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("logout to register");
        }
        this.userService.createUser(user);
        return ResponseEntity.status(HttpStatus.OK).body("Hello Motherfucker!!!");
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
