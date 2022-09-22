package io.github.bruandreo.quarkussocial.rest.dto;

public class FieldError {

    private String field;
    private String message;

    public FieldError(String field, String message) {
        this.field = field;
        this.message = message;
    }

    public String getField() {
        return this.field;
    }

    public String getMessage() {
        return this.message;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
