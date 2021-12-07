package ru.nsu.ccfit.khudyakov.mapping;

import org.bson.Document;
import ru.nsu.ccfit.khudyakov.mapping.context.MongoMappingContext;
import ru.nsu.ccfit.khudyakov.mapping.document.DocumentAccessor;
import ru.nsu.ccfit.khudyakov.mapping.document.DocumentAccessorImpl;
import ru.nsu.ccfit.khudyakov.mapping.entity.MongoPersistentEntity;
import ru.nsu.ccfit.khudyakov.mapping.property.MongoPersistentProperty;
import ru.nsu.ccfit.khudyakov.mapping.property.PersistentPropertyAccessor;
import ru.nsu.ccfit.khudyakov.type.ClassTypeInfo;
import ru.nsu.ccfit.khudyakov.type.TypeInfo;

import java.util.List;

public class MongoConverterImpl implements MongoConverter {

    private final MongoMappingContext mongoMappingContext;


    public MongoConverterImpl(MongoMappingContext mongoMappingContext) {
        this.mongoMappingContext = mongoMappingContext;
    }

    @Override
    public <S> S read(Class<S> entityType, Document document) {
        TypeInfo<?> typeInfo = ClassTypeInfo.from(entityType);
        MongoPersistentEntity<?> persistentEntity = mongoMappingContext.getPersistentEntity(typeInfo);

        try {
            S entity = entityType.getDeclaredConstructor().newInstance();
            PersistentPropertyAccessor<S> propertyAccessor = persistentEntity.getPropertyAccessor(entity);
            DocumentAccessor documentAccessor = new DocumentAccessorImpl(document);

            List<MongoPersistentProperty> properties = persistentEntity.getProperties();
            for (MongoPersistentProperty property : properties) {
                Object propertyValue = documentAccessor.get(property);
                propertyAccessor.setProperty(property, propertyValue);
            }

            return entity;
        } catch (Exception e) {
            throw new IllegalStateException();
        }
    }

    @Override
    public Document write(Object entity) {
        Class<?> entityType = entity.getClass();
        TypeInfo<?> typeInfo = ClassTypeInfo.from(entityType);

        MongoPersistentEntity<?> persistentEntity = mongoMappingContext.getPersistentEntity(typeInfo);
        PersistentPropertyAccessor<Object> propertyAccessor = persistentEntity.getPropertyAccessor(entity);

        Document document = new Document();
        DocumentAccessor documentAccessor = new DocumentAccessorImpl(document);

        List<MongoPersistentProperty> properties = persistentEntity.getProperties();
        for (MongoPersistentProperty p : properties) {
            documentAccessor.put(p, propertyAccessor.getPropertyValue(p));
        }

        return document;
    }

}
