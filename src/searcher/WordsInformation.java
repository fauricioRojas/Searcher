package searcher;

import java.util.ArrayList;


public class WordsInformation {
    public String word;
    public int i;
    
    public static ArrayList<WordsInformation> info = new ArrayList();

    public WordsInformation(String word, int i) {
        this.word = word;
        this.i = i;
    }
    
    public static void add(String word, int i)
    {
        if(!info.isEmpty())
        {
            int j = 0;
            for(WordsInformation wi : info)
            {
                if(wi.word.equals(word))
                {
                    info.get(j).i = info.get(j).i + i;
                    return;
                }
                j++;
            }
            info.add(new WordsInformation(word, i));
        }
        else
        {
            info.add(new WordsInformation(word, i));
        }
    }
}
