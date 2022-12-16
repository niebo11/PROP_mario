import java.util.ArrayList;
import java.util.HashMap;

public class Contingut {

    private Integer totalWords;

    private ArrayList<Frase> frases;

    private HashMap<String, Integer> wordMap;

    private HashMap<String, Double> TFMap;

    public Contingut(){
        frases = new ArrayList<>();
        initMaps();
    }

    public Contingut(String text){
        String[] items = text.split("\n");
        frases = new ArrayList<>();
        for (String frase: items) {
            frases.add(new Frase(frase));
        }
        initMaps();
        updateWordMap();
    }

    public Contingut(ArrayList<Frase> frases) {
        this.frases = frases;
        initMaps();
        updateWordMap();
    }

    public ArrayList< Frase> getContinguts() {
        return frases;
    }

    public void setContingut(ArrayList<Frase> continguts) {
        this.frases = frases;
        initMaps();
        updateWordMap();
    }

    public void setContingut(String s) {
        String[] stringFrase = s.split("\n");
        this.frases = new ArrayList<Frase>(stringFrase.length);
        for (int i = 0; i < stringFrase.length; ++i){
            System.out.println(stringFrase[i]);
            this.frases.add(i, new Frase(stringFrase[i]));
        }
        System.out.println(this.frases);
        initMaps();
        updateWordMap();
    }

    public void addFrase(Frase fr) {
        frases.add(fr);
        updateWordMapFrase(fr);
    }

    public HashMap<String, Integer> getWordMap() {
        return wordMap;
    }

    public Integer getTotalWords() {
        return totalWords;
    }

    public HashMap<String, Double> getTFMap() {
        return TFMap;
    }

    public Boolean hasToken(String token) {
        for (Frase f: frases) {
            if (f.hasToken(token)) return true;
        }
        return false;
    }

    public Boolean equalsString(String s) {
        String[] frases = new String[this.frases.size()];
        for (int i = 0; i < this.frases.size(); ++i) {
            frases[i] = this.frases.get(i).toString();
        }
        return s.equals(String.join("\n", frases));
    }

    public Boolean hasSentence(String sentence) {
        for (Frase f: frases) {
            if (f.hasSentence(sentence)) return true;
        }
        return false;
    }

    private void initMaps() {
        totalWords = 0;
        TFMap = new HashMap<String, Double>();
        wordMap = new HashMap<String, Integer>();
    }

    private void updateWordMap(){
        for (Frase f: frases) {
            updateWordMapFrase(f);
        }
        updateTFMap();
    }

    private void updateWordMapFrase(Frase f) {
        for (HashMap.Entry<String, Integer> entry: f.getWordCount().entrySet()){
            if (wordMap.containsKey(entry.getKey())) {
                wordMap.put(entry.getKey(), wordMap.get(entry.getKey()) + entry.getValue());
            }
            else {
                wordMap.put(entry.getKey(), entry.getValue());
            }
            totalWords += entry.getValue();
        }
        updateTFMap();
    }

    private void updateTFMap() {
        for(HashMap.Entry<String, Integer> entry: wordMap.entrySet()) {
            TFMap.put(entry.getKey(), (double) entry.getValue()/totalWords);
        }
    }

    @Override
    public String toString(){
        String[] frases = new String[this.frases.size()];
        for (int i = 0; i < this.frases.size(); ++i) {
            frases[i] = this.frases.get(i).toString();
        }
        return String.join("\n", frases);
    }
}
