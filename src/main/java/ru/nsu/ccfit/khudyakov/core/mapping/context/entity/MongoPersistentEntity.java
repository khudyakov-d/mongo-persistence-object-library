package ru.nsu.ccfit.khudyakov.core.mapping.context.entity;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import ru.nsu.ccfit.khudyakov.core.exception.DocumentAnnotationNotFoundException;
import ru.nsu.ccfit.khudyakov.core.exception.NotValidIdTypeException;
import ru.nsu.ccfit.khudyakov.core.mapping.context.property.MongoPersistentProperty;
import ru.nsu.ccfit.khudyakov.core.mapping.context.property.PersistentPropertyAccessor;
import ru.nsu.ccfit.khudyakov.core.mapping.context.property.PersistentPropertyAccessorImpl;
import ru.nsu.ccfit.khudyakov.core.mapping.context.type.TypeInfo;
import ru.nsu.ccfit.khudyakov.core.persistence.Document;
import ru.nsu.ccfit.khudyakov.core.persistence.Id;

import java.beans.Introspector;
import java.util.List;
import java.util.stream.Collectors;

public class MongoPersistentEntity<T> extends AbstractPersistentEntity<T, MongoPersistentProperty> {

    private static final List<Class<?>> SUPPORTED_ID_TYPES = List.of(String.class, ObjectId.class);

    private final String collectionName;

    public MongoPersistentEntity(TypeInfo<T> typeInfo) {
        super(typeInfo);

        Class<T> type = typeInfo.getType();

        if (!type.isAnnotationPresent(Document.class)) {
            throw new DocumentAnnotationNotFoundException();
        }

        Document documentAnnotation = type.getAnnotation(Document.class);
        if (StringUtils.isBlank(documentAnnotation.collection())) {
            collectionName = Introspector.decapitalize(type.getSimpleName());
        } else {
            collectionName = documentAnnotation.collection();
        }
    }

    @Override
    public String getCollectionName() {
        return collectionName;
    }

    @Override
    public MongoPersistentProperty getIdProperty() {
        return idProperty;
    }

    @Override
    public List<MongoPersistentProperty> getAssociations() {
        return properties.stream()
                .filter(MongoPersistentProperty::isAssociation)
                .collect(Collectors.toList());
    }

    @Override
    public void addPersistentProperty(MongoPersistentProperty property) {
        boolean isId = property.getField().isAnnotationPresent(Id.class);
        if (isId) {
            addIdProperty(property);
            return;
        }

        properties.add(property);
    }

    @Override
    public boolean containsPersistentProperty(String name) {
        return properties.stream().anyMatch(p -> p.getFieldName().equals(name));
    }

    @Override
    public MongoPersistentProperty getPersistentProperty(String name) {
        return properties.stream()
                .filter(p -> p.getFieldName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private void addIdProperty(MongoPersistentProperty property) {
        Class<?> type = property.getTypeInfo().getType();
        boolean isValidType = SUPPORTED_ID_TYPES.stream()
                .anyMatch(t -> type.isAssignableFrom(type));

        if (!isValidType) {
            throw new NotValidIdTypeException();
        }

        idProperty = property;
    }

    @Override
    public <B> PersistentPropertyAccessor<B> getPropertyAccessor(B bean) {
        return new PersistentPropertyAccessorImpl<>(bean);
    }

}
