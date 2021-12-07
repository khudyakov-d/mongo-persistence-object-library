package ru.nsu.ccfit.khudyakov.type;

public abstract class TypeInfoDiscoverer<T> implements TypeInfo<T> {

    protected final Class<T> type;

    protected TypeInfoDiscoverer(Class<T> type) {
        this.type = type;
    }

    @Override
    public Class<T> getType() {
        return type;
    }

}
