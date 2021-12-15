package ru.nsu.ccfit.khudyakov.core.mapping.context.property;

public interface PersistentPropertyAccessor<T> {

    void setProperty(PersistentProperty<?> property, Object value);

    Object getPropertyValue(PersistentProperty<?> property);

    T getBean();

}
