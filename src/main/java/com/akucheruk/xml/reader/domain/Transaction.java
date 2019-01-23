package com.akucheruk.xml.reader.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "transaction")
public class Transaction implements Serializable {
    private UUID id;
    private String place;
    private BigDecimal amount;
    private String currency;
    private String card;
    private Client client;
    private Timestamp createdDate;

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
    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    @Column(nullable = false)
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }


    @Column(nullable = false)
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Column(nullable = false)
    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    @ManyToOne(cascade = CascadeType.PERSIST)
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Column(name = "created_date", nullable = false)
    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("Place: " + place)
                .append(", amount: " + amount)
                .append(", currency: " + currency)
                .append(", card: " + card)
                .append(", client: " + client)
                .append(", created date: " + createdDate)
                .toString();

    }
}
