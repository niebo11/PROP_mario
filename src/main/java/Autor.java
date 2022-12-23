import java.util.ArrayList;
import java.util.Date;

/**
 * Esta clase representa el autor de una {@link Document}. Un autor tiene un
 * nombre y una lista de {@link Document} que han sido escritos por él.
 */
public class Autor {

    /**
     * Nombre del {@link Autor}.
     */
    private Frase name;

    /**
     * Lista de documentos escritos por el autor.
     */
    private ArrayList<Document> documentList;

    /**
     * Constructor básico.
     * @param name Nombre del {@link Autor}.
     */
    public Autor(Frase name) {
        if (name.isEmpty())
            throw new java.lang.IllegalArgumentException("Autor name cannot be empty.");
        this.name = name;
        documentList = new ArrayList<Document>();
    }

    /**
     * @return Devuelve el nombre del {@link Autor}.
     */
    public Frase getName() {
        return name;
    }

    /**
     * Cambia el nombre del {@link Autor}.
     * @param name Nuevo nombre.
     */
    public void setName(Frase name) {
        this.name = name;
    }

    public ArrayList<Document> getDocumentList() {
        return documentList;
    }

    public void addDocument(Document d) {
        documentList.add(d);
    }

    public void removeDocument(Document d){
        documentList.remove(d);
    }

    public int getNumberDocuments() {
        return documentList.size();
    }

    public boolean hasName(Frase a){
        return a.equals(name);
    }

    public boolean startsWith(String prefix) {
        return name.toString().startsWith(prefix);
    }

    @Override
    public String toString(){
        return name.toString();
    }

    public void updateDocument(String docName, Document newDoc) {
        for (int i = 0; i < documentList.size(); ++i) {
            if (documentList.get(i).getTitol().toString().equals(docName)) {
                documentList.set(i, newDoc);
            }
        }
    }
}