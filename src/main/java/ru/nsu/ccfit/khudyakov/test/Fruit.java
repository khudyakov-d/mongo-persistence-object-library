package ru.nsu.ccfit.khudyakov.test;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.ccfit.khudyakov.core.persistence.Document;
import ru.nsu.ccfit.khudyakov.core.persistence.Id;

@Getter
@Setter
@Document(collection = "fruits")
public class Fruit {

    @Id
    private String id;

    private String name;

    private Double price;

}
