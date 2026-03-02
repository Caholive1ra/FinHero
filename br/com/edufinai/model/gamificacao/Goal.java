package model.gamificacao;

import java.sql.Date;

public class Goal {
    private Long id;
    private String name;
    private Double objectValue;
    private Double actualValue;
    private Date timeOut;


    public Goal(Long id, String name, Double objectValue, Double actualValue, Date timeOut) {
        this.id = id;
        this.name = name;
        this.objectValue = objectValue;
        this.actualValue = actualValue;
        this.timeOut = timeOut;
    }
}
