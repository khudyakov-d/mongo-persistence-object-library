package ru.nsu.ccfit.khudyakov.mapping.property;

import ru.nsu.ccfit.khudyakov.mapping.entity.PersistentEntity;
import ru.nsu.ccfit.khudyakov.type.TypeInfo;

import java.lang.reflect.Field;

public class MongoPersistentProperty extends AbstractPersistentProperty<MongoPersistentProperty> {

    public MongoPersistentProperty(Property property, PersistentEntity<?, MongoPersistentProperty> owner) {
        super(property, owner);
    }

    @Override
    public PersistentEntity<?, MongoPersistentProperty> getOwner() {
        return owner;
    }

    @Override
    public String getFieldName() {
        return property.getName();
    }

    @Override
    public TypeInfo<?> getTypeInfo() {
        return property.getType();
    }

    @Override
    public Field getField() {
        return property.getField();
    }

}
