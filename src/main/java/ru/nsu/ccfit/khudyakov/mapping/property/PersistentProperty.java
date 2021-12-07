package ru.nsu.ccfit.khudyakov.mapping.property;

import ru.nsu.ccfit.khudyakov.mapping.entity.PersistentEntity;
import ru.nsu.ccfit.khudyakov.type.TypeInfo;

import java.lang.reflect.Field;

public interface PersistentProperty<P extends PersistentProperty<P>> {

    PersistentEntity<?, P> getOwner();

    String getFieldName();

    TypeInfo<?> getTypeInfo();

    Field getField();

}
