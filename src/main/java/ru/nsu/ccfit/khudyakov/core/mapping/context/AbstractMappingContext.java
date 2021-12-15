package ru.nsu.ccfit.khudyakov.core.mapping.context;

import ru.nsu.ccfit.khudyakov.core.exception.IdFieldNotFoundException;
import ru.nsu.ccfit.khudyakov.core.mapping.context.entity.PersistentEntity;
import ru.nsu.ccfit.khudyakov.core.mapping.context.property.PersistentProperty;
import ru.nsu.ccfit.khudyakov.core.mapping.context.property.Property;
import ru.nsu.ccfit.khudyakov.core.mapping.context.type.TypeInfo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMappingContext<E extends PersistentEntity<?, P>, P extends PersistentProperty<P>>
        implements MappingContext<E, P> {

    private final Map<TypeInfo<?>, E> persistentEntities = new HashMap<>();

    @Override
    public E getPersistentEntity(TypeInfo<?> type) {
        E entity = persistentEntities.get(type);

        if (entity != null) {
            return entity;
        }

        return addPersistentEntity(type);
    }

    private E addPersistentEntity(TypeInfo<?> type) {
        E entity = createPersistentEntity(type);
        addProperties(entity);
        persistentEntities.put(type, entity);
        return entity;
    }

    private void addProperties(E entity) {
        TypeInfo<?> typeInfo = entity.getTypeInfo();
        Class<?> type = typeInfo.getType();

        Field[] fields = type.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Property property = new Property(typeInfo, field);
            P persistentProperty = createPersistentProperty(property, entity);
            entity.addPersistentProperty(persistentProperty);
        }

        P idProperty = entity.getIdProperty();
        if (idProperty == null) {
            throw new IdFieldNotFoundException();
        }
    }

    protected abstract <T> E createPersistentEntity(TypeInfo<T> typeInformation);

    protected abstract P createPersistentProperty(Property property, E owner);

}
