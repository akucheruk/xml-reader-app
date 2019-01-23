package com.akucheruk.xml.reader.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "client")
public class Client implements Serializable {
    private UUID id;
    private String firstName;
    private String lastName;
    private String middleName;
    private Long inn;

    public Client() {
    }

    public Client(String firstName, String lastName, String middleName, Long inn) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.inn = inn;
    }

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", updatable = false, nullable = false)
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Column(nullable = false)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(nullable = false)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column
    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    @Column
    public Long getInn() {
        return inn;
    }

    public void setInn(Long inn) {
        this.inn = inn;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("FirstName: " + firstName)
                .append(", lastName: " + lastName)
                .append(", middleName: " + middleName)
                .append(", inn: " + inn)
                .toString();
    }
}
