/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searcher;

/**
 *
 * @author fauricio
 */
public class SearchInformation {
    public String word;
    public String webSite;
    public String header;
    public int appearances;
    public double time;

    public SearchInformation(String word, String webSite, String header, int appearances, double time) {
        this.word = word;
        this.webSite = webSite;
        this.header = header;
        this.appearances = appearances;
        this.time = time;
    }
}
