import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Frase {

    private ArrayList<String> words;

    private HashMap<String, Integer> wordCount;

    public Frase()
    {
        words = new ArrayList<>();
        wordCount = new HashMap<String, Integer>();
    }

    public Frase(ArrayList<String> words) {
        this.words = words;
        wordCount = new HashMap<String, Integer>();
        updateCount();
    }

    public Frase(String frase) {
        if (frase != null) {
            words = new ArrayList<String>(Arrays.asList(frase.split(" ")));
            updateCount();
        }
    }

    public void setFrases(ArrayList<String> words) {
        this.words = words;
        updateCount();
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public boolean isEmpty() {
        return words.isEmpty();
    }

    public boolean equals(Frase f) {
        return String.join(" ", f.getWords()).equals(String.join(" ", words));
    }

    @Override
    public String toString() {
        return String.join(" ", words);
    }

    public HashMap<String, Integer> getWordCount() {
        return wordCount;
    }

    public boolean hasToken(String token) {
        for (String s: words) {
            if(s.equals(token)) return true;
        }
        return false;
    }

    public boolean hasSentence(String sentence) {
        String joinFrase = String.join(" ", words);
        return joinFrase.contains(sentence);
    }

    private void updateCount() {
        wordCount = new HashMap<String, Integer>();
        countWords();
    }

    private void countWords() {
        for (String s: words) {
            if(wordCount.containsKey(s)) wordCount.put(s, wordCount.get(s) + 1);
            else wordCount.put(s, 1);
        }
    }
}
