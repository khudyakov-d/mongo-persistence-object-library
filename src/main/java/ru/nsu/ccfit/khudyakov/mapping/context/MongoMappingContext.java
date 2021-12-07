package ru.nsu.ccfit.khudyakov.mapping.context;

import ru.nsu.ccfit.khudyakov.mapping.entity.MongoPersistentEntity;
import ru.nsu.ccfit.khudyakov.mapping.property.MongoPersistentProperty;
import ru.nsu.ccfit.khudyakov.mapping.property.Property;
import ru.nsu.ccfit.khudyakov.type.TypeInfo;

public class MongoMappingContext extends AbstractMappingContext<MongoPersistentEntity<?>, MongoPersistentProperty> {

    @Override
    protected <T> MongoPersistentEntity<?> createPersistentEntity(TypeInfo<T> typeInformation) {
        return new MongoPersistentEntity<>(typeInformation);
    }

    @Override
    protected MongoPersistentProperty createPersistentProperty(Property property, MongoPersistentEntity<?> owner) {
        return new MongoPersistentProperty(property, owner);
    }

}
