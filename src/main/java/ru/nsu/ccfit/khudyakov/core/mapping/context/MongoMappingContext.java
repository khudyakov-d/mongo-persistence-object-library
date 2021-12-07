package ru.nsu.ccfit.khudyakov.core.mapping.context;

import ru.nsu.ccfit.khudyakov.core.mapping.context.entity.MongoPersistentEntity;
import ru.nsu.ccfit.khudyakov.core.mapping.context.property.MongoPersistentProperty;
import ru.nsu.ccfit.khudyakov.core.mapping.context.property.Property;
import ru.nsu.ccfit.khudyakov.core.mapping.context.type.TypeInfo;

public class MongoMappingContext extends AbstractMappingContext<MongoPersistentEntity<?>, MongoPersistentProperty> {

    private MongoMappingContext() {
    }

    public static MongoMappingContext getInstance() {
        return MongoMappingContextHolder.instance;
    }

    @Override
    protected <T> MongoPersistentEntity<?> createPersistentEntity(TypeInfo<T> typeInformation) {
        return new MongoPersistentEntity<>(typeInformation);
    }

    @Override
    protected MongoPersistentProperty createPersistentProperty(Property property, MongoPersistentEntity<?> owner) {
        return new MongoPersistentProperty(property, owner);
    }

    private static final class MongoMappingContextHolder {
        private static final MongoMappingContext instance = new MongoMappingContext();
    }

}
