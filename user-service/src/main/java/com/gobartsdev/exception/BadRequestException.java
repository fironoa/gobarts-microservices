package com.gobartsdev.exception;

public class BadRequestException extends RuntimeException{

    public BadRequestException(String resource, String field, String value){
        super(
                String.format(
                        "%s of the %s is invalid, provided: %s", field, resource, value
                )
        );

    }
}
