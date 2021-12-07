package ru.nsu.ccfit.khudyakov.core;

import java.util.Optional;

public abstract class MongoRepository<T, I> implements CrudRepository<T, I> {

    private final MongoOperations mongoOperations;
    private final Class<T> entityClass;

    protected MongoRepository(MongoOperations mongoOperations, Class<T> entityClass) {
        this.mongoOperations = mongoOperations;
        this.entityClass = entityClass;
    }

    @Override
    public <S extends T> S save(S entity) {
        return mongoOperations.save(entity);
    }

    @Override
    public Optional<T> findById(I id) {
        return Optional.ofNullable(mongoOperations.findById(id, entityClass));
    }

    @Override
    public void deleteById(I id) {

    }

}
