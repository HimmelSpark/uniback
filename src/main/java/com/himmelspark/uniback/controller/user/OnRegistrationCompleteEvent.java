package com.himmelspark.uniback.controller.user;

import com.himmelspark.uniback.model.UserModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

public class OnRegistrationCompleteEvent extends ApplicationEvent {
    @Getter @Setter private String appURL;
    @Getter @Setter private UserModel user;

    public OnRegistrationCompleteEvent (
            String appURL,
            UserModel user
    ) {
        super(user);
        this.appURL = appURL;
        this.user = user;
    }
}
