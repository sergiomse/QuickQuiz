package com.sergiomse.quickquiz.model;

/**
 * Created by sergio on 08/11/2015.
 */
public class Exercise {

    public final static int NORMAL_EXERCISE_TYPE = 0;
    public final static int QUESTIONNAIRE_EXERCISE_TYPE = 1;


    private String id;
    private String folder;
    private int type;
    private String question;
    private Option options[];
    private String solution;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Option[] getOptions() {
        return options;
    }

    public void setOptions(Option[] options) {
        this.options = options;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

}
