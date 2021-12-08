package ru.nsu.ccfit.khudyakov.core.mapping.context.property;

import ru.nsu.ccfit.khudyakov.core.mapping.context.entity.PersistentEntity;
import ru.nsu.ccfit.khudyakov.core.mapping.context.type.TypeInfo;
import ru.nsu.ccfit.khudyakov.core.persistence.Ref;

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

    @Override
    public boolean isAssociation() {
        return getField().isAnnotationPresent(Ref.class);
    }


}
