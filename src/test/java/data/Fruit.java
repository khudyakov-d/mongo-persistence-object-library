package data;

import lombok.Getter;
import lombok.Setter;
import ru.nsu.ccfit.khudyakov.core.persistence.Document;
import ru.nsu.ccfit.khudyakov.core.persistence.Id;
import ru.nsu.ccfit.khudyakov.core.persistence.Ref;

import java.util.List;

@Getter
@Setter
@Document(collection = "fruits")
public class Fruit {

    @Id
    private String id;

    private String name;

    @Ref
    private Tree tree;

    @Ref(lazy = true)
    private List<Variety> varieties;

}
