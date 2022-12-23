import javax.lang.model.element.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class CtrlPersistencia {
    private static final Path AUTOSAVE_FILE = Paths.get("autosave.lm");
    private static final Path STOP_WORDS = Paths.get("StopWords");

    public static void save(String path, String[] lb, String[] a) throws Exception {
        deleteAutosaved();
        BufferedWriter bw = open2Write(path);
        StringBuilder s = new StringBuilder();
        s.append(lb[0]);
        s.append("\n");
        s.append(lb[1]);
        s.append("\n");
        s.append(a[0]);
        s.append("\n");
        s.append(a[1]);
        bw.write(s.toString());
        bw.flush();
        bw.close();
    }

    public static void saveDocumentPlainText(String titol, String autor, String content, String path) throws Exception {
        BufferedWriter bw = open2Write(path);
        StringBuilder s = new StringBuilder();
        s.append(titol);
        s.append("\n");
        s.append(autor);
        s.append("\n");
        s.append(content);
        bw.write(s.toString());
        bw.flush();
        bw.close();
    }

    public static void saveDocumentXML(String titol, String autor, String content, String path) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        org.w3c.dom.Document doc = db.newDocument();

        org.w3c.dom.Element rootElement = doc.createElement("document");
        org.w3c.dom.Element a = doc.createElement("author");
        a.setTextContent(autor);
        org.w3c.dom.Element t = doc.createElement("title");
        t.setTextContent(titol);
        org.w3c.dom.Element c = doc.createElement("content");
        c.setTextContent(content);

        rootElement.appendChild(t);
        rootElement.appendChild(a);
        rootElement.appendChild(c);
        doc.appendChild(rootElement);

        try (FileOutputStream output =
                     new FileOutputStream(path)) {
            writeXml(doc, output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeXml(org.w3c.dom.Document doc,
                                 OutputStream output)
            throws TransformerException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);

        transformer.transform(source, result);

    }

    private static BufferedWriter open2Write(String path) throws FileNotFoundException, Exception {
        return new BufferedWriter(new FileWriter(path));
    }

    private static BufferedReader open2Read(String path) throws FileNotFoundException, Exception {
        return new BufferedReader(new FileReader(path)); //crear nuevo
    }

    public static String[] open(String path) throws Exception{
        deleteAutosaved();
        BufferedReader br = open2Read(path);
        String[] s = new String[4];
        s[0] = br.readLine();
        s[1] = br.readLine();
        s[2] = br.readLine();
        s[3] = br.readLine();
        return s;
    }

    public static Set<String> openStopWords() throws Exception {
        BufferedReader br = open2Read(STOP_WORDS.toString());
        Set<String> result = new HashSet<String>();
        for(String line = br.readLine(); line!= null; line= br.readLine()){
            result.add(line);
        }
        return result;
    }

    public static boolean existsAutosaved() {
        return new File(AUTOSAVE_FILE.toString()).exists();
    }

    public static String[] openAutosaved() throws Exception {

        return open(AUTOSAVE_FILE.toString());
    }

    public static void deleteAutosaved() {
        try {
            Files.delete(FileSystems.getDefault().getPath(AUTOSAVE_FILE.toString()));
        } catch (IOException ignored) {
        }
    }

    public static void autoSave(String[] lb, String[] a) throws Exception {
        File f = new File(AUTOSAVE_FILE.toString());
        f.mkdirs();
        save(AUTOSAVE_FILE.toString(), lb, a);
    }

}
