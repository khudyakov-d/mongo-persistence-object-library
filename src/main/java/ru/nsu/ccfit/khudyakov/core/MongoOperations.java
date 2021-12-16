package ru.nsu.ccfit.khudyakov.core;

import org.bson.Document;

import java.util.List;

public interface MongoOperations {

    <T> T findById(Object id, Class<T> entityClass);

    <T> List<T> findAll(Document criteriaDocument, Class<T> entityClass);

    <P, T> List<P> findAll(Class<T> entityClass, Class<P> projectionClass);

    <T> T save(T objectToSave);

    void remove(Object object);

}
