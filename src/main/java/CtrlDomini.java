import com.google.gson.Gson;

import javax.print.Doc;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import org.xml.sax.SAXException;

import java.security.InvalidKeyException;
import java.util.*;
import java.util.List;

public class CtrlDomini {

    private static CtrlDomini instance;

    public static final String TXT = ".txt";

    private Llibreria lb;

    private Algorithm c;

    private String path;

    private BooleanParser booleanParser;

    Timer autosavetimer;
    public CtrlDomini() {
        lb = new Llibreria();
        try {
            c = new Algorithm(CtrlPersistencia.openStopWords());
        } catch (Exception e) {}
        autosavetimer = new Timer();
        booleanParser = new BooleanParser();
        if (CtrlPersistencia.existsAutosaved()) {
            try {
                CtrlPersistencia.openAutosaved();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        setAutoSave();
    }

    public void importDocumentPlainText(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        Frase autor = new Frase(br.readLine());
        Frase titol = new Frase(br.readLine());
        Document to_add = new Document(autor, titol, path);
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

    public void importDocumentXML(String path) throws IOException, ParserConfigurationException, SAXException {
        File f = new File(path);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        org.w3c.dom.Document d = db.parse(f);
        String autor, titol, content;
        try {
            autor = d.getElementsByTagName("author").item(0).getTextContent();
            titol = d.getElementsByTagName("title").item(0).getTextContent();
            content = d.getElementsByTagName("content").item(0).getTextContent();
        } catch (Exception e) {
            throw new InvalidPropertiesFormatException("invalid xml format");
        }
        Contingut c = new Contingut(content);
        Document to_add = new Document(
                new Frase(autor),
                new Frase(titol),
                c,
                path
        );
        lb.addDocument(to_add);
        this.c.updateIDF(lb.getDocuments());
    }

    public void importDocument(String path) throws IOException, ParserConfigurationException, SAXException {
        if (path.toLowerCase().endsWith(".txt")) {importDocumentPlainText(path);}
        else if (path.toLowerCase().endsWith(".xml")) {importDocumentXML(path);}
    }

    public Boolean parserExpression(String titol, String autor, String expression){
        Document d = getDocumentFromNameAutor(titol, autor);
        return booleanParser.parserBooleanExpresion(expression, d);
    }

    public Llibreria getLlibreria(){
        return lb;
    }

    public void addDocument(Document d){
        lb.addDocument(d);
        c.updateIDF(lb.getDocuments());
    }

    public  void addDocument(String titol, String autor, String content) {
        Document to_add = new Document(
                new Frase(titol),
                new Frase(autor),
                new Contingut(content)
        );
        lb.addDocument(to_add);
        c.updateIDF(lb.getDocuments());
    }

    public boolean hasDocument(String titol, String autor) {
        Document tempt = getDocumentFromNameAutor(titol, autor);
        if (tempt == null) return false;
        return true;
    }

    public boolean hasDocument(Document d) {
        return lb.hasDocument(d);
    }

    public void removeDocument(String titol, String autor) {
        Document to_remove = getDocumentFromNameAutor(titol, autor);
        lb.removeDocument(to_remove);
        c.updateIDF(lb.getDocuments());
    }

    public void changeAutorDocument(String titol, String autor, String newName){
        Document to_change = getDocumentFromNameAutor(titol, autor);
        lb.changeAutorDocument(
                to_change,
                new Frase(newName)
        );
    }

    public void changeTitolDocument(String titol, String autor, String newTitol) {
        System.out.println(titol);
        System.out.println(newTitol);
        Document to_change = getDocumentFromNameAutor(titol, autor);
        to_change = lb.changeTitolDocument(
                to_change,
                new Frase(newTitol)
        );
        lb.updateAutorDocuments(autor, titol, to_change);
    }

    public String[][] getDocumentsData() {
        ArrayList<Document> documents = lb.getDocuments();
        String[][] d = new String[documents.size()][2];
        for (int i = 0; i < documents.size(); ++i){
            d[i][0] = documents.get(i).getTitol().toString();
            d[i][1] = documents.get(i).getAutor().toString();
        }
        return d;
    }

    public String[][] getAutorsData() {
        ArrayList<Autor> autors = lb.getAutors();
        String[][] d = new String[autors.size()][3];
        for (int i = 0; i < autors.size(); ++i) {
            Autor a = autors.get(i);
            d[i][0] = a.getName().toString();
            d[i][1] = Integer.toString(a.getDocumentList().size());
            d[i][2] = a.getDocumentList().toString();
        }
        return d;
    }

    public String[] getDocumentListFromAutor(String autor) {
        Autor a = lb.getAutor(autor);
        ArrayList<Document> documents = a.getDocumentList();
        String[] documentsName = new String[documents.size()];
        int idx = 0;
        for (Document d: documents) {
            documentsName[idx] = d.getTitol().toString();
            ++idx;
        }
        Arrays.sort(documentsName);
        return documentsName;
    }

    public Boolean hasEqualContent(String titol, String autor, String content) {
        Document to_compare = getDocumentFromNameAutor(titol, autor);
        String toCompare = to_compare.getContingut().toString();
        return toCompare.equals(content);
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

    public String[][] getPwords(String words, Integer k) {
        Document aux = new Document(
                new Frase("aux"),
                new Frase("aux"),
                new Contingut(words.replace(",", " "))
        );
        HashMap<Document, Double> relevance = getMostSimilarDocuments(aux, k);
        return translateHashMap(relevance);
    }

    public String[][] getKSimilarDocs(String titol, String autor, Integer k) {
        Document to_search = getDocumentFromNameAutor(titol, autor);
        HashMap<Document, Double> relevance = getMostSimilarDocuments(to_search, k);
        return translateHashMap(relevance);
    }

    public String[][] translateHashMap(HashMap<Document, Double> relevance) {
        String[][] result = new String[relevance.size()][3];
        int i = 0;
        for (HashMap.Entry<Document, Double> entry: relevance.entrySet()) {
            result[i][0] = entry.getKey().getTitol().toString();
            result[i][1] = entry.getKey().getAutor().toString();
            result[i][2] = Double.toString(entry.getValue());
            i++;
        }
        return result;
    }

    public String[] getSearches() {
        return booleanParser.get_recent_searches();
    }

    public ArrayList<Document> getDocuments(){
        return lb.getDocuments();
    }

    public Integer getNumDocuments(){ return lb.getDocuments().size(); }

    public Document getDocumentFromNameAutor(String name, String autor) {
        return lb.getDocumentFromNameAutor(name, autor);
    }

    public void setContent(String titol, String autor, String s) {
        Document d = getDocumentFromNameAutor(titol, autor);
        lb.setContent(d, s);
    }

    public ArrayList<Autor> getAutors(){
        return lb.getAutors();
    }

    public Autor getAutor(String name) {
        return lb.getAutor(name);
    }

    public void save(String path) throws IOException, Exception {
        this.path = path;
        CtrlPersistencia.save(path, serializeLibrary(), serializeAlgorithm());
        //CtrlPersistencia.deleteAutosaved();
    }

    public Boolean saveDocument(String titol, String autor) throws Exception {
        Document d = getDocumentFromNameAutor(titol, autor);
        Contingut c = d.getContingut();
        String path = d.getPath();
        if (path == null) return false;
        if (path.endsWith("txt")) {
            CtrlPersistencia.saveDocumentPlainText(titol, autor, c.toString(), path);
        }
        else if (path.endsWith("xml")) {
            CtrlPersistencia.saveDocumentXML(titol, autor, c.toString(), path);
        }
        return true;
    }

    public Boolean saveDocument(String titol, String autor, String path) throws Exception {
        Document d = getDocumentFromNameAutor(titol, autor);
        Contingut c = d.getContingut();
        d.setPath(path);
        System.out.println(path);
        if (path.endsWith("txt")) {
            CtrlPersistencia.saveDocumentPlainText(titol, autor, c.toString(), path);
        }
        else if (path.endsWith("xml")) {
            CtrlPersistencia.saveDocumentXML(titol, autor, c.toString(), path);
        }
        return true;
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

    public void autoSave() throws Exception {
        CtrlPersistencia.autoSave(serializeLibrary(), serializeAlgorithm());
    }

    public void setAutoSave(){
        autosavetimer.cancel();
        autosavetimer.purge();
        autosavetimer = new Timer();
        TimerTask autosavetask = new TimerTask() {
            public void run() {
                try {
                    autoSave();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        autosavetimer.scheduleAtFixedRate(autosavetask, 0, 10*1000);
    }
}
