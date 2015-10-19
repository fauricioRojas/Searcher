package searcher;

import java.util.ArrayList;

public class ExecutionInformation {
    public String type;
    public double time;
    public int i;

    public static ArrayList<ExecutionInformation> info = new ArrayList();
     
    public ExecutionInformation(String type, double time, int i) {
        this.type = type;
        this.time = time;
        this.i = i;
    }
}