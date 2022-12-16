import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

public class Lang {

    private static final String LANG_NAME = "propper";

    private static ResourceBundle resources = ResourceBundle.getBundle(LANG_NAME, new Locale("en"));
    private static ArrayList<LangListener> listeners = new ArrayList<LangListener>();

    public static void registerListener(LangListener l) {
        listeners.add(l);
    }

    public static void unregisterListener(LangListener l) {
        listeners.remove(l);
    }

    public static String getString(String key) {
        try {
            return new String(resources.getString(key).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "MISSING";
        }
    }

    public static void changeLang(Language l) {
        resources = ResourceBundle.getBundle(LANG_NAME, new Locale(l.getCode()));
        for (LangListener listener : listeners) {
            listener.onLanguageChanged();
        }
    }

    public enum Language {
        CATALAN("Català", "ca"),
        ENGLISH("English", "en"),
        SPANISH("Español", "es");

        private final String name, code;

        Language(String name, String code) {
            this.name = name;
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }

        public static Language fromString(String s) {
            for (Language l : values()) {
                if (l.getName().equalsIgnoreCase(s))
                    return l;
            }
            throw new IllegalArgumentException(s + " language not supported");

        }
    }
}
