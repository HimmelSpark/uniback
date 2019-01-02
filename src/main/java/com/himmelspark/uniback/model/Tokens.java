package com.himmelspark.uniback.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "tokens")
@AllArgsConstructor
public class Tokens {
    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter private Long id;

    @Column(name = "token")
    @Getter @Setter private String token;

    @OneToOne(targetEntity = UserModel.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "id")
    @Getter @Setter private UserModel user;

//    @Column(insertable = false, updatable = false)
//    @JsonIgnore
//    @Getter @Setter private Date expiryDate;

    protected Tokens(){}

    public Tokens(Long id, String token) {
        this.id = id;
        this.token = token;
    }

    @Override
    public String toString() {
        return String.format("Token[id=%d, token='%s']", id, token);
    }

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }
}