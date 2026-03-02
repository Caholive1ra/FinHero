package model;

import java.rmi.server.UID;
import java.time.LocalDate;

public class User {
    private UID Id;
    public String name;
    public String email;
    private String password;
    private boolean active;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private LocalDate deleteAt;

    public User() {
        this.name = name;
        this.email = email;

    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(String name, String email, String password, boolean active, LocalDate createdAt, LocalDate updatedAt,
                LocalDate deleteAt) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deleteAt = deleteAt;
    }

    public UID getId() {
        return Id;
    }

    public void setId(UID id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDate getDeleteAt() {
        return deleteAt;
    }

    public void setDeleteAt(LocalDate deleteAt) {
        this.deleteAt = deleteAt;
    }

    public User createUser () {
        System.out.println("Criando Usúario!");
        return new User(name, email, password, active, createdAt, updatedAt, deleteAt);
    }

    public void deleteUser () {
        System.out.println("Deletando Usúario!");
    }

    public User uptadeUser () {
        System.out.println("Atualizando Usúario!");
        return null;
    }
}
