package model;

public class Category {

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

    public void createCategory() {
        System.out.println("Criando Categoria!");
    }

    public void deleteCategory() {
        System.out.println("Deletando Categoria!");
    }

    public void updateCategory() {
        System.out.println("Atualizando Categoria!");
    }

}