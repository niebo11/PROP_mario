import java.util.Date;
import java.util.*;

public class Document {

    // Titol is the document name
    private Frase titol;

    // Autor is the autor of the document
    private Frase autor;

    //
    private String path;

    // Editable returns true if the document is editable false otherwise, by default true
    private boolean editable;

    // A document can have a code for manage who is editing
    private String codi;

    //DateAlta returns the date when the document was first uploaded

    private Date dataAlta;
    private Contingut contingut;


    public Document() {

    }
    //Constructor of class document

    public Document(Frase titol, Frase autor) {
        this.titol = titol;
        this.autor = autor;
        path = null;
        editable = false;
        this.codi = "";
        dataAlta = new Date();
        contingut = new Contingut();
    }

    public Document(Frase titol, Frase autor, String path) {
        this.titol = titol;
        this.autor = autor;
        this.path = path;
        editable = false;
        this.codi = "";
        dataAlta = new Date();
        contingut = new Contingut();
    }

    public Document(Frase titol, Frase autor, Contingut c) {
        this.titol = titol;
        this.autor = autor;
        path = null;
        editable = false;
        this.codi = "";
        dataAlta = new Date();
        contingut = c;
    }

    public Document(Frase titol, Frase autor, Contingut c, String path) {
        this.titol = titol;
        this.autor = autor;
        this.path = path;
        editable = false;
        this.codi = "";
        dataAlta = new Date();
        contingut = c;
    }

    public Frase getTitol() {
        return titol;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setTitol(Frase titol) {
        this.titol = titol;
    }

    public Frase getAutor() {
        return autor;
    }

    public void setAutor(Frase name) { autor = name; }

    public String getCodi() {
        return codi;
    }

    public void setCodi(String codi) {
        codi = codi;
    }

    public void removeCodi() {
        codi = "";
    }

    public void setEditable() {
        editable = true;
    }

    public void DocumentNoEditable() {
        editable = false;
    }

    public void setContingut(Contingut contingut) {
        this.contingut = contingut;
    }

    public void setContingut(String contingut) {
        this.contingut.setContingut(contingut);
    }

    public Contingut getContingut() {
        return contingut;
    }

    @Override
    public String toString() {
        return titol.toString();
    }

    public boolean hasToken(String token) {
        return contingut.hasToken(token);
    }

    public boolean hasSentence(String sentence) {
        return contingut.hasSentence(sentence);
    }

    @Override
    public boolean equals(Object o){
        Document aux = (Document) o;
        return (aux.getAutor().equals(this.autor) && aux.getTitol().equals(this.titol));
    }
}