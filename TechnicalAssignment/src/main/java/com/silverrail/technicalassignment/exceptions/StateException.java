package com.silverrail.technicalassignment.exceptions;


import javax.xml.ws.WebServiceException;

public class StateException extends WebServiceException {

    public StateException(String message) {
        super(message);
    }
}
