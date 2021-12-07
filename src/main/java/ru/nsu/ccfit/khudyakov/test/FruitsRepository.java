package ru.nsu.ccfit.khudyakov.test;

import ru.nsu.ccfit.khudyakov.repository.CrudMongoRepository;
import ru.nsu.ccfit.khudyakov.repository.MongoOperations;

public class FruitsRepository extends CrudMongoRepository<fruits, String> {

    public FruitsRepository(MongoOperations mongoOperations, Class<fruits> entityClass) {
        super(mongoOperations, entityClass);
    }

}
