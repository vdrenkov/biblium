package dev.vdrenkov.biblium.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "clients")
public class Client extends Person {

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @Column(name = "email", unique = true)
    private String email;

    public Client(int id, String name, String surname, String phoneNumber, String email) {
        super(id, name, surname);
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public Client(String name, String surname, String phoneNumber, String email) {
        super(name, surname);
        this.phoneNumber = phoneNumber;
        this.email = email;
    }
}
