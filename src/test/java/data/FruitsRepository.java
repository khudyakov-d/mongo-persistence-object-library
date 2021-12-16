package data;

import ru.nsu.ccfit.khudyakov.core.MongoOperations;
import ru.nsu.ccfit.khudyakov.core.MongoRepository;

public class FruitsRepository extends MongoRepository<Fruit, String> {

    public FruitsRepository(MongoOperations mongoOperations, Class<Fruit> entityClass) {
        super(mongoOperations, entityClass);
    }

}
