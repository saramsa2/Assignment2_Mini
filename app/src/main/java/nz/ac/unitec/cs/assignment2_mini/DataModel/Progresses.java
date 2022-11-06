package nz.ac.unitec.cs.assignment2_mini.DataModel;

import java.util.Map;

public class Progresses {
    Map<String, Progress> progresses;

    public Progresses(Map<String, Progress> progresses) {
        this.progresses = progresses;
    }

    public Progresses() {
    }

    public Map<String, Progress> getProgresses() {
        return progresses;
    }

    public void setProgresses(Map<String, Progress> progresses) {
        this.progresses = progresses;
    }
}
