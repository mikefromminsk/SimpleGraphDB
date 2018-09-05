package refactored;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IniFile {

    private Pattern section = Pattern.compile("\\s*\\[([^]]*)\\]\\s*");
    private Pattern keyValue = Pattern.compile("\\s*([^=]*)=(.*)");
    private String filePath = null;
    private Map<String, Map<String, String>> entries = new HashMap<>();

    public IniFile(String filePath) throws IOException {
        load(filePath);
    }

    public void load(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            String section = null;
            while ((line = br.readLine()) != null) {
                Matcher m = this.section.matcher(line);
                if (m.matches()) {
                    section = m.group(1).trim();
                } else if (section != null) {
                    m = keyValue.matcher(line);
                    if (m.matches()) {
                        String key = m.group(1).trim();
                        String value = m.group(2).trim();
                        Map<String, String> kv = entries.get(section);
                        if (kv == null)
                            entries.put(section, kv = new HashMap<>());
                        kv.put(key, value);
                    }
                }
            }
        }
        this.filePath = filePath;
    }

    public void save(String filePath) throws FileNotFoundException {
        File saveFile = new File(filePath);
        saveFile.delete();
        PrintWriter out = new PrintWriter(saveFile);
        for (String sectionKey : entries.keySet()) {
            out.println("[" + sectionKey + "]");
            Map<String, String> section = entries.get(sectionKey);
            for (String paramKey : section.keySet()) {
                String paramValue = section.get(paramKey);
                if (paramValue != null)
                    out.println(section + "=" + paramValue.replace("\n", ""));
            }
        }
    }

    public Map<String, Map<String, String>> getEntries() {
        return entries;
    }

    public Map<String, String> getSection(String section) {
        return entries.get(section);
    }

    public String get(String section, String key, String defaultvalue) {
        Map<String, String> kv = entries.get(section);
        if (kv == null)
            return defaultvalue;
        return kv.get(key);
    }

    public void put(String sectionKey, String key, String value) {
        Map<String, String> section = entries.get(sectionKey);
        if (section != null){
            section.put(key, value);
        }else{
            section = new HashMap<>();
            section.put(key, value);
            entries.put(sectionKey, section);
        }
    }
}