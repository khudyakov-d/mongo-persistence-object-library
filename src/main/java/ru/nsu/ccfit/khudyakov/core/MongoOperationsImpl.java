package ru.nsu.ccfit.khudyakov.core;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import ru.nsu.ccfit.khudyakov.core.mapping.BasicMongoConverter;
import ru.nsu.ccfit.khudyakov.core.mapping.MongoConverter;
import ru.nsu.ccfit.khudyakov.core.mapping.context.MongoMappingContext;
import ru.nsu.ccfit.khudyakov.core.mapping.context.entity.MongoPersistentEntity;
import ru.nsu.ccfit.khudyakov.core.mapping.context.property.MongoPersistentProperty;
import ru.nsu.ccfit.khudyakov.core.mapping.context.property.PersistentPropertyAccessor;
import ru.nsu.ccfit.khudyakov.core.mapping.context.type.ClassTypeInfo;
import ru.nsu.ccfit.khudyakov.core.mapping.context.type.ParametrizedListTypeInfo;
import ru.nsu.ccfit.khudyakov.core.mapping.document.DocumentAccessor;
import ru.nsu.ccfit.khudyakov.core.mapping.document.DocumentAccessorImpl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;
import static ru.nsu.ccfit.khudyakov.core.mapping.context.type.TypeInfoDiscoverer.getInfo;

public class MongoOperationsImpl implements MongoOperations {

    public static final String ID = "_id";

    private final MongoMappingContext mongoContext;
    private final MongoConverter mongoConverter;
    private final MongoDatabase mongoDatabase;

    public MongoOperationsImpl(MongoDatabase mongoDatabase) {
        this.mongoContext = MongoMappingContext.getInstance();
        this.mongoConverter = BasicMongoConverter.getInstance();
        this.mongoDatabase = mongoDatabase;
    }

    @Override
    public <T> T save(T entity) {
        ClassTypeInfo<?> typeInfo = ClassTypeInfo.from(entity.getClass());
        MongoPersistentEntity<?> persistentEntity = mongoContext.getPersistentEntity(typeInfo);

        Document document = mongoConverter.write(entity);
        MongoCollection<Document> collection = mongoDatabase.getCollection(persistentEntity.getCollectionName());

        doSave(document, collection);

        return findById(document.get(ID), (Class<T>) entity.getClass());
    }

    private void doSave(Document document, MongoCollection<Document> collection) {
        Object id = document.get(ID);
        if (id == null) {
            collection.insertOne(document);
        } else {
            collection.updateOne(eq(ID, id), document);
        }
    }

    @Override
    public <T> T findById(Object id, Class<T> entityClass) {
        ClassTypeInfo<T> typeInfo = ClassTypeInfo.from(entityClass);
        MongoPersistentEntity<?> persistentEntity = mongoContext.getPersistentEntity(typeInfo);

        String collectionName = persistentEntity.getCollectionName();
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

        Document document = collection.find(eq(ID, convertId(id))).first();

        if (document == null) {
            return null;
        }

        T result = mongoConverter.read(entityClass, document);
        readAssociations(persistentEntity, result, document);

        return result;
    }

    private <T> void readAssociations(MongoPersistentEntity<?> persistentEntity, T entityValue, Document document) {
        DocumentAccessor documentAccessor = new DocumentAccessorImpl(document);

        MongoPersistentEntity<?> childEntity = mongoContext.getPersistentEntity(getInfo(entityValue.getClass()));
        PersistentPropertyAccessor<T> propertyAccessor = childEntity.getPropertyAccessor(entityValue);

        List<MongoPersistentProperty> associations = persistentEntity.getAssociations();
        for (MongoPersistentProperty association : associations) {
            if (association.getTypeInfo().isCollection()) {
                readCollectionAssociation(documentAccessor, propertyAccessor, association);
            } else {
                readAssociation(documentAccessor, propertyAccessor, association);
            }
        }
    }

    private <T> void readCollectionAssociation(DocumentAccessor documentAccessor,
                                               PersistentPropertyAccessor<T> propertyAccessor,
                                               MongoPersistentProperty association) {
        ParametrizedListTypeInfo<?> typeInfo = (ParametrizedListTypeInfo<?>) association.getTypeInfo();
        List<?> refs = (List<?>) documentAccessor.get(association);
        List<?> refValues = refs.stream()
                .map(ref -> resolveRef(typeInfo.getArgumentType(), ref))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        propertyAccessor.setProperty(association, refValues);
    }

    private <T> void readAssociation(DocumentAccessor documentAccessor,
                                     PersistentPropertyAccessor<T> propertyAccessor,
                                     MongoPersistentProperty association) {
        Object ref = documentAccessor.get(association);
        Object refValue = resolveRef(association.getTypeInfo().getType(), ref);
        if (refValue != null) {
            propertyAccessor.setProperty(association, refValue);
        }
    }

    private Object resolveRef(Class<?> type, Object ref) {
        if (ref instanceof Document) {
            Object id = ((Document) ref).get(ID);
            return findById(id, type);
        } else {
            throw new IllegalStateException();
        }
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
    public void remove(Object entity) {
        Class<?> entityClass = entity.getClass();
        ClassTypeInfo<?> typeInfo = ClassTypeInfo.from(entityClass);
        MongoPersistentEntity<?> persistentEntity = mongoContext.getPersistentEntity(typeInfo);

        String collectionName = persistentEntity.getCollectionName();
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

        PersistentPropertyAccessor<Object> propertyAccessor = persistentEntity.getPropertyAccessor(entity);
        Object idValue = propertyAccessor.getPropertyValue(persistentEntity.getIdProperty());
        ObjectId id = convertId(idValue);

        collection.deleteOne(eq(ID, convertId(id)));
    }

}
