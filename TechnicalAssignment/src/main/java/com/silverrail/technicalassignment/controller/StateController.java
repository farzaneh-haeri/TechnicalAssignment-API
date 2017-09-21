package com.silverrail.technicalassignment.controller;

import com.silverrail.technicalassignment.entity.State;
import com.silverrail.technicalassignment.exceptions.StateException;
import com.silverrail.technicalassignment.service.StateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class StateController {


    private StateService stateService;

    @Autowired
    public StateController(StateService stateService) {
        this.stateService = stateService;
    }

    @RequestMapping(value = "/state", method = RequestMethod.GET)
    public State getCurrentState(HttpSession session) {
        final String sessionId = String.valueOf(session.getId().hashCode());
        return stateService.getState(sessionId);
    }

    @RequestMapping(value = "/chars", method = RequestMethod.POST)
    public ResponseEntity append(HttpSession session, @Valid @RequestBody CharsToAppend charsToAppend) {
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

    //TODO: Validation of PathVariable should be revised.
    @RequestMapping(value = "/chars/{character}", method = RequestMethod.DELETE)
    public ResponseEntity deleteLastOccurrenceOfCharacter(HttpSession session,@Valid @Pattern(regexp = "^[A-Za-z0-9]+$", message="The character can be only numbers or letters.") @PathVariable("character") Character string) {
        final String sessionId = String.valueOf(session.getId().hashCode());
        State state = stateService.deleteLastOccurrenceOfCharacter(sessionId, string);
        return ResponseEntity.ok().body(state);
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map handle(MethodArgumentNotValidException exception) {
        return error(exception.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList()));
    }

    @ExceptionHandler
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map handle(ConstraintViolationException exception) {
        return error(exception.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList()));
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
