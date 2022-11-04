package nz.ac.unitec.cs.assignment2_mini.DataModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MyQuiz {
    private String category;
    private String difficulty;
    private String question;
    private String correctAnswer;
    private List<String> incorrectAnswers = null;
    private String quizDetailKey;

    public MyQuiz(String category, String difficulty, String question, String correctAnswer, List<String> incorrectAnswers, String quizDetailKey) {
        this.category = category;
        this.difficulty = difficulty;
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswers = incorrectAnswers;
        this.quizDetailKey = quizDetailKey;
    }

    public MyQuiz(){}

    public String getQuizDetailKey() {
        return quizDetailKey;
    }

    public void setQuizDetailKey(String quizDetailKey) {
        this.quizDetailKey = quizDetailKey;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public List<String> getIncorrectAnswers() {
        return incorrectAnswers;
    }

    public void setIncorrectAnswers(List<String> incorrectAnswers) {
        this.incorrectAnswers = incorrectAnswers;
    }
}
