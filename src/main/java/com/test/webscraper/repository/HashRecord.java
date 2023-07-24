package com.test.webscraper.repository;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Table(name = "hash_table")
@ToString
@NoArgsConstructor
public class HashRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String hash;

    @NonNull
    private Double price;

    @NonNull
    private String link;

    @NonNull
    private String date;

    private Timestamp added;

    public HashRecord(@NonNull String hash, @NonNull Double price, @NonNull String link, @NonNull String date) {
        this.hash = hash;
        this.price = price;
        this.link = link;
        this.date = date;
    }
}