package com.example.l3_20202137;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Answer {

    @SerializedName("response_code")
    private int responseCode;

    @SerializedName("results")
    private List<Question> results;

    public int getResponseCode() {
        return responseCode;
    }

    public List<Question> getResults() {
        return results;
    }
}
