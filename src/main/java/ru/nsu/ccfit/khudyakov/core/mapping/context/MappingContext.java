package ru.nsu.ccfit.khudyakov.core.mapping.context;

import ru.nsu.ccfit.khudyakov.core.mapping.context.entity.PersistentEntity;
import ru.nsu.ccfit.khudyakov.core.mapping.context.property.PersistentProperty;
import ru.nsu.ccfit.khudyakov.core.mapping.context.type.TypeInfo;

public interface MappingContext<E extends PersistentEntity<?, P>, P extends PersistentProperty<P>> {

    E getPersistentEntity(TypeInfo<?> type);

}
