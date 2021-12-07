package ru.nsu.ccfit.khudyakov.mapping.property;

import ru.nsu.ccfit.khudyakov.mapping.entity.PersistentEntity;

public abstract class AbstractPersistentProperty<P extends PersistentProperty<P>> implements PersistentProperty<P> {

    protected Property property;
    protected PersistentEntity<?, P> owner;

    protected AbstractPersistentProperty(Property property, PersistentEntity<?, P> owner) {
        this.property = property;
        this.owner = owner;
    }

}
