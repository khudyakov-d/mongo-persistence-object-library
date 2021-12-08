package ru.nsu.ccfit.khudyakov.test;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.nsu.ccfit.khudyakov.core.persistence.Document;
import ru.nsu.ccfit.khudyakov.core.persistence.Id;

@Getter
@Setter
@ToString
@Document(collection = "shops")
public class Shop {

    @Id
    private String id;

    private String name;

}
