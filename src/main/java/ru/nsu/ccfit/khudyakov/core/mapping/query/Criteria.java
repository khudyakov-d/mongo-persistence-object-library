package ru.nsu.ccfit.khudyakov.core.mapping.query;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class Criteria {

    private static final Object NO_VALUE = new Object();

    private static final String NE_OPERATOR = "$ne";
    private static final String OR_OPERATOR = "$or";
    private static final String AND_OPERATOR = "$and";
    private static final String NOT_OPERATOR = "$not";
    private static final String GT_OPERATOR = "$gt";
    private static final String LT_OPERATOR = "$lt";

    private String field;
    private Object value = NO_VALUE;

    private final List<Criteria> criteriaChain;
    private final LinkedHashMap<String, Object> criteriaOperators = new LinkedHashMap<>();

    public static Criteria where(String field) {
        return new Criteria(field);
    }

    public Criteria() {
        this.criteriaChain = new ArrayList<>();
    }

    public Criteria(String field) {
        this.criteriaChain = new ArrayList<>();
        this.criteriaChain.add(this);
        this.field = field;
    }

    protected Criteria(List<Criteria> criteriaChain, String field) {
        this.criteriaChain = criteriaChain;
        this.criteriaChain.add(this);
        this.field = field;
    }

    public Criteria and(String field) {
        return new Criteria(this.criteriaChain, field);
    }

    public Criteria is(Object value) {
        if (!NO_VALUE.equals(this.value)) {
            throw new IllegalArgumentException();
        }
        if (lastOperatorIsNot()) {
            throw new IllegalArgumentException();
        }
        this.value = value;

        return this;
    }

    public Criteria isNull() {
        return is(null);
    }

    public Criteria isNot(Object value) {
        return getCriteria(value, NE_OPERATOR);
    }

    public Criteria gt(Object value) {
        return getCriteria(value, GT_OPERATOR);
    }

    public Criteria lt(Object value) {
        return getCriteria(value, LT_OPERATOR);
    }

    public Criteria not() {
        return getCriteria(null, NOT_OPERATOR);
    }

    private Criteria getCriteria(Object value, String gtOperator) {
        criteriaOperators.put(gtOperator, value);
        return this;
    }

    private boolean lastOperatorIsNot() {
        if (this.criteriaOperators.isEmpty()) {
            return false;
        }
        Object lastOperator = this.criteriaOperators.keySet().toArray()[this.criteriaOperators.size() - 1];
        return NOT_OPERATOR.equals(lastOperator);
    }

    public Criteria orOperator(Criteria... criteria) {
        if (criteria == null || criteria.length == 0) {
            throw new IllegalArgumentException();
        }
        return getOperatorCriteria(OR_OPERATOR, criteria);
    }

    public Criteria andOperator(Criteria... criteria) {
        if (criteria == null || criteria.length == 0    ) {
            throw new IllegalArgumentException();
        }
        return getOperatorCriteria(AND_OPERATOR, criteria);
    }

    private Criteria getOperatorCriteria(String operator, Criteria[] criteria) {
        List<Document> criteriaDocuments = Arrays.stream(criteria)
                .map(Criteria::getCriteriaDocument)
                .collect(Collectors.toList());

        return updateCriteriaChain(new Criteria(operator).is(criteriaDocuments));
    }

    private Criteria updateCriteriaChain(Criteria criteria) {
        criteriaChain.add(criteria);
        return this;
    }

    public Document getCriteriaDocument() {
        Document criteriaDocument = new Document();

        for (Criteria criteria : this.criteriaChain) {
            Document document = criteria.getCriteriaObjectInner();
            document.forEach((key, v) -> setValue(criteriaDocument, key, v));
        }

        return criteriaDocument;
    }

    private Document getCriteriaObjectInner() {
        Document criteriaDocument = new Document();

        boolean not = false;
        for (Entry<String, Object> e : criteriaOperators.entrySet()) {
            String operator = e.getKey();
            Object operatorValue = e.getValue();

            if (NOT_OPERATOR.equals(operator)) {
                not = true;
                continue;
            }

            if (not) {
                not = false;
                criteriaDocument.put(NOT_OPERATOR, new Document(operator, operatorValue));
            } else {
                criteriaDocument.put(operator, operatorValue);
            }
        }

        if (StringUtils.isBlank(this.field)) {
            return criteriaDocument;
        }

        if (!NO_VALUE.equals(this.value)) {
            Document document = new Document();
            document.put(this.field, this.value);
            document.putAll(criteriaDocument);
            return document;
        }

        return new Document(this.field, criteriaDocument);
    }

    private void setValue(Document document, String key, Object value) {
        Object keyValue = document.get(key);

        if (keyValue == null) {
            document.put(key, value);
        } else {
            throw new IllegalArgumentException();
        }
    }

}
