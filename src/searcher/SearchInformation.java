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
    public String webSite;
    public String header;
    public double time;

    public SearchInformation(String webSite, String header, double time) {
        this.webSite = webSite;
        this.header = header;
        this.time = time;
    }
}
