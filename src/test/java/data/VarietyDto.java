package data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VarietyDto {

    private String name;

    private Double price;

    public VarietyDto(String name, Double price) {
        this.name = name;
        this.price = price;
    }

}
