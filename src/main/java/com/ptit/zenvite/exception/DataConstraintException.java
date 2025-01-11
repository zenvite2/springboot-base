package com.ptit.zenvite.exception;

public class DataConstraintException extends GlobalException {
    public DataConstraintException(String errorKey, String entityName, String title) {
        super(errorKey, entityName, title);
    }
}
