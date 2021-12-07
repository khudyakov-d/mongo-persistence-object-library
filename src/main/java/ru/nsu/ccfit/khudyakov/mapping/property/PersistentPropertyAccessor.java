package ru.nsu.ccfit.khudyakov.mapping.property;

public interface PersistentPropertyAccessor<T> {

    void setProperty(PersistentProperty<?> property, Object value);

    Object getPropertyValue(PersistentProperty<?> property);

}
