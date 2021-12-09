package ru.nsu.ccfit.khudyakov.core.exception;

public class NotValidIdTypeException extends CoreException {

    public NotValidIdTypeException() {
        super("Id field has no valid type. Valid types: String, ObjectId");
    }

}
