package ru.nsu.ccfit.khudyakov.core.exception;

public class DocumentAnnotationNotFoundException extends CoreException {

    public DocumentAnnotationNotFoundException() {
        super("Entity has no document annotation");
    }

}
