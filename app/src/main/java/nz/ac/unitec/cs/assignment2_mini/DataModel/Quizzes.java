package nz.ac.unitec.cs.assignment2_mini.DataModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Quizzes {

    @SerializedName("response_code")
    @Expose
    private Integer responseCode;
    @SerializedName("results")
    @Expose
    private List<Quiz> results = null;

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public List<Quiz> getResults() {
        return results;
    }

    public void setResults(List<Quiz> results) {
        this.results = results;
    }
//    @SerializedName("results")
//    List<Quiz> quizzes;
//    @SerializedName("response_code")
//    int response_code;
//
//    public Integer getResponseCode() {
//        return response_code;
//    }
//
//    public void setResponseCode(Integer responseCode) {
//        this.response_code = responseCode;
//    }
//
//    public List<Quiz> getQuizzes() {
//        return quizzes;
//    }
//
//    public void setQuizzes(List<Quiz> quizzes) {
//        this.quizzes = quizzes;
//    }
}
