package ru.nsu.ccfit.khudyakov.core.mapping.context.entity;

import ru.nsu.ccfit.khudyakov.core.mapping.context.property.MongoPersistentProperty;
import ru.nsu.ccfit.khudyakov.core.mapping.context.property.PersistentProperty;
import ru.nsu.ccfit.khudyakov.core.mapping.context.type.TypeInfo;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPersistentEntity<T, P extends PersistentProperty<P>> implements PersistentEntity<T, P >{

    protected TypeInfo<T> typeInfo;

    protected final List<P> properties = new ArrayList<>();

    protected P idProperty;

    protected AbstractPersistentEntity(TypeInfo<T> typeInfo) {
        this.typeInfo = typeInfo;
    }

    @Override
    public TypeInfo<T> getTypeInfo() {
        return typeInfo;
    }

    @Override
    public List<P> getProperties() {
        return properties;
    }

    public abstract MongoPersistentProperty getPersistentProperty(String name);
}
