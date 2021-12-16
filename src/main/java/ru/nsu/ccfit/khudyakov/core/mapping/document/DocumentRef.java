package ru.nsu.ccfit.khudyakov.core.mapping.document;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
public class DocumentRef {

    private ObjectId id;

    private String ref;

}
