package ru.nsu.ccfit.khudyakov.core.mapping;

import org.bson.Document;
import org.bson.types.ObjectId;
import ru.nsu.ccfit.khudyakov.core.mapping.context.MongoMappingContext;
import ru.nsu.ccfit.khudyakov.core.mapping.document.DocumentAccessor;
import ru.nsu.ccfit.khudyakov.core.mapping.document.DocumentAccessorImpl;
import ru.nsu.ccfit.khudyakov.core.mapping.context.entity.MongoPersistentEntity;
import ru.nsu.ccfit.khudyakov.core.mapping.context.property.MongoPersistentProperty;
import ru.nsu.ccfit.khudyakov.core.mapping.context.property.PersistentPropertyAccessor;
import ru.nsu.ccfit.khudyakov.core.mapping.context.type.ClassTypeInfo;
import ru.nsu.ccfit.khudyakov.core.mapping.context.type.TypeInfo;

import java.util.List;

public class BasicMongoConverter implements MongoConverter {

    private final MongoMappingContext mongoMappingContext = MongoMappingContext.getInstance();

    private BasicMongoConverter() {
    }

    public static MongoConverter getInstance() {
        return MongoConverterImplHolder.instance;
    }


    @Override
    public Document write(Object entity) {
        Class<?> entityType = entity.getClass();
        TypeInfo<?> typeInfo = ClassTypeInfo.from(entityType);

        MongoPersistentEntity<?> persistentEntity = mongoMappingContext.getPersistentEntity(typeInfo);
        PersistentPropertyAccessor<Object> propertyAccessor = persistentEntity.getPropertyAccessor(entity);

        Document document = new Document();
        DocumentAccessor documentAccessor = new DocumentAccessorImpl(document);

        putId(persistentEntity, propertyAccessor, documentAccessor);
        putProperties(persistentEntity, propertyAccessor, documentAccessor);

        return document;
    }

    private void putProperties(MongoPersistentEntity<?> persistentEntity,
                               PersistentPropertyAccessor<Object> propertyAccessor,
                               DocumentAccessor documentAccessor) {
        List<MongoPersistentProperty> properties = persistentEntity.getProperties();
        for (MongoPersistentProperty p : properties) {
            documentAccessor.put(p, propertyAccessor.getPropertyValue(p));
        }
    }

    private void putId(MongoPersistentEntity<?> persistentEntity,
                       PersistentPropertyAccessor<Object> propertyAccessor,
                       DocumentAccessor documentAccessor) {
        MongoPersistentProperty idProperty = persistentEntity.getIdProperty();

        Object idValue = propertyAccessor.getPropertyValue(idProperty);
        if (idValue == null) {
            return;
        }

        if (idProperty.getTypeInfo().getType().isAssignableFrom(ObjectId.class)) {
            documentAccessor.putId((ObjectId) idValue);
        }
        if (idProperty.getTypeInfo().getType().isAssignableFrom(String.class)) {
            documentAccessor.putId(new ObjectId((String) idValue));
        }

        throw new IllegalArgumentException();
    }

    @Override
    public <S> S read(Class<S> entityType, Document document) {
        TypeInfo<?> typeInfo = ClassTypeInfo.from(entityType);
        MongoPersistentEntity<?> persistentEntity = mongoMappingContext.getPersistentEntity(typeInfo);

        try {
            S entity = entityType.getDeclaredConstructor().newInstance();
            PersistentPropertyAccessor<S> propertyAccessor = persistentEntity.getPropertyAccessor(entity);
            DocumentAccessor documentAccessor = new DocumentAccessorImpl(document);

            setIdProperty(persistentEntity, propertyAccessor, documentAccessor);
            setProperties(persistentEntity, propertyAccessor, documentAccessor);

            return entity;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private <S> void setIdProperty(MongoPersistentEntity<?> persistentEntity,
                                   PersistentPropertyAccessor<S> propertyAccessor,
                                   DocumentAccessor documentAccessor) {
        MongoPersistentProperty idProperty = persistentEntity.getIdProperty();
        ObjectId id = documentAccessor.getId();

        if (idProperty.getTypeInfo().getType().isAssignableFrom(String.class)) {
            propertyAccessor.setProperty(idProperty, id.toString());
            return;
        }
        if (idProperty.getTypeInfo().getType().isAssignableFrom(ObjectId.class)) {
            propertyAccessor.setProperty(idProperty, id);
            return;
        }

        throw new IllegalArgumentException();
    }

    private <S> void setProperties(MongoPersistentEntity<?> persistentEntity,
                                   PersistentPropertyAccessor<S> propertyAccessor,
                                   DocumentAccessor documentAccessor) {
        List<MongoPersistentProperty> properties = persistentEntity.getProperties();
        for (MongoPersistentProperty property : properties) {
            Object propertyValue = documentAccessor.get(property);
            propertyAccessor.setProperty(property, propertyValue);
        }
    }

    private static final class MongoConverterImplHolder {
        private static final BasicMongoConverter instance = new BasicMongoConverter();
    }

}
