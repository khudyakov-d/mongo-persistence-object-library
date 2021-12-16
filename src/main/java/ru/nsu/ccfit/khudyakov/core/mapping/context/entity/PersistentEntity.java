package ru.nsu.ccfit.khudyakov.core.mapping.context.entity;

import ru.nsu.ccfit.khudyakov.core.mapping.context.property.PersistentProperty;
import ru.nsu.ccfit.khudyakov.core.mapping.context.property.PersistentPropertyAccessor;
import ru.nsu.ccfit.khudyakov.core.mapping.context.type.TypeInfo;

import java.util.List;

public interface PersistentEntity<T, P extends PersistentProperty<P>> {

    String getCollectionName();

    TypeInfo<T> getTypeInfo();

    P getIdProperty();

    List<P> getProperties();

    List<P> getAssociations();

    void addPersistentProperty(P property);

    boolean containsPersistentProperty(String name);

    <B> PersistentPropertyAccessor<B> getPropertyAccessor(B bean);

}
