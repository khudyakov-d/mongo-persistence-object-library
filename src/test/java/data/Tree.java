package data;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.ccfit.khudyakov.core.persistence.Document;
import ru.nsu.ccfit.khudyakov.core.persistence.Id;
import ru.nsu.ccfit.khudyakov.core.persistence.Ref;

@Setter
@Getter
@Document(collection = "trees")
public class Tree {

    @Id
    private String id;

    private String name;

    @Ref
    private Fruit fruit;

}
