import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Collections;
import java.util.List;

public class CtrlDomini {

    private static CtrlDomini instance;

    public static final String TXT = ".txt";

    private Llibreria lb;

    private Calculator c;

    public CtrlDomini(){
        lb = new Llibreria();
        c = new Calculator();
    }

    public CtrlDomini(HashMap<String, Document> documents, HashMap<String, Author> authors) {
        lb = new Llibreria();
        c = new Calculator();
    }

    public Document importDocumentPlainText(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        StringBuilder sb = new StringBuilder();
        Frase autor = new Frase(br.readLine());
        Frase titol = new Frase(br.readLine());
        Contingut c = new Contingut();
        String line = br.readLine();
        while (line != null) {
            c.addFrase(new Frase(line));
            line = br.readLine();
        }
        return new Document(autor, titol, c);
    }

    public Document importDocumentXML(String path) throws IOException {
        return new Document(); //TODO
    }

    public Llibreria getLlibreria(){
        return lb;
    }

    public void addDocument(Document d){
        lb.addDocument(d);
        c.updateIDF(lb.getDocuments());
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
            if (d != d2) {
                similarityScores.put(d2, c.computeCosineSimilarity(documentScoreVector, d2));
            }
        }
        if (k > similarityScores.size()) return similarityScores;
        else {
            return c.getKHigherScores(similarityScores, k);
        }
    }
}
