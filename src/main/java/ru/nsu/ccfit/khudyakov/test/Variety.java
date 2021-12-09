package ru.nsu.ccfit.khudyakov.test;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.ccfit.khudyakov.core.persistence.Document;
import ru.nsu.ccfit.khudyakov.core.persistence.Id;
import ru.nsu.ccfit.khudyakov.core.persistence.Ref;

@Getter
@Setter
@Document(collection = "varieties")
public class Variety {

    @Id
    private String id;

    private String name;

    @Ref
    private Fruit fruit;

}
