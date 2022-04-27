package com.tiger.types;

public enum BaseType {
    INT,
    FLOAT,
    ;

    public String format() {
        return switch (this) {
            case INT -> "int";
            case FLOAT -> "float";
        };
    }
}
