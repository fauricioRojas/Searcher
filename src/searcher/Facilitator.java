package searcher;

import gui.Browser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


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
     * @param totalTime Total time of the search
     * @param type Execution's type
     * @param i 
     */
    public void showResults(ArrayList<SearchInformation> arrayInformation, double totalTime, boolean type, int i) 
    {        
        String result = "<div style='font-family: Arial, Helvetica, sans-serif; font-size: 12px;'>"+arrayInformation.size() + " results found (" + totalTime + " seconds)</div>";
        
        for (SearchInformation searchInformation : arrayInformation) {
           
            WordsInformation.add(searchInformation.word,searchInformation.appearances);
            
            result += "<br><hr><br>";
            result += "<strong style='font-family: Arial; font-size: 13px;'>"+searchInformation.title+"</strong>";
            result += "<br>";
            result += "<a style='font-family: Arial, Helvetica, sans-serif; font-size: 11px;' href='"+searchInformation.webSite+"'>"+searchInformation.webSite+"</a>";
            result += "<br>";
            result += "<i style='font-family: Arial, Helvetica, sans-serif; font-size: 11px;'>"+searchInformation.paragraph + "</i>";
            result += "<div style='font-family: Arial, Helvetica, sans-serif; font-size: 11px;'>"+searchInformation.appearances + " appearances of <strong>"+searchInformation.word+"</strong></div>";
            result += "<div style='font-family: Arial, Helvetica, sans-serif; font-size: 11px;'>"+searchInformation.time + " seconds</div>";            
        }       
        
        if(!type)
            ExecutionInformation.info.add(new ExecutionInformation("Sequential", totalTime, i));
        else
            ExecutionInformation.info.add(new ExecutionInformation("Parrallel", totalTime, i));
        this.myBrowser.setTextInTextResults(result);
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
    
    /**
     * This method get a paragraph of the web site
     * @param webSite Web site in which to search
     * @param word Word to search
     * @return Paragraph of the web site
     * @throws IOException 
     */
    public String getParagraph(String webSite, String word) throws IOException {
        Document document = Jsoup.connect(webSite).get();
        String paragraph = document.text();
        
        return(paragraph.substring(0, 150) + "...");
    } 
}
