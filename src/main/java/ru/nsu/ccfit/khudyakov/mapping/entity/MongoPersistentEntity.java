package ru.nsu.ccfit.khudyakov.mapping.entity;

import ru.nsu.ccfit.khudyakov.mapping.property.MongoPersistentProperty;
import ru.nsu.ccfit.khudyakov.mapping.property.PersistentPropertyAccessor;
import ru.nsu.ccfit.khudyakov.mapping.property.PersistentPropertyAccessorImpl;
import ru.nsu.ccfit.khudyakov.type.TypeInfo;

public class MongoPersistentEntity<T> extends AbstractPersistentEntity<T, MongoPersistentProperty> {

    public MongoPersistentEntity(TypeInfo<T> typeInfo) {
        super(typeInfo);
    }

    @Override
    public MongoPersistentProperty getIdProperty() {
        return null;
    }

    @Override
    public String getName() {
        return typeInfo.getType().getSimpleName();
    }

    @Override
    public Class<T> getType() {
        return typeInfo.getType();
    }

    @Override
    public <B> PersistentPropertyAccessor<B> getPropertyAccessor(B bean) {
        return new PersistentPropertyAccessorImpl<>(bean);
    }



}
