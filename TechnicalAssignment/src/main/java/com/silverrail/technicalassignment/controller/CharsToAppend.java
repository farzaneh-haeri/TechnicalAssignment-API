package com.silverrail.technicalassignment.controller;

import javax.validation.constraints.*;

public class CharsToAppend {

    @Min(1)
    @Max(9)
    private long count;

    @NotNull
    @Size(min=1, max=1, message="Value to append must be one character.")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message="The character can be only numbers or letters.")
    private String character;

    public CharsToAppend(){

    }
    public CharsToAppend(String character, long count) {
        this.character = character;
        this.count = count;
    }

    public int getCount() {
        return (int) count;
    }

    public String getCharacter() {
        return character;
    }
}
