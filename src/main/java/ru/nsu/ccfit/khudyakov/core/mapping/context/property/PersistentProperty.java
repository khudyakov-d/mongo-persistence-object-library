package ru.nsu.ccfit.khudyakov.core.mapping.context.property;

import ru.nsu.ccfit.khudyakov.core.mapping.context.entity.PersistentEntity;
import ru.nsu.ccfit.khudyakov.core.mapping.context.type.TypeInfo;

import java.lang.reflect.Field;

public interface PersistentProperty<P extends PersistentProperty<P>> {

    PersistentEntity<?, P> getOwner();

    String getFieldName();

    TypeInfo<?> getTypeInfo();

    Field getField();

}
