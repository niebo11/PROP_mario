import java.util.ArrayList;
import java.util.HashMap;

public class VistaTerminal {
    private CtrlPresentacio cp;

    public VistaTerminal(CtrlPresentacio ctrlPresentacio) {
        this.cp = ctrlPresentacio;
    }

    public void InicialitzaVistaTerminal() throws Exception {
        System.out.println("Welcome to our program \n");
        Document d = new Document();

        for (int i = 1; i < 6; ++i) {
            String path = "C:\\Users\\Equipo\\IdeaProjects\\PROP_mario\\test\\docs\\test";
            path = path + i + ".txt";
            d = cp.importDocumentPlainText(path);
            cp.addDocument(d);
        }
        Calculator c = new Calculator();
        HashMap<Document, Double> add = cp.getMostSimilarDocuments(d, 1);
        c.updateIDF(cp.getLlibreria().getDocuments());
    }
}
