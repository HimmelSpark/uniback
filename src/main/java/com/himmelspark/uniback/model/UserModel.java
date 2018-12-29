package com.himmelspark.uniback.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "users")
@DynamicUpdate //TODO поставил с надеждой, что save позволит обновить
public class UserModel {

    @JsonCreator
    public UserModel (
            @JsonProperty("usename") String username,
            @JsonProperty("email") String email,
            @JsonProperty("password") String password,
            Boolean enabled
    ) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.enabled = (enabled != null && enabled);
    }

    protected UserModel(){}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter private Long id;

    @Column
    @Getter @Setter private String username;

    @Column
    @Getter @Setter private String email;

    @Column
    @Getter @Setter private String password;

    @Transient
    @Column(name = "enabled")
    @Getter @Setter private Boolean enabled;

}
