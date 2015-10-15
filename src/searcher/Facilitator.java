/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package searcher;

import gui.Browser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 *
 * @author fauricio
 */
public class Facilitator {
    private Browser myBrowser;

    public Facilitator(Browser myBrowser) {
        this.myBrowser = myBrowser;
    }
    
    /**
     * This method get the appearances of a word in the content of the web site
     * @param content Content of the web site
     * @param word Word to search
     * @return Number of appearances
     */
    public int getTotalAppearances(String content, String word) {
        int appearances = 0;
        
        StringTokenizer wordsContent = new StringTokenizer(content, " \n<>&¿?@=¡!|^{}[]*~'&%#\";:/-_°¬+,.\\()");
        
        while(wordsContent.hasMoreTokens()){
            if(wordsContent.nextToken().equals(word)) {
                appearances++;
            }
        }
        
        return appearances;
    }
    
    /**
     * This method shows the results of the search
     * @param arrayInformation Array with the information of the search
     */
    public void showResults(ArrayList<SearchInformation> arrayInformation) {
        this.myBrowser.textAreaResults.append(arrayInformation.size() + " results found (" + getTotalTime(arrayInformation) + " seconds)");
                        
        for (SearchInformation searchInformation : arrayInformation) {
            this.myBrowser.textAreaResults.append("\n\n" + searchInformation.word + "\n");
            this.myBrowser.textAreaResults.append(searchInformation.title + "\n");
            this.myBrowser.textAreaResults.append(searchInformation.webSite + "\n");
            this.myBrowser.textAreaResults.append(searchInformation.appearances + " appearances\n");
            this.myBrowser.textAreaResults.append(searchInformation.time + " seconds");
        }
    }
    
    /**
     * This method get the total time of the search
     * @param arrayInformation Array with the information of the search
     * @return Total time
     */
    public double getTotalTime(ArrayList<SearchInformation> arrayInformation) {
        double totalTime = 0;
        
        for (SearchInformation searchInformation : arrayInformation) {
            totalTime += searchInformation.time;
        }
        
        return totalTime;
    }
    
    /**
     * This method get the words to search in the web sites
     * @return Array with the words to search
     */
    public ArrayList<String> getWordsToSearch() {
        ArrayList<String> arrayWords = new ArrayList();
        String stringSearch = this.myBrowser.textSearch.getText().replace(" | ","|"), word = "";
        
        for(int i=0; i<stringSearch.length(); i++) {
            if(stringSearch.charAt(i) != '|') {
                word += stringSearch.charAt(i);
            }
            else {
                arrayWords.add(word);
                word = "";
            }
        }
        arrayWords.add(word);
        
        return arrayWords;
    }
    
    /**
     * This method get the web sites of the browser to search
     * @return Array with the web sites
     */
    public ArrayList<String> getWebSites() {
        ArrayList<String> webSites = new ArrayList();
        String result[] = this.myBrowser.textAreaPages.getText().split("\\n");
        
        webSites.addAll(Arrays.asList(result));
        
        return webSites;
    }
}
