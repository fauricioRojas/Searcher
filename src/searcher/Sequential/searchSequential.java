/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searcher.Sequential;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import searcher.Facilitator;
import searcher.SearchInformation;


public class searchSequential {
    private Facilitator myFacilitator;

    public searchSequential(Facilitator myFacilitator) {
        this.myFacilitator = myFacilitator;
    }
    
    /**
     * This method get the content for a web sites and search if a word exists in the content of the web site
     * @param arrayWords Array with the words to search
     * @param webSites Web sites in which search
     * @return Array with information of the search in the web sites
     * @throws IOException 
     */
    public ArrayList<SearchInformation> searchSequential(ArrayList<String> arrayWords, ArrayList<String> webSites) throws IOException {
        // This array contains the information for show in the statistics
        ArrayList<SearchInformation> arrayInformation = new ArrayList();
        // This object contains the information
        SearchInformation mySearchInformation;
        
        boolean readyTitle = false;
        
        for (String webSite : webSites) {
            URL url = new URL(webSite);
            URLConnection uc = url.openConnection();
            uc.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String line, title = "", content = "";
            while ((line = in.readLine()) != null) {
                if(line.contains("<title>") && !readyTitle) {
                    title = line.substring(line.indexOf("<title>")+7, line.indexOf("</title>"));
                    readyTitle = true;
                }
                
                content += line;
            }
            in.close();
            
            double time = System.currentTimeMillis();
            
            for (String word : arrayWords) {
                int appearances = this.myFacilitator.getTotalAppearances(content, word);
                if (appearances > 0) {
                    double totalTime = (System.currentTimeMillis() - time)/1000;
                    
                    mySearchInformation = new SearchInformation(word, webSite, title, myFacilitator.getParagraph(webSite, word), appearances, totalTime);
                    arrayInformation.add(mySearchInformation);
                }    
            }
            readyTitle = false;
        }
        
        return arrayInformation;
    }
}
