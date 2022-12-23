import org.xml.sax.SAXException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;

public class CtrlPresentacio {

    private CtrlDomini cd;
    private MainWindow mainwindow;

    public CtrlPresentacio() {
        this.cd = new CtrlDomini();
        mainwindow = new MainWindow(this);
    }

    public Document getDocumentFromNameAutor(String name, String autor) {
        return cd.getDocumentFromNameAutor(name, autor);
    }

    public void importDocument(String path) throws IOException, ParserConfigurationException, SAXException {
        cd.importDocument(path);
    }

    public Llibreria getLlibreria(){
        return cd.getLlibreria();
    }

    public void addDocument(String titol, String autor, String content) {
        cd.addDocument(titol, autor, content);
    }

    public Boolean parserExpression(String titol, String autor, String expression) {
        return cd.parserExpression(titol, autor, expression);
    }

    public boolean hasDocument(String titol, String autor) {
        return cd.hasDocument(titol, autor);
    }

    public void removeDocument(String titol, String autor) {
        cd.removeDocument(titol, autor);
    }

    public void changeAutorDocument(String titol, String autor, String newAutor) {
        cd.changeAutorDocument(titol, autor, newAutor);
    }

    public void changeTitolDocument(String titol, String autor, String newTitol) {
        cd.changeTitolDocument(titol, autor, newTitol);
    }

    public ArrayList<Document> getDocumentFromAutor(Frase name) {
        return cd.getDocumentFromAutor(name);
    }

    public ArrayList<String> getPrefixAutor(String prefix) {
        return cd.getPrefixAutor(prefix);
    }

    public HashMap<Document, Double> getMostSimilarDocuments(Document d, Integer k){
        return cd.getMostSimilarDocuments(d, k);
    }

    public String[][] getMostSimilarDocument(String titol, String autor, Integer k) {
        return cd.getKSimilarDocs(titol, autor, k);
    }

    public String[][] getPwords(String words, Integer k) {
        return cd.getPwords(words, k);
    }

    public Autor getAutor(String name) {
        return cd.getAutor(name);
    }

    public String[][] getDocumentosData(){
        return cd.getDocumentsData();
    }

    public String[][] getAutorsData() {
        return cd.getAutorsData();
    }

    public void setContent(String titol, String autor, String s) { cd.setContent(titol, autor, s); }

    public Boolean saveDocument(String titol, String autor) throws Exception {
        return cd.saveDocument(titol, autor);
    }

    public Boolean saveDocument(String titol, String autor, String path) throws Exception {
        return cd.saveDocument(titol, autor, path);
    }

    public void errorManagement(String s){
        JOptionPane.showMessageDialog(new JFrame("error"),
                s, "error", JOptionPane.ERROR_MESSAGE);

    }

    public Boolean hasEqualContent(String titol, String autor, String content) {
        return cd.hasEqualContent(titol, autor, content);
    }

    public void save(String path) {
        try{
            cd.save(path);
        } catch (IOException E){errorManagement("errorguardando");
        } catch (Exception E){
            errorManagement("errorguardando");
            E.printStackTrace();
        }
    }

    public void open(String path) {
        try{
            cd = cd.reset();
            System.gc();
            cd.open(path);
            System.out.println("disposing");
            mainwindow.dispose();
            System.out.println("creating");
            mainwindow = new MainWindow(this);
            System.out.println("succesfully created");
        } catch (IOException E){errorManagement("Error occured at opening.");
        } catch (Exception E){
            errorManagement("Error occured at opening.");
            E.printStackTrace();
        }
    }

    public String[] getDocumentListFromAutor(String autor){
        return cd.getDocumentListFromAutor(autor);
    }

    public String[] getSearches() {return cd.getSearches();}

}
