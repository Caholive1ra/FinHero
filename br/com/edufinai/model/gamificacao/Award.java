package model.gamificacao;

public class Award {
    private Long id;
    private String description;
    private int points;

    public  Award() {
    }
    public Award(Long id, String description, int points) {
        this.id = id;
        this.description = description;
        this.points = points;
    }

}
