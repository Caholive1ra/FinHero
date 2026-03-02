package model;

public class Account {
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Account createAccount() {
        System.out.println("Criando Conta!");
        return null;
    }

    public void deleteAccount() {
        System.out.println("Deletando Conta!");
    }

    public Account uptadeAccount() {
        System.out.println("Atualizando Conta!");
        return null;
    }
}