package ru.nsu.ccfit.khudyakov.core;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;

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
    public <T> T findById(Object id, Class<T> entityClass) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        if (entityClass == null) {
            throw new IllegalArgumentException("entityClass must not be null");
        }

        return doFind(id, entityClass, new HashMap<>());
    }

    private <T> T doFind(Object id, Class<T> entityClass, Map<ObjectId, Object> resolvedRefs) {
        ClassTypeInfo<?> typeInfo = ClassTypeInfo.from(entityClass);
        MongoPersistentEntity<?> persistentEntity = mongoContext.getPersistentEntity(typeInfo);

        String collectionName = persistentEntity.getCollectionName();
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

        ObjectId documentId = convertId(id);
        Document document = collection.find(eq(ID, convertId(id))).first();
        if (document == null) {
            return null;
        }

        T result = mongoConverter.read(entityClass, document);
        resolvedRefs.put(documentId, result);
        readAssociations(persistentEntity, result, document, resolvedRefs);

        return result;
    }

    @Override
    public <T> List<T> find(Document criteriaDocument, Class<T> entityClass) {
        if (criteriaDocument == null) {
            throw new IllegalArgumentException("criteriaDocument must not be null");
        }
        if (entityClass == null) {
            throw new IllegalArgumentException("entityClass must not be null");
        }

        return doFind(criteriaDocument, entityClass);
    }

    private <T> List<T> doFind(Document criteriaDocument, Class<T> entityClass) {
        ClassTypeInfo<?> typeInfo = ClassTypeInfo.from(entityClass);
        MongoPersistentEntity<?> persistentEntity = mongoContext.getPersistentEntity(typeInfo);

        String collectionName = persistentEntity.getCollectionName();
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

        FindIterable<Document> documents = collection.find(criteriaDocument);

        List<T> entities = new ArrayList<>();
        Map<ObjectId, Object> resolvedRefs = new HashMap<>();

        for (Document document : documents) {
            T entity = mongoConverter.read(entityClass, document);
            resolvedRefs.put(convertId(document.get(ID)), entity);
            readAssociations(persistentEntity, entity, document, resolvedRefs);

            entities.add(entity);
        }
        return entities;
    }

    private <T> void readAssociations(MongoPersistentEntity<?> persistentEntity,
                                      T entityValue,
                                      Document document,
                                      Map<ObjectId, Object> resolvedRefs) {

        DocumentAccessor documentAccessor = new DocumentAccessorImpl(document);
        PersistentPropertyAccessor<T> propertyAccessor = persistentEntity.getPropertyAccessor(entityValue);

        List<MongoPersistentProperty> associations = persistentEntity.getAssociations();

        associations.forEach(association -> {
            if (documentAccessor.get(association) == null) {
                return;
            }

            Object associationValue;
            if (association.isLazyAssociation()) {
                associationValue = readLazyAssociation(association, documentAccessor, resolvedRefs);
            } else {
                associationValue = readAssociation(association, documentAccessor, resolvedRefs);
            }

            if (associationValue != null) {
                propertyAccessor.setProperty(association, associationValue);
            }
        });
    }

    private Object readLazyAssociation(MongoPersistentProperty association,
                                       DocumentAccessor documentAccessor,
                                       Map<ObjectId, Object> resolvedRefs) {
        Enhancer enhancer = new Enhancer();
        Class<?> returnType = association.getDescriptor().getReadMethod().getReturnType();
        enhancer.setSuperclass(returnType);
        enhancer.setCallback((LazyLoader) () -> getObject(association, documentAccessor, resolvedRefs));
        return enhancer.create();
    }

    private Object getObject(MongoPersistentProperty association, DocumentAccessor documentAccessor, Map<ObjectId, Object> resolvedRefs) {
        return readAssociation(association, documentAccessor, resolvedRefs);
    }

    private Object readAssociation(MongoPersistentProperty association,
                                   DocumentAccessor documentAccessor,
                                   Map<ObjectId, Object> resolvedRefs) {
        if (association.getTypeInfo().isCollection()) {
            return readCollectionAssociation(association, documentAccessor, resolvedRefs);
        } else {
            return readObjectAssociation(association, documentAccessor, resolvedRefs);
        }
    }

    private Object readCollectionAssociation(MongoPersistentProperty association,
                                             DocumentAccessor documentAccessor,
                                             Map<ObjectId, Object> resolvedRefs) {
        ParametrizedListTypeInfo<?> typeInfo = (ParametrizedListTypeInfo<?>) association.getTypeInfo();
        List<?> refs = (List<?>) documentAccessor.get(association);
        if (refs == null) {
            return null;
        }

        return refs.stream()
                .map(ref -> resolveRef(typeInfo.getArgumentType(), ref, resolvedRefs))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Object readObjectAssociation(MongoPersistentProperty association,
                                         DocumentAccessor documentAccessor,
                                         Map<ObjectId, Object> resolvedRefs) {
        Object ref = documentAccessor.get(association);
        if (ref == null) {
            return null;
        }

        return resolveRef(association.getTypeInfo().getType(), ref, resolvedRefs);
    }

    private Object resolveRef(Class<?> type, Object ref, Map<ObjectId, Object> resolvedRefs) {
        if (ref instanceof Document) {
            Object id = ((Document) ref).get(ID);
            Object refValue = resolvedRefs.get(id);
            return refValue != null ? refValue : doFind(id, type, resolvedRefs);
        }
        throw new IllegalStateException();
    }

    private ObjectId convertId(Object id) {
        if (id instanceof String) {
            return new ObjectId((String) id);
        }
        if (id instanceof ObjectId) {
            return (ObjectId) id;
        }
        throw new IllegalStateException();
    }

    @Override
    public <T> T save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity must not be null");
        }

        ClassTypeInfo<?> typeInfo = ClassTypeInfo.from(entity.getClass());
        MongoPersistentEntity<?> persistentEntity = mongoContext.getPersistentEntity(typeInfo);

        MongoCollection<Document> collection = mongoDatabase.getCollection(persistentEntity.getCollectionName());
        Document document = mongoConverter.write(entity);
        doSave(document, collection);

        return findById(document.get(ID), (Class<T>) entity.getClass());
    }

    private void doSave(Document document, MongoCollection<Document> collection) {
        Object id = document.get(ID);
        if (id == null) {
            collection.insertOne(document);
        } else {
            collection.replaceOne(eq(ID, id), document);
        }
    }

    @Override
    public void remove(Object entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity must not be null");
        }

        Class<?> entityClass = entity.getClass();
        ClassTypeInfo<?> typeInfo = ClassTypeInfo.from(entityClass);
        MongoPersistentEntity<?> persistentEntity = mongoContext.getPersistentEntity(typeInfo);

        String collectionName = persistentEntity.getCollectionName();
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

        PersistentPropertyAccessor<Object> propertyAccessor = persistentEntity.getPropertyAccessor(entity);
        Object id = propertyAccessor.getPropertyValue(persistentEntity.getIdProperty());

        collection.deleteOne(eq(ID, convertId(id)));
    }

}
