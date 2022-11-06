package nz.ac.unitec.cs.assignment2_mini.DataModel;

import java.util.HashMap;
import java.util.Map;

public class Progress {
    int progress;
    Map<Integer, Boolean> results;

    public Progress(int progress) {
        this.progress = progress;
        results = new HashMap<>();
    }
    public Progress() {
        this.progress = 0;
        results = new HashMap<>();
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public Map<Integer, Boolean> getResults() {
        return results;
    }

    public void setResults(Map<Integer, Boolean> results) {
        this.results = results;
    }
    public void addResult(int questionNumber, boolean result) {
        this.results.put(questionNumber, result);
    }
}
