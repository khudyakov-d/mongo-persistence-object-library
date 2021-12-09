package ru.nsu.ccfit.khudyakov.core.exception;

public class IdFieldNotFoundException extends CoreException {
    public IdFieldNotFoundException() {
        super("Entity must contain id field\n");
    }
}
