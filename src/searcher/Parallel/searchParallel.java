/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searcher.Parallel;

import java.util.ArrayList;

/**
 *
 * @author Kenneth Pérez
 */
public class searchParallel 
{
    // This array contains the words to search in the web sites
    ArrayList<String> arrayWords;
    
    // This array contains the URLs to search
    ArrayList<String> arrayURLs;
    
    /**
     * Constructor of class searchParallel, this method is used to search parallel
     * @param Words Words to search in the web sites
     * @param URLs URLs for search
     */
    public searchParallel(ArrayList<String> Words, ArrayList<String> URLs)
    {
        this.arrayWords = Words;
        this.arrayURLs = URLs;
    }
}
