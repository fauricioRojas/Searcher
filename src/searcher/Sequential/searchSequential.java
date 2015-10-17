package searcher.Sequential;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
        
        for (String webSite : webSites) {
            URL url = new URL(webSite);
            URLConnection uc = url.openConnection();
            uc.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String line, title, content = "";
            
            while ((line = in.readLine()) != null) {
                content += line;
            }
            in.close();
            
            Document document = Jsoup.connect(webSite).get();
            title = document.title();
            
            double time = System.currentTimeMillis();
            
            for (String word : arrayWords) {
                int appearances = this.myFacilitator.getTotalAppearances(content, word);
                if (appearances > 0) {
                    double totalTime = (System.currentTimeMillis() - time)/1000;
                    
                    arrayInformation.add(new SearchInformation(word, webSite, title, myFacilitator.getParagraph(webSite, word), appearances, totalTime));
                }    
            }
        }
        
        return arrayInformation;
    }
}
