package com.silverrail.technicalassignment.controller;

import com.silverrail.technicalassignment.controller.validators.AlphaNumericValidator;
import com.silverrail.technicalassignment.entity.State;
import com.silverrail.technicalassignment.exceptions.StateException;
import com.silverrail.technicalassignment.service.StateService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class StateController {

    final private StateService stateService;
    final private AlphaNumericValidator alphaNumericValidator;
    private static final Logger logger = org.apache.log4j.Logger.getLogger(StateController.class);


    @Autowired
    public StateController(StateService stateService, AlphaNumericValidator alphaNumericValidator) {
        this.stateService = stateService;
        this.alphaNumericValidator = alphaNumericValidator;
    }

    @InitBinder("charsToAppend")
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(alphaNumericValidator);
    }

    @RequestMapping(value = "/state", method = RequestMethod.GET)
    public ResponseEntity<State> getCurrentState(HttpSession session) {
        final String sessionId = String.valueOf(session.getId().hashCode());
        return ResponseEntity.ok().body(stateService.getState(sessionId));
    }

    @RequestMapping(value = "/chars", method = RequestMethod.POST)
    public ResponseEntity<State> append(HttpSession session, @Validated @RequestBody CharsToAppend charsToAppend) {
        final String sessionId = String.valueOf(session.getId().hashCode());
        State currentState = stateService.append(sessionId, charsToAppend);
        return ResponseEntity.ok().body(currentState);
    }

    @RequestMapping(value = "/sum", method = RequestMethod.GET)
    public long getStateNumbersSum(HttpSession session) {
        final String sessionId = String.valueOf(session.getId().hashCode());
        return stateService.getStateNumbersSum(sessionId);
    }

    @RequestMapping(value = "/chars", method = RequestMethod.GET)
    public String getStateWithoutNumbers(HttpSession session) {
        final String sessionId = String.valueOf(session.getId().hashCode());
        return stateService.getStateWithoutNumbers(sessionId);
    }

    @RequestMapping(value = "/chars/{character}", method = RequestMethod.DELETE)
    public ResponseEntity<State> deleteLastOccurrenceOfCharacter(HttpSession session, @Validated @PathVariable("character") Character character) {
        final String sessionId = String.valueOf(session.getId().hashCode());
        State state = stateService.deleteLastOccurrenceOfCharacter(sessionId, character);
        return ResponseEntity.ok().body(state);
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map handle(MethodArgumentNotValidException exception) {
        Map error = error(exception.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList()));
        logger.error(error);
        return error;
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map handle(MethodArgumentTypeMismatchException exception) {
        Map error = error(exception.getMessage());
        logger.error(error);
        return error;
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map handle(ConstraintViolationException exception) {
        Map error = error(exception.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList()));
        logger.error(error);
        return error;
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map handle(StateException exception) {
        return error(exception.getMessage());
    }

    private Map error(Object message) {
        return Collections.singletonMap("error", message);
    }

}
