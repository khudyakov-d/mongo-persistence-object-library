package data;

import ru.nsu.ccfit.khudyakov.core.MongoOperations;
import ru.nsu.ccfit.khudyakov.core.MongoRepository;

public class TreeRepository extends MongoRepository<Tree, String> {

    public TreeRepository(MongoOperations mongoOperations, Class<Tree> entityClass) {
        super(mongoOperations, entityClass);
    }

}
