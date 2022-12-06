import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Collections;
import java.util.Collection;
import java.util.List;
import java.lang.Math;

class Calculator{

    private Integer totalWords;

    private HashMap<String, Double> IDF;

    public Calculator() {
        totalWords = 0;
        IDF = new HashMap<String, Double>();
    }
    public void updateIDF(ArrayList<Document> documentsList) {
        HashMap<String, Integer> temptCountMap = new HashMap<String, Integer>();
        for (Document d: documentsList) {
            HashMap<String, Integer> temptMap = d.getContingut().getWordMap();
            Set<String> temptStringList = temptMap.keySet();
            for (String key: temptStringList) {
                if (temptCountMap.containsKey(key)) temptCountMap.put(key, temptCountMap.get(key) + 1);
                else temptCountMap.put(key, 1);
            }
        }
        totalWords = temptCountMap.size();
        for (HashMap.Entry<String, Integer> entry: temptCountMap.entrySet()) {
            IDF.put(entry.getKey(), Math.log((double) entry.getValue() / (double) totalWords));
        }
    }

    public HashMap<String, Double> computeWordScore(Document d){
        HashMap<String, Double> result = new HashMap<String, Double>();
        HashMap<String, Double> TFMap = d.getContingut().getTFMap();
        for (HashMap.Entry<String, Double> entry: TFMap.entrySet()) {
            result.put(entry.getKey(), entry.getValue()*IDF.get(entry.getKey()));
        }
        return result;
    }

    public Double computeCosineSimilarity(HashMap<String, Double> TF_IDF, Document d2) {
        HashMap<String, Double> TF_IDF_2 = computeWordScore(d2);
        Double dotProductResult = dotProduct(TF_IDF, TF_IDF_2);
        Double d1AbsoluteValue = absoluteValue(TF_IDF);
        Double d2AbsoluteValue = absoluteValue(TF_IDF_2);
        if (d1AbsoluteValue * d2AbsoluteValue == 0) {
            return 0.0;
        }
        return dotProductResult/(d1AbsoluteValue * d2AbsoluteValue);
    }

    public Double dotProduct(HashMap<String, Double> x1, HashMap<String, Double> x2) {
        Double result = 0.0;
        for (HashMap.Entry<String, Double> entry: x1.entrySet()) {
            if (x2.containsKey(entry.getKey())) {
                result += entry.getValue() * x2.get(entry.getKey());
            }
        }
        return result;
    }

    public Double absoluteValue(HashMap<String, Double> x) {
        Collection<Double> scores = x.values();
        Double result = 0.0;
        for (Double d: scores) {
            result += d*d;
        }
        return Math.sqrt(result);
    }

    public HashMap<Document, Double> getKHigherScores(HashMap<Document, Double> similarityScores, Integer k) {
        List<Double> scores = new ArrayList<>(similarityScores.values());
        Collections.sort(scores, Collections.reverseOrder());
        int count = 0;
        HashMap<Document, Double> sortedMap = new HashMap<Document, Double>();
        for (double d: scores) {
            for(HashMap.Entry<Document, Double> entry: similarityScores.entrySet()){
                if (entry.getValue().equals(d)) {
                    sortedMap.put(entry.getKey(), entry.getValue());
                    ++count;
                    break;
                }
            }
            if (k == count) break;
        }
        return sortedMap;
    }

    public HashMap<Document, Double> getPWordListScores(
            ArrayList<String> wordList,
            ArrayList<Document> documentsList,
            Integer k) {
        HashMap<Document, Double> result = new HashMap<Document, Double>();
        for (Document d: documentsList){
            double relevance = 0.0;
            HashMap<String, Double> TFMap = d.getContingut().getTFMap();
            for (String word: wordList) {
                if (IDF.containsKey(word) && TFMap.containsKey(word)) relevance += IDF.get(word) * TFMap.get(word);
            }
            result.put(d, relevance);
        }

        return getKHigherScores(result, k);
    }
}