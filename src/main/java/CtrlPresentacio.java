import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;
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

    public void importDocumentPlainText(String path) throws IOException {
        cd.importDocumentPlainText(path);
    }

    public Llibreria getLlibreria(){
        return cd.getLlibreria();
    }

    public void addDocument(Document d) {
        cd.addDocument(d);
    }

    public Boolean parserExpression(String expression, Document d) {
        return cd.parserExpression(expression, d);
    }

    public boolean hasDocument(Document d) {
        return cd.hasDocument(d);
    }

    public void removeDocument(Document d) {
        cd.removeDocument(d);
    }

    public void changeAutorDocument(Document d, Frase name) {
        cd.changeAutorDocument(d, name);
    }

    public void changeTitolDocument(Document d, Frase name) {
        cd.changeTitolDocument(d, name);
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

    public Object[][] getDocumentosData(){
        ArrayList<Document> documents = cd.getDocuments();
        Object[][] d = new Object[documents.size()][2];
        for (int i = 0; i < documents.size(); ++i){
            d[i][0] = documents.get(i).getTitol();
            d[i][1] = documents.get(i).getAutor();
        }
        return d;
    }

    public Object[][] getAutorsData() {
        ArrayList<Autor> autors = cd.getAutors();
        Object[][] d = new Object[autors.size()][3];
        for (int i = 0; i < autors.size(); ++i) {
            Autor a = autors.get(i);
            d[i][0] = a.getName().toString();
            d[i][1] = Integer.toString(a.getDocumentList().size());
            d[i][2] = a.getDocumentList().toString();
        }
        return d;
    }

    public void setContent(Document d, String s) { cd.setContent(d, s); }

    public void errorManagement(String s){
        JOptionPane.showMessageDialog(new JFrame("error"),
                s, "error", JOptionPane.ERROR_MESSAGE);

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

}
