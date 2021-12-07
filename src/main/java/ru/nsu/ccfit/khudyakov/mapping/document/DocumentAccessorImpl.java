package ru.nsu.ccfit.khudyakov.mapping.document;

import com.mongodb.DBObject;
import org.bson.Document;
import org.bson.conversions.Bson;
import ru.nsu.ccfit.khudyakov.mapping.entity.MongoPersistentEntity;
import ru.nsu.ccfit.khudyakov.mapping.property.MongoPersistentProperty;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public class DocumentAccessorImpl extends DocumentAccessor{

    public DocumentAccessorImpl(Document document) {
        super(document);
    }

    public void put(MongoPersistentProperty property, Object value) {
        String fieldName = property.getFieldName();
        document.put(fieldName, value);
    }

    public Object get(MongoPersistentProperty property) {
        return document.get(property.getFieldName());
    }

    public Object getRawId(MongoPersistentEntity<?> entity) {
        return get(entity.getIdProperty());
    }

}
