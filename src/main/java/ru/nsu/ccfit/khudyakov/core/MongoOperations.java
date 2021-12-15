package ru.nsu.ccfit.khudyakov.core;

import org.bson.Document;

import java.util.List;

public interface MongoOperations {

    <T> T findById(Object id, Class<T> entityClass);

    <T> List<T> find(Document criteriaDocument, Class<T> entityClass);

    <T> T save(T objectToSave);

    void remove(Object object);

}
