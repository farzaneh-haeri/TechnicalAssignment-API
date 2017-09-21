package com.silverrail.technicalassignment.controller.validators;

import com.silverrail.technicalassignment.controller.CharsToAppend;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class AlphaNumericValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return CharsToAppend.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if(!(target instanceof CharsToAppend))
            return;

        CharsToAppend charsToAppend = (CharsToAppend) target;
        Character character = charsToAppend.getCharacter();

        if(!Character.isLetterOrDigit(character))
            errors.rejectValue("character","Alpha", "Only alphanumeric characters are accepted!");
    }
}
