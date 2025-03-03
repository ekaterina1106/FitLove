package com.example.fitlove.models;

import jakarta.persistence.*;

@Entity
public class Enrollment {
    @EmbeddedId
    private Enrollments id;

    // Дополнительные поля
    // Например:
    @ManyToOne
    @MapsId("clientId")
    private Clients client;

    @ManyToOne
    @MapsId("classId")
    private GroupClasses aClass;

    public Enrollment() {}

    public Enrollment(Enrollments id, Clients client, GroupClasses aClass) {
        this.id = id;
        this.client = client;
        this.aClass = aClass;
    }

    public Enrollments getId() {
        return id;
    }

    public void setId(Enrollments id) {
        this.id = id;
    }

    public Clients getClient() {
        return client;
    }

    public void setClient(Clients client) {
        this.client = client;
    }

    public GroupClasses getAClass() {
        return aClass;
    }

    public void setAClass(GroupClasses aClass) {
        this.aClass = aClass;
    }
}
