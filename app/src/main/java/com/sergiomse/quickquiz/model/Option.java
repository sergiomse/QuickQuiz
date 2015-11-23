package com.sergiomse.quickquiz.model;

/**
 * Created by sergio on 15/11/2015.
 */
public class Option {

    private String text;
    private boolean correct;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
