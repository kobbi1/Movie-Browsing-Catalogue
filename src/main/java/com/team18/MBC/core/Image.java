package com.team18.MBC.core;

import jakarta.persistence.*;

@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob  // Large Object for storing binary data
    @Column(nullable = false)
    private byte[] data;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(name = "user_id", nullable = false)  // Ensures a user is linked to an image
    private Long userId;

    // Default constructor
    public Image() {}

    // Constructor with parameters
    public Image(String name, String type, byte[] data, Long userId) {
        this.name = name;
        this.type = type;
        this.data = data;
        this.userId = userId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
