package ru.nsu.ccfit.khudyakov.mapping.document;

import org.bson.Document;
import ru.nsu.ccfit.khudyakov.mapping.entity.MongoPersistentEntity;
import ru.nsu.ccfit.khudyakov.mapping.property.MongoPersistentProperty;

public abstract class DocumentAccessor {

    protected final Document document;

    protected DocumentAccessor(Document document) {
        this.document = document;
    }

    public abstract void put(MongoPersistentProperty property, Object value);

    public abstract Object get(MongoPersistentProperty property);

}

