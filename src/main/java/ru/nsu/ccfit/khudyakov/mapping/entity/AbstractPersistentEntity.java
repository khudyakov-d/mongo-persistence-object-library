package ru.nsu.ccfit.khudyakov.mapping.entity;

import ru.nsu.ccfit.khudyakov.mapping.property.MongoPersistentProperty;
import ru.nsu.ccfit.khudyakov.mapping.property.PersistentProperty;
import ru.nsu.ccfit.khudyakov.type.TypeInfo;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPersistentEntity<T, P extends PersistentProperty<P>>
        implements PersistentEntity<T, P >{

    protected TypeInfo<T> typeInfo;

    private final List<P> properties = new ArrayList<>();

    private  P idProperty;

    protected AbstractPersistentEntity(TypeInfo<T> typeInfo) {
        this.typeInfo = typeInfo;

        Class<T> entity = typeInfo.getType();

    }

    @Override
    public TypeInfo<?> getTypeInfo() {
        return typeInfo;
    }

    @Override
    public void addPersistentProperty(P property) {
        properties.add(property);
    }

    public List<P> getProperties() {
        return properties;
    }

}
