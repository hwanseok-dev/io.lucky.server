package io.lucky.server.domain.user.entity;


import lombok.Getter;

@Getter
public class User {

    private Long id;
    private String name;
    private int money;

    public User(Long id, String name, int money) {
        this.id = id;
        this.name = name;
        this.money = money;
    }

    public User(String name, int money) {
        this.name = name;
        this.money = money;
    }
}
