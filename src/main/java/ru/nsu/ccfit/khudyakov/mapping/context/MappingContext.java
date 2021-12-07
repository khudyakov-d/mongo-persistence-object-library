package ru.nsu.ccfit.khudyakov.mapping.context;

import ru.nsu.ccfit.khudyakov.mapping.entity.PersistentEntity;
import ru.nsu.ccfit.khudyakov.mapping.property.PersistentProperty;
import ru.nsu.ccfit.khudyakov.type.TypeInfo;

public interface MappingContext<E extends PersistentEntity<?, P>, P extends PersistentProperty<P>> {

    E getPersistentEntity(TypeInfo<?> type);

}
