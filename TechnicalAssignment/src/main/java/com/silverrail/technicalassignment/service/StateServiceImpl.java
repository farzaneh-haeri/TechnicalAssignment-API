package com.silverrail.technicalassignment.service;

import com.silverrail.technicalassignment.controller.CharsToAppend;
import com.silverrail.technicalassignment.entity.State;
import com.silverrail.technicalassignment.exceptions.StateException;
import com.silverrail.technicalassignment.repository.StateRepository;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class StateServiceImpl implements StateService{

    private static final Logger logger = org.apache.log4j.Logger.getLogger(StateServiceImpl.class);
    private static final int MAX_LENGTH = 200;


    private StateRepository stateRepository;

    public StateServiceImpl(StateRepository stateRepository){
        this.stateRepository = stateRepository;
    }

    @Override
    public State getState(String userId){
        State state = stateRepository.findOne(userId);
        if(state == null)
            state = new State(userId, "");
        logger.info("UserId " + userId + " got current state: " + state.getValue());
        return state;
    }

    @Override
    public State append(String userId, CharsToAppend charsToAppend) {
        final String stringToAppend = new String(new char[charsToAppend.getCount()]).replace('\0', charsToAppend.getCharacter());
        State currentState = getState(userId);
        String currentStateValue = currentState.getValue();
        currentState.setValue(currentStateValue + stringToAppend);
        if (currentState.getValue().length() > MAX_LENGTH) {
            logger.error("Length of the string state will exceed 200 characters!" + currentState);
            throw new StateException("Length of the string state will exceed 200 characters!");
        }

        logger.info("UserId " + userId + " added " + charsToAppend.getCharacter() + " " + charsToAppend.getCount() + " time(s)");
        saveOrUpdateState(currentState);
        return currentState;
    }

    @Override
    public long getStateNumbersSum(String userId) {
        State currentState = getState(userId);
        final List<String> digits = filterAlphabeticChars(currentState.getValue());
        long sum = 0;
        for (String digit : digits) {
                sum += Integer.parseInt(digit);
        }
        logger.info("UserId " + userId + " got sum of " + currentState + ", sum: " + sum);
        return sum;
    }

    @Override
    public String getStateWithoutNumbers(String userId) {
        State currentState = getState(userId);
        final String stateWithoutNumbers = filterNumericChars(currentState.getValue());
        if(stateWithoutNumbers.isEmpty()) {
            logger.error("UserId " + userId + " got current state without numbers: " + currentState);
            throw new StateException("No alphabetic character found!");
        }
        logger.info("UserId " + userId + " got current state without numbers: " + currentState);
        return stateWithoutNumbers;
    }

    @Override
    public State deleteLastOccurrenceOfCharacter(String userId, Character character) {

        if(!Character.isLetterOrDigit(character)){
            logger.error("UserId " + userId + " could not delete: " + character);
            throw new StateException("Only alphanumeric characters are accepted!");
        }

        State currentState = getState(userId);
        StringBuilder currentStateValue = new StringBuilder(currentState.getValue());
        final int lastIndex = currentStateValue.lastIndexOf(String.valueOf(character));

        if (lastIndex < 0) {
            logger.error("UserId " + userId + " could not delete char: " + character + ", because " + character + " not found");
            throw new StateException("Char " + character + " not found.");
        }

        currentStateValue.deleteCharAt(lastIndex);
        logger.info("UserId " + userId + " deleted char: " + character + " from " + currentStateValue);
        currentState.setValue(currentStateValue.toString());
        saveOrUpdateState(currentState);
        return currentState;
    }

    public void saveOrUpdateState(State state){
        stateRepository.save(state);
    }

    @NotNull
    private String filterNumericChars(String value){
        StringBuilder alphabeticChars = new StringBuilder("");
        Pattern pattern = Pattern.compile("[^0-9]+");
        Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            alphabeticChars.append(matcher.group());
        }
        return alphabeticChars.toString();
    }

    @NotNull
    private List<String> filterAlphabeticChars(String value){
        List<String> numericChars = new ArrayList<>(100);
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(value);
        while (matcher.find()) {
            numericChars.add(matcher.group());
        }
        return numericChars;
    }

}
