package com.gobartsdev.exception;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String resource, String id){

        super(
                String.format(
                        "%s not found with key %s", resource, id
                )
        );
    }

}
