package ru.nsu.ccfit.khudyakov.core;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import ru.nsu.ccfit.khudyakov.core.mapping.MongoConverter;
import ru.nsu.ccfit.khudyakov.core.mapping.BasicMongoConverter;
import ru.nsu.ccfit.khudyakov.core.mapping.context.MongoMappingContext;
import ru.nsu.ccfit.khudyakov.core.mapping.context.entity.MongoPersistentEntity;
import ru.nsu.ccfit.khudyakov.core.mapping.context.type.ClassTypeInfo;

import static com.mongodb.client.model.Filters.eq;

public class MongoOperationsImpl implements MongoOperations {

    public static final String ID = "_id";

    private final MongoMappingContext mongoMappingContext;
    private final MongoConverter mongoConverter;
    private final MongoDatabase mongoDatabase;

    public MongoOperationsImpl(MongoDatabase mongoDatabase) {
        this.mongoMappingContext = MongoMappingContext.getInstance();
        this.mongoConverter = BasicMongoConverter.getInstance();
        this.mongoDatabase = mongoDatabase;
    }

    @Override
    public <T> T save(T entity) {
        ClassTypeInfo<?> typeInfo = ClassTypeInfo.from(entity.getClass());
        MongoPersistentEntity<?> persistentEntity = mongoMappingContext.getPersistentEntity(typeInfo);

        Document document = mongoConverter.write(entity);
        MongoCollection<Document> collection = mongoDatabase.getCollection(persistentEntity.getCollectionName());

        Object id = document.get("_id");
        if (id == null) {
            collection.insertOne(document);
        } else {
            collection.updateOne(eq(ID, id), document);
        }

        return mongoConverter.read((Class<T>) persistentEntity.getTypeInfo().getType(),  document);
    }

    @Override
    public <T> T findById(Object id, Class<T> entityClass) {
        ClassTypeInfo<T> typeInfo = ClassTypeInfo.from(entityClass);
        MongoPersistentEntity<?> persistentEntity = mongoMappingContext.getPersistentEntity(typeInfo);

        String collectionName = persistentEntity.getCollectionName();
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

        Document document = collection.find(eq(ID, convertId(id))).first();
        return document == null ? null : mongoConverter.read(entityClass, document);
    }

    private ObjectId convertId(Object id) {
        if (id instanceof String) {
            return new ObjectId((String) id);
        }
        if (id instanceof ObjectId) {
            return (ObjectId) id;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public void remove(Object object) {

    }

}
