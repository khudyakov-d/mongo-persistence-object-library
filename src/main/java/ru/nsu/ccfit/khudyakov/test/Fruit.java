package ru.nsu.ccfit.khudyakov.test;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.nsu.ccfit.khudyakov.core.persistence.Document;
import ru.nsu.ccfit.khudyakov.core.persistence.Id;
import ru.nsu.ccfit.khudyakov.core.persistence.Ref;

import java.util.List;

@Getter
@Setter
@ToString
@Document(collection = "fruits")
public class Fruit {

    @Id
    private String id;

    private String name;

    private Double price;

    @Ref
    private List<Shop> shops;

}