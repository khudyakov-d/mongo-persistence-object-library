package ru.nsu.ccfit.khudyakov.test;

import ru.nsu.ccfit.khudyakov.core.MongoOperations;
import ru.nsu.ccfit.khudyakov.core.MongoRepository;

public class ShopRepository extends MongoRepository<Variety, String> {

    public ShopRepository(MongoOperations mongoOperations, Class<Variety> entityClass) {
        super(mongoOperations, entityClass);
    }

}
