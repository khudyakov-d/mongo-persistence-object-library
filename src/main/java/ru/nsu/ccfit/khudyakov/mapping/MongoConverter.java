package ru.nsu.ccfit.khudyakov.mapping;

import org.bson.Document;
import org.bson.conversions.Bson;

public interface MongoConverter {

    <S> S read(Class<S> type, Document bson);

    Document write(Object obj);

}
