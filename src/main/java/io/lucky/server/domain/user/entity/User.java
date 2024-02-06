package io.lucky.server.domain.user.entity;


import lombok.Getter;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return getMoney() == user.getMoney() && Objects.equals(getId(), user.getId()) && Objects.equals(getName(), user.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getMoney());
    }
}
