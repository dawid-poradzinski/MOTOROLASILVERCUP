package com.bluescript.youknowit;

import org.json.JSONException;
import org.json.JSONObject;


public class Question {
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void seAnswer(String answer) {
        this.answer = answer;
    }

    private String question;
    private String answer;

    Question(String question, String answer){
        this.question = question;
        this.answer = answer;
    }

    Question(String JSONString) throws JSONException{
        JSONObject json = new JSONObject(JSONString);
        this.question = json.getString("question");
        this.answer = json.getString("answer");
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("question", this.question);
        json.put("answer", this.answer);

        return json;
    }

    @Override
    public String toString() {
        return "{" +
                "\"question\":'" + question + "\"," +
                "\"answer\":\"" + answer + "\"" +
                "}";
    }
}
