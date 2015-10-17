/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searcher.Parallel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import searcher.SearchInformation;


public class searchParallel 
{
    // This array contains the words to search in the web sites
    ArrayList<String> arrayWords;
    
    // This array contains the URLs to search
    ArrayList<String> arrayURLs;
    
    /**
     * Constructor of class searchParallel
     * @param Words Words to search in the web sites
     * @param URLs URLs for search
     */
    public searchParallel(ArrayList<String> Words, ArrayList<String> URLs)
    {
        this.arrayWords = Words;
        this.arrayURLs = URLs;
    }
    
    /**
     * This method is used to search parallel
     * @return 
     */
    public ArrayList<SearchInformation> search()
    {
        ForkJoinPool fork_join_pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        searchWord task = new searchWord(this.arrayWords, this.arrayURLs);
        return fork_join_pool.invoke(task);
    }
    
    private ArrayList<SearchInformation> searchURLs(String word, ArrayList<String> URLs)
    {
        ForkJoinPool fork_join_pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        searchURL task = new searchURL(word, URLs);
        return fork_join_pool.invoke(task);
    }
    
    private SearchInformation searchWordInURL(String word, String MyURL)
    {        
        StringBuffer page = null;
        String title = "";
        try 
        { 
            URL url = new URL(MyURL);
            URLConnection uc = url.openConnection(); 
            uc.connect();
            
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream())); 
            StringBuilder builder = new StringBuilder(); 
            String line = "";
            boolean readyTitle = false;
            while ((line = in.readLine()) != null) {
                if(line.contains("<title>") && !readyTitle) {
                    title = line.substring(line.indexOf("<title>")+7, line.indexOf("</title>"));
                    readyTitle = true;
                }
                builder.append(line);
            } 
            String text = builder.toString(); 
            page = new StringBuffer(text); 
        }
        catch (Exception e){}
        
        double start = System.currentTimeMillis();
        
        ForkJoinPool fork_join_pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        Search task = new Search(page, word, 0, page.length(), page.length()/40);
        int occurrences = fork_join_pool.invoke(task);
        
        double end = (System.currentTimeMillis() - start)/1000;
        
        if(occurrences != 0)
        {
            return new SearchInformation(word, MyURL, title, occurrences, end);
        } 
        return null;      
    }
    
    class Search extends RecursiveTask<Integer> 
    {
        StringBuffer pageInfo;
        String wordToSearch;
        int textBegin;
        int textLenght;
        int splitSize;

        public Search(StringBuffer pageInfo, String wordToSearch, int textBegin, int textLenght, int splitSize) 
        {
            this.pageInfo = pageInfo;
            this.wordToSearch = wordToSearch;
            this.textBegin = textBegin;
            this.textLenght = textLenght;
            this.splitSize = splitSize;
        }
        
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

        @Override
        protected Integer compute() 
        {  
            int occurrencesInPage = 0;
            ArrayList<RecursiveTask<Integer>> forks = new ArrayList();


            if (this.textLenght - this.textBegin > this.splitSize) 
            {                
                int lenghtHalf = (this.textBegin + this.textLenght )/2;

                Search task1 = new Search(this.pageInfo, this.wordToSearch, this.textBegin, lenghtHalf, this.splitSize);
                forks.add(task1);
                task1.fork();

                Search task2 = new Search(this.pageInfo, this.wordToSearch, lenghtHalf, this.textLenght, this.splitSize);
                forks.add(task2);
                task2.fork();
            } 
            else 
            {
                return getTotalAppearances(this.pageInfo.substring(this.textBegin,this.textLenght), this.wordToSearch);                
            }     

            for (RecursiveTask<Integer> task : forks)
                occurrencesInPage = occurrencesInPage + task.join();

            return occurrencesInPage;
        }
    } 
            
    class searchURL extends RecursiveTask<ArrayList<SearchInformation>>
    {     
        // Word to search
        String wordToSearch;
        
        // This array contains the URLs to search
        List<String> arrayURLs;
               
        /**
         * Constructor of class searchURL.
         * @param word This is the word to seach
         * @param URLs URLs for search
         */
        public searchURL(String word, List<String> URLs) 
        {
            this.wordToSearch = word;
            this.arrayURLs = URLs;
        }

        @Override
        protected ArrayList<SearchInformation> compute() 
        {
            ArrayList<RecursiveTask<ArrayList<SearchInformation>>> forks = new ArrayList();
            
            if (this.arrayURLs.size() != 1) 
            {                 
                searchURL task1 = new searchURL(this.wordToSearch, this.arrayURLs.subList(0, this.arrayURLs.size()-1));
                forks.add(task1);
                task1.fork();

                searchURL task2 = new searchURL(this.wordToSearch, this.arrayURLs.subList(this.arrayURLs.size()-1,this.arrayURLs.size()));
                forks.add(task2);
                task2.fork();
            }
            else 
            {
                SearchInformation searchResult = searchWordInURL(this.wordToSearch, this.arrayURLs.get(0));
                if (searchResult != null)
                {
                    ArrayList<SearchInformation> myResult = new ArrayList();
                    myResult.add(searchResult);
                    return  myResult;
                }
            }        
            
            ArrayList<SearchInformation> result = new ArrayList();
            for (RecursiveTask<ArrayList<SearchInformation>> task : forks)
                result.addAll(task.join());
            return result;
        }
    }
            
    class searchWord extends RecursiveTask<ArrayList<SearchInformation>>
    {
        // This array contains the words to search in the web sites
        List<String> arrayWords;

        // This array contains the URLs to search
        ArrayList<String> arrayURLs;

        /**
         * Constructor of class searchWord.
         * @param words to search in the web sites
         * @param URLs URLs for search
         */
        public searchWord(List<String> words, ArrayList<String> URLs) 
        {
            this.arrayWords = words;
            this.arrayURLs = URLs;
        }

        @Override
        protected ArrayList<SearchInformation> compute() 
        {
            ArrayList<RecursiveTask<ArrayList<SearchInformation>>> forks = new ArrayList<>();

            if (this.arrayWords.size() != 1) 
            {      
                searchWord task1 = new searchWord(this.arrayWords.subList(0, this.arrayWords.size()-1), this.arrayURLs);
                forks.add(task1);
                task1.fork();  
                
                searchWord task2 = new searchWord(this.arrayWords.subList(this.arrayWords.size()-1,this.arrayWords.size()), this.arrayURLs);
                forks.add(task2);
                task2.fork();
            } 
            else 
            {
                return searchURLs(this.arrayWords.get(0), this.arrayURLs);
            }
            
            ArrayList<SearchInformation> result = new ArrayList();
            for (RecursiveTask<ArrayList<SearchInformation>> task : forks)
                result.addAll(task.join());
            return result;
        }    
    }
}


