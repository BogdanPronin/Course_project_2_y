package com.github.bogdan.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Objects;

@DatabaseTable(tableName = "user")
public class User implements Filtration {
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(unique = true)
    private String email;
    @DatabaseField
    private String password;
    @DatabaseField
    private Role role;
    @DatabaseField
    private String createdAt;
    @DatabaseField
    private String updatedAt;
    @DatabaseField(unique = true)
    private String nickName;

    public User() {
    }
    public User(Role role, String password, String dateOfBirthday, String dateOfRegister, String email, String nickName) {
        this.role = role;
        this.password = password;
        this.updatedAt = dateOfBirthday;
        this.createdAt = dateOfRegister;
        this.email = email;
        this.nickName = nickName;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getNickName() {
        return nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public ArrayList<String> getQueryParams() {
        ArrayList<String> s = new ArrayList<>();
        s.add("role");
        s.add("createdAt");
        s.add("updatedAt");
        s.add("email");
        s.add("nickName");
        s.add("id");
        return s;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                role == user.role &&
                Objects.equals(password, user.password) &&
                Objects.equals(updatedAt, user.updatedAt) &&
                Objects.equals(createdAt, user.createdAt) &&
                Objects.equals(email, user.email) &&
                Objects.equals(nickName, user.nickName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, role, password, updatedAt, createdAt, email, nickName);
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

}
