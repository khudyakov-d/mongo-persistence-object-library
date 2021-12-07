package ru.nsu.ccfit.khudyakov.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import ru.nsu.ccfit.khudyakov.mapping.MongoConverter;
import ru.nsu.ccfit.khudyakov.mapping.context.MongoMappingContext;
import ru.nsu.ccfit.khudyakov.mapping.entity.MongoPersistentEntity;
import ru.nsu.ccfit.khudyakov.type.ClassTypeInfo;

import static com.mongodb.client.model.Filters.eq;

@RequiredArgsConstructor
public class MongoOperationsImpl implements MongoOperations {

    private final MongoMappingContext mongoMappingContext;
    private final MongoConverter mongoConverter;
    private final MongoDatabase mongoDatabase;

    @Override
    public <T> T save(T objectToSave) {
        return null;
    }

    @Override
    public <T> T findById(Object id, Class<T> entityClass) {
        ClassTypeInfo<T> typeInfo = ClassTypeInfo.from(entityClass);
        MongoPersistentEntity<?> persistentEntity = mongoMappingContext.getPersistentEntity(typeInfo);

        String collectionName = persistentEntity.getName();
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

        Document document = collection.find(eq("_id", new ObjectId((String) id))).first();
        return document == null ? null : mongoConverter.read(entityClass, document);
    }

    @Override
    public void remove(Object object) {

    }

}
