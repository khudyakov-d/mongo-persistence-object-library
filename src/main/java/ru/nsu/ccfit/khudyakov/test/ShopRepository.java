package ru.nsu.ccfit.khudyakov.test;

import ru.nsu.ccfit.khudyakov.core.MongoOperations;
import ru.nsu.ccfit.khudyakov.core.MongoRepository;

public class ShopRepository extends MongoRepository<Shop, String> {

    public ShopRepository(MongoOperations mongoOperations, Class<Shop> entityClass) {
        super(mongoOperations, entityClass);
    }

}
