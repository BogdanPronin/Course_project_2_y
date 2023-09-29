package com.github.bogdan.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Objects;

@DatabaseTable(tableName = "friend")
public class Friend implements Filtration{
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private User user1;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private User user2;

    public Friend() {
    }

    public Friend(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friend friends = (Friend) o;
        return id == friends.id && Objects.equals(user1, friends.user1) && Objects.equals(user2, friends.user2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user1, user2);
    }

    @Override
    public ArrayList<String> getQueryParams() {
        ArrayList<String> s = new ArrayList<>();
        s.add("user1");
        s.add("user2");
        return s;
    }
}
