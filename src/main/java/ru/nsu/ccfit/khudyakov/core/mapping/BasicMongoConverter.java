package ru.nsu.ccfit.khudyakov.core.mapping;

import org.bson.Document;
import org.bson.types.ObjectId;
import ru.nsu.ccfit.khudyakov.core.mapping.context.MongoMappingContext;
import ru.nsu.ccfit.khudyakov.core.mapping.context.entity.MongoPersistentEntity;
import ru.nsu.ccfit.khudyakov.core.mapping.context.property.MongoPersistentProperty;
import ru.nsu.ccfit.khudyakov.core.mapping.context.property.PersistentPropertyAccessor;
import ru.nsu.ccfit.khudyakov.core.mapping.context.type.ClassTypeInfo;
import ru.nsu.ccfit.khudyakov.core.mapping.context.type.ParametrizedListTypeInfo;
import ru.nsu.ccfit.khudyakov.core.mapping.context.type.TypeInfo;
import ru.nsu.ccfit.khudyakov.core.mapping.document.DocumentAccessor;
import ru.nsu.ccfit.khudyakov.core.mapping.document.DocumentAccessorImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.nsu.ccfit.khudyakov.core.mapping.context.type.TypeInfoDiscoverer.getInfo;

public class BasicMongoConverter implements MongoConverter {

    private final MongoMappingContext mongoContext = MongoMappingContext.getInstance();

    private BasicMongoConverter() {
    }

    public static MongoConverter getInstance() {
        return MongoConverterImplHolder.instance;
    }

    @Override
    public Document write(Object entity) {
        Class<?> entityType = entity.getClass();
        TypeInfo<?> typeInfo = ClassTypeInfo.from(entityType);
        MongoPersistentEntity<?> persistentEntity = mongoContext.getPersistentEntity(typeInfo);

        PersistentPropertyAccessor<Object> propertyAccessor = persistentEntity.getPropertyAccessor(entity);

        Document document = new Document();
        DocumentAccessor documentAccessor = new DocumentAccessorImpl(document);

        writeId(persistentEntity, propertyAccessor, documentAccessor);
        writeProperties(persistentEntity, propertyAccessor, documentAccessor);

        return document;
    }

    private void writeId(MongoPersistentEntity<?> persistentEntity,
                         PersistentPropertyAccessor<Object> propertyAccessor,
                         DocumentAccessor documentAccessor) {
        MongoPersistentProperty idProperty = persistentEntity.getIdProperty();

        Object idValue = propertyAccessor.getPropertyValue(idProperty);
        if (idValue == null) {
            return;
        }

        ObjectId id = convertId(idProperty, idValue);
        documentAccessor.putId(id);
    }

    private void writeProperties(MongoPersistentEntity<?> persistentEntity,
                                 PersistentPropertyAccessor<Object> propertyAccessor,
                                 DocumentAccessor documentAccessor) {
        List<MongoPersistentProperty> properties = persistentEntity.getProperties();
        properties.forEach(property -> {
            if (!property.isAssociation()) {
                documentAccessor.put(property, propertyAccessor.getPropertyValue(property));
                return;
            }

            if (property.isTransient()) {
                return;
            }

            if (property.getTypeInfo().isCollection()) {
                writeRefs(property, propertyAccessor, documentAccessor);
            } else {
                writeRef(property, propertyAccessor, documentAccessor);
            }
        });
    }

    private void writeRefs(MongoPersistentProperty targetProperty,
                           PersistentPropertyAccessor<Object> propertyAccessor,
                           DocumentAccessor documentAccessor) {
        ParametrizedListTypeInfo<?> typeInfo = (ParametrizedListTypeInfo<?>) targetProperty.getTypeInfo();
        MongoPersistentEntity<?> childEntity = mongoContext.getPersistentEntity(getInfo(typeInfo.getArgumentType()));

        List<?> values = (List<?>) propertyAccessor.getPropertyValue(targetProperty);
        if (values != null) {
            List<Document> documents = values.stream()
                    .map(v -> mapChildValue(childEntity, v))
                    .collect(Collectors.toList());

            documentAccessor.put(targetProperty, documents);
        }
    }

    private void writeRef(MongoPersistentProperty targetProperty,
                          PersistentPropertyAccessor<Object> propertyAccessor,
                          DocumentAccessor documentAccessor) {
        TypeInfo<?> typeInfo = targetProperty.getTypeInfo();
        MongoPersistentEntity<?> childEntity = mongoContext.getPersistentEntity(getInfo(typeInfo.getType()));

        Object propertyValue = propertyAccessor.getPropertyValue(targetProperty);
        if (propertyValue != null) {
            Document document = mapChildValue(childEntity, propertyValue);
            documentAccessor.put(targetProperty, document);
        }
    }

    private ObjectId convertId(MongoPersistentProperty idProperty, Object idValue) {
        if (ObjectId.class.equals(idProperty.getTypeInfo().getType())) {
            return ((ObjectId) idValue);
        }

        if (String.class.equals(idProperty.getTypeInfo().getType())) {
            return new ObjectId((String) idValue);
        }

        throw new IllegalArgumentException();
    }

    private Document mapChildValue(MongoPersistentEntity<?> entity, Object entityValue) {
        MongoPersistentProperty idProperty = entity.getIdProperty();

        return Optional.of(entityValue)
                .map(entity::getPropertyAccessor)
                .map(accessor -> accessor.getPropertyValue(idProperty))
                .map(idValue -> convertId(idProperty, idValue))
                .map(objectId -> {
                    Document document = new Document();
                    document.put("$ref", entity.getCollectionName());
                    document.put("$id", objectId);
                    return document;
                })
                .orElseThrow();
    }


    @Override
    public <S> S read(Class<S> entityType, Document document) {
        TypeInfo<?> typeInfo = ClassTypeInfo.from(entityType);
        MongoPersistentEntity<?> persistentEntity = mongoContext.getPersistentEntity(typeInfo);

        try {
            S entity = entityType.getDeclaredConstructor().newInstance();
            PersistentPropertyAccessor<S> propertyAccessor = persistentEntity.getPropertyAccessor(entity);
            DocumentAccessor documentAccessor = new DocumentAccessorImpl(document);

            readId(persistentEntity, propertyAccessor, documentAccessor);
            readProperties(persistentEntity, propertyAccessor, documentAccessor);

            return entity;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private <S> void readId(MongoPersistentEntity<?> persistentEntity,
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

    private <S> void readProperties(MongoPersistentEntity<?> persistentEntity,
                                    PersistentPropertyAccessor<S> propertyAccessor,
                                    DocumentAccessor documentAccessor) {
        List<MongoPersistentProperty> properties = persistentEntity.getProperties();
        for (MongoPersistentProperty property : properties) {
            if (property.isAssociation() || property.isTransient()) {
                continue;
            }
            Object propertyValue = documentAccessor.get(property);
            propertyAccessor.setProperty(property, propertyValue);
        }
    }

    private static final class MongoConverterImplHolder {
        private static final BasicMongoConverter instance = new BasicMongoConverter();
    }

}
