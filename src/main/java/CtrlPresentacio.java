import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CtrlPresentacio {

    private CtrlDomini cd;
    private VistaTerminal Vp;
    private InOut io;

    public CtrlPresentacio(){

    }

    public CtrlPresentacio(CtrlDomini cd, VistaTerminal vp, InOut io) {
        this.cd = cd;
        this.Vp = vp;
        this.io = io;
    }

    public void inicializeCtrlPresentacio() throws Exception {
        cd = new CtrlDomini();
        //cd.InicialitzarCtrlDomini();
        Vp = new VistaTerminal(this);
        Vp.InicialitzaVistaTerminal();
    }

    public Document importDocumentPlainText(String path) throws IOException {
        return cd.importDocumentPlainText(path);
    }

    public Llibreria getLlibreria(){
        return cd.getLlibreria();
    }

    public void addDocument(Document d) {
        cd.addDocument(d);
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
}
