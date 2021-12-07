package ru.nsu.ccfit.khudyakov.test;

import ru.nsu.ccfit.khudyakov.core.MongoRepository;
import ru.nsu.ccfit.khudyakov.core.MongoOperations;

public class FruitsRepository extends MongoRepository<Fruit, String> {

    public FruitsRepository(MongoOperations mongoOperations, Class<Fruit> entityClass) {
        super(mongoOperations, entityClass);
    }

}
