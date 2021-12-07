package ru.nsu.ccfit.khudyakov.mapping.entity;

import ru.nsu.ccfit.khudyakov.mapping.property.PersistentProperty;
import ru.nsu.ccfit.khudyakov.mapping.property.PersistentPropertyAccessor;
import ru.nsu.ccfit.khudyakov.type.TypeInfo;

public interface PersistentEntity<T, P extends PersistentProperty<P>> {

    P getIdProperty();

    String getName();

    Class<T> getType();

    TypeInfo<?> getTypeInfo();

    void addPersistentProperty(P property);

    <B> PersistentPropertyAccessor<B> getPropertyAccessor(B bean);


}
