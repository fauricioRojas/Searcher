/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searcher;


public class SearchInformation {
    public String word;
    public String webSite;
    public String title;
    public int appearances;
    public double time;

    public SearchInformation(String word, String webSite, String title, int appearances, double time) {
        this.word = word;
        this.webSite = webSite;
        this.title = title;
        this.appearances = appearances;
        this.time = time;
    }
}
