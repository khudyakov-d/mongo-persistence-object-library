package ru.nsu.ccfit.khudyakov.core;

import org.bson.Document;
import ru.nsu.ccfit.khudyakov.core.mapping.query.Criteria;

import java.util.List;
import java.util.Optional;

public abstract class MongoRepository<T, I> implements CrudRepository<T, I> {

    private final MongoOperations mongoOperations;
    private final Class<T> entityClass;

    protected MongoRepository(MongoOperations mongoOperations, Class<T> entityClass) {
        this.mongoOperations = mongoOperations;
        this.entityClass = entityClass;
    }

    @Override
    public Optional<T> findById(I id) {
        return Optional.ofNullable(mongoOperations.findById(id, entityClass));
    }

    @Override
    public List<T> findAll() {
        return mongoOperations.findAll(new Document(), entityClass);
    }

    @Override
    public List<T> findAll(Criteria criteria) {
        Document criteriaDocument = criteria == null ? new Document() :criteria.getCriteriaDocument();
        return mongoOperations.findAll(criteriaDocument, entityClass);
    }

    @Override
    public <P> List<P> findAll(Criteria criteria, Class<P> projectionClass) {
        Document criteriaDocument = criteria == null ? new Document() :criteria.getCriteriaDocument();
        return mongoOperations.findAll(entityClass, criteriaDocument, projectionClass);
    }

    @Override
    public <S extends T> S save(S entity) {
        return mongoOperations.save(entity);
    }

    @Override
    public <S extends T> void delete(S entity) {
        mongoOperations.delete(entity);
    }

}
