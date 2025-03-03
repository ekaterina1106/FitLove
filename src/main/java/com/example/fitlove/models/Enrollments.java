package com.example.fitlove.models;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Enrollments implements Serializable {
    private int clientId;
    private int classId;

    public Enrollments() {
    }

    public Enrollments(int clientId, int classId) {
        this.clientId = clientId;
        this.classId = classId;
    }
    public int getClientId() {
        return clientId;
    }
    public void setClientId(int clientId) {
        this.clientId = clientId;
    }
    public int getClassId() {
        return classId;
    }
    public void setClassId(int classId) {
        this.classId = classId;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Enrollments that = (Enrollments) o;
        return Objects.equals(clientId, that.clientId) && Objects.equals(classId, that.classId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, classId);
    }
}
