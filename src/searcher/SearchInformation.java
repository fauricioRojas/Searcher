package searcher;

public class SearchInformation {
    public String word;
    public String webSite;
    public String title;
    public String paragraph;
    public int appearances;
    public double time;

    public SearchInformation(String word, String webSite, String title, String paragraph, int appearances, double time) {
        this.word = word;
        this.webSite = webSite;
        this.title = title;
        this.paragraph = paragraph;
        this.appearances = appearances;
        this.time = time;
    }
}
