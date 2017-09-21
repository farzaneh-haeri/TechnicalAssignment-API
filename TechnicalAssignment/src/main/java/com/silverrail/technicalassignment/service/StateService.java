package com.silverrail.technicalassignment.service;

import com.silverrail.technicalassignment.controller.CharsToAppend;
import com.silverrail.technicalassignment.entity.State;

public interface StateService {

    State getState(String userId);

    State append(String userId, CharsToAppend CharsToAppend);

    long getStateNumbersSum(String userId);

    String getStateWithoutNumbers(String userId);

    State deleteLastOccurrenceOfCharacter(String userId, Character character);

    void saveOrUpdateState(State state);

}
