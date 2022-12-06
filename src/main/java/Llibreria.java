import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Llibreria {

    private ArrayList<Autor> autors;

    private ArrayList<Document> documents;

    public Llibreria() {
        autors = new ArrayList<Autor>();
        documents = new ArrayList<Document>();
    }

    public void addDocument(Document d) {
        documents.add(d);
        Frase name = d.getAutor();
        Autor autor = getAutor(name);
        if (autor != null) {
            autor.addDocument(d);
        }
        else{
            autor = new Autor(name);
            autor.addDocument(d);
            autors.add(autor);
        }
    }

    public ArrayList<Document> getDocuments() {
        return documents;
    }

    public ArrayList<Autor> getAutors(){
        return autors;
    }

    public void removeDocument(Document d) {
        if (!documents.contains(d)) {
            throw new java.lang.IllegalArgumentException("El document no existeix a la llibreria");
        }
        documents.remove(d);
        Frase name = d.getAutor();
        Autor autor = getAutor(name);
        autor.removeDocument(d);
        updateAutors(autor);
    }

    public void changeTitolDocument(Document d, Frase name) {
        if (!documents.contains(d)) {
            throw new java.lang.IllegalArgumentException("El document no existeix a la llibreria");
        }
        d.setTitol(name);
    }

    public void changeAutorDocument(Document d, Frase name) {
        if (!documents.contains(d)) {
            throw new java.lang.IllegalArgumentException("El document no existeix a la llibreria");
        }
        Autor autor = getAutor(name);
        if (autor == null) {
            autor = new Autor(name);
            autors.add(autor);
        }
        autor.addDocument(d);
        Autor oldAutor = getAutor(d.getAutor());
        d.setAutor(autor.getName());
        oldAutor.removeDocument(d);
        updateAutors(oldAutor);
    }

    public ArrayList<Document> getDocumentFromAutor(Frase name) {
        Autor a = getAutor(name);
        if (a == null) return new ArrayList<Document>();
        ArrayList<Document> documentListAutor = a.getDocumentList();
        return documentListAutor;
    }

    public ArrayList<String> getPrefixAutor(String prefix) {
        ArrayList<String> result = new ArrayList<String>();
        for (Autor a: autors) {
            if (a.startsWith(prefix)) result.add(a.getName().toString());
        }
        return result;
    }

    private void updateAutors(Autor a){
        if(a.getNumberDocuments() == 0) {
            autors.remove(a);
        }
    }

    private Autor getAutor(Frase f){
        for (Autor a: autors){
            if (a.hasName(f)) return a;
        }
        return null;
    }

    @Override
    public String toString() {
        return documents.stream().map(document->document.toString()).collect(Collectors.toList()).toString();
    }
}
