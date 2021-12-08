package ru.nsu.ccfit.khudyakov.core.mapping.context.type;

public interface TypeInfo<T> {

    Class<T> getType();

    boolean isCollection();

}
