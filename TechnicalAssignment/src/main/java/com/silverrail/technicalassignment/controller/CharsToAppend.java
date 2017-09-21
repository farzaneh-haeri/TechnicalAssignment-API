package com.silverrail.technicalassignment.controller;

import javax.validation.constraints.*;

public class CharsToAppend {

    @Min(1)
    @Max(9)
    private long count;

    @NotNull
    private Character character;

    public CharsToAppend(){

    }
    public CharsToAppend(Character character, long count) {
        this.character = character;
        this.count = count;
    }

    public int getCount() {
        return (int) count;
    }

    public Character getCharacter() {
        return character;
    }
}
