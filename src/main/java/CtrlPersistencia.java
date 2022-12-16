import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CtrlPersistencia {
    private static final Path PREFERENCES_FILE = Paths.get(".preferences.properties");
    private static final Path AUTOSAVE_FILE = Paths.get(".autosave.lm");

    public static void save(String path, String[] lb, String[] a) throws Exception {
        //deleteAutosaved();
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

    private static BufferedWriter open2Write(String path) throws FileNotFoundException, Exception {
        return new BufferedWriter(new FileWriter(path));
    }

    private static BufferedReader open2Read(String path) throws FileNotFoundException, Exception {
        return new BufferedReader(new FileReader(path)); //crear nuevo
    }

    public static String[] open(String path) throws Exception{
        BufferedReader br = open2Read(path);
        String[] s = new String[4];
        s[0] = br.readLine();
        s[1] = br.readLine();
        s[2] = br.readLine();
        s[3] = br.readLine();
        return s;
    }
}
