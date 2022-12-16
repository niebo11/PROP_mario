import com.google.gson.Gson;

import javax.print.Doc;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import java.util.*;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.List;

public class CtrlDomini {

    private static CtrlDomini instance;

    public static final String TXT = ".txt";

    private Llibreria lb;

    private Algorithm c;

    private String path;

    private BooleanParser booleanParser;

    public CtrlDomini(){
        lb = new Llibreria();
        c = new Algorithm();
        booleanParser = new BooleanParser();
    }

    public CtrlDomini(HashMap<String, Document> documents, HashMap<String, Autor> authors) {
        lb = new Llibreria();
        c = new Algorithm();
    }

    public void importDocumentPlainText(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        StringBuilder sb = new StringBuilder();
        Frase autor = new Frase(br.readLine());
        Frase titol = new Frase(br.readLine());
        Document to_add = new Document(autor, titol);
        if (lb.hasDocument(to_add)) {throw new IOException("Duplicated document");}
        Contingut c = new Contingut();
        String line = br.readLine();
        while (line != null) {
            c.addFrase(new Frase(line));
            line = br.readLine();
        }
        to_add.setContingut(c);
        lb.addDocument(to_add);
        this.c.updateIDF(lb.getDocuments());
    }

    public Document importDocumentXML(String path) throws IOException {
        return new Document(); //TODO
    }

    public Boolean parserExpression(String expression, Document d){
        return booleanParser.parserBooleanExpresion(expression, d);
    }

    public Llibreria getLlibreria(){
        return lb;
    }

    public void addDocument(Document d){
        lb.addDocument(d);
        c.updateIDF(lb.getDocuments());
    }

    public boolean hasDocument(Document d) {
        return lb.hasDocument(d);
    }

    public void removeDocument(Document d){
        lb.removeDocument(d);
        c.updateIDF(lb.getDocuments());
    }

    public void changeAutorDocument(Document d, Frase newName){
        lb.changeAutorDocument(d, newName);
    }

    public void changeTitolDocument(Document d, Frase name) {
        lb.changeTitolDocument(d, name);
    }

    public ArrayList<Document> getDocumentFromAutor(Frase name) {
        return lb.getDocumentFromAutor(name);
    }

    public ArrayList<String> getPrefixAutor(String prefix) {
        return lb.getPrefixAutor(prefix);
    }

    public HashMap<Document, Double> getMostSimilarDocuments(Document d, Integer k){
        HashMap<Document, Double> similarityScores = new HashMap<Document, Double>();
        HashMap<String, Double> documentScoreVector = c.computeWordScore(d);
        for (Document d2: lb.getDocuments()) {
            if (!d.equals(d2)) {
                similarityScores.put(d2, c.computeCosineSimilarity(documentScoreVector, d2));
            }
        }
        if (k > similarityScores.size()) return similarityScores;
        else {
            return c.getKHigherScores(similarityScores, k);
        }
    }

    public ArrayList<Document> getDocuments(){
        return lb.getDocuments();
    }

    public Integer getNumDocuments(){ return lb.getDocuments().size(); }

    public Document getDocumentFromNameAutor(String name, String autor) {
        return lb.getDocumentFromNameAutor(name, autor);
    }

    public void setContent(Document d, String s) { lb.setContent(d, s); }

    public ArrayList<Autor> getAutors(){
        return lb.getAutors();
    }

    public void save(String path) throws IOException, Exception {
        this.path = path;
        CtrlPersistencia.save(path, serializeLibrary(), serializeAlgorithm());
        //CtrlPersistencia.deleteAutosaved();
    }

    public String[] serializeLibrary() {
        Gson gson = new Gson();
        Document[] dc = lb.getDocuments().toArray(new Document[lb.getNumberDocuments()]);
        Autor[] a = lb.getAutors().toArray(new Autor[lb.getNumberAutors()]);
        return new String[]{gson.toJson(dc), gson.toJson(a)};
    }

    public String[] serializeAlgorithm() {
        Gson gson = new Gson();
        Integer i = c.getTotalWords();
        HashMap<String, Double> idf = c.getIDF();
        return new String[]{gson.toJson(idf), gson.toJson(i)};
    }

    public CtrlDomini reset() {
        instance = new CtrlDomini();
        return instance;
    }

    public void open(String path) throws Exception {
        String[] s = CtrlPersistencia.open(path);
        deserialize(s, path);
    }

    public void deserialize(String[] s, String path) throws Exception {
        deserializeDocuments(s[0], s[1]);
        deserializeAlgorithm(s[2], s[3]);
        this.path = path;
    }

    public void deserializeDocuments(String documents, String autors) throws Exception {
        Gson gson = new Gson();
        Document[] dc = gson.fromJson(documents, Document[].class);
        lb.setDocuments(new ArrayList<Document>(Arrays.asList(dc)));
        Autor[] aut = gson.fromJson(autors, Autor[].class);
        lb.setAutors(new ArrayList<Autor>(Arrays.asList(aut)));
    }

    public void deserializeAlgorithm(String idf, String totalwords){
        Gson gson = new Gson();
        int i = Integer.parseInt(totalwords);
        Type type = new TypeToken<HashMap<String, Double>>(){}.getType();
        HashMap<String, Double> des_idf = gson.fromJson(idf, type);
        c.setIDF(des_idf);
        c.setTotalWords(i);
    }
}
