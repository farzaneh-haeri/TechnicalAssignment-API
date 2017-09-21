package com.silverrail.technicalassignment.entity;


import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "STATE")
public class State {

    @Id
    @Column(name = "USER_ID")
    private String userId;

    @NotNull
    @Column(name = "VALUE")
    private String value;

    public State() {
    }

    public State(String userId, String value) {
        this.userId = userId;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "State{" +
                "userId='" + userId + '\'' +
                ", value='" + value + '\'' +
                "}";
    }
}
