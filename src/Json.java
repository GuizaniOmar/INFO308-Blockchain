import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Json {

    public static String serialize(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(quote(entry.getKey())).append(":").append(valueToString(entry.getValue())).append(",");
        }
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 1);
        }
        sb.append("}");
        return sb.toString();
    }

    public static Object deserialize(String json) {
        Map<String, Object> map = new HashMap<String, Object>();
        Pattern p = Pattern.compile("\\{([^\\}]+)\\}");
        Matcher m = p.matcher(json);
        if (m.find()) {
            String[] pairs = m.group(1).split(",");
            for (String pair : pairs) {
                String[] keyValue = pair.split(":");
                String key = stripQuotes(keyValue[0].trim());
                String value = keyValue[1].trim();
                if (value.startsWith("{")) {
                    map.put(key, deserialize(value));
                } else {
                    map.put(key, stripQuotes(value));
                }
            }
        }
        return map;
    }
    public static List<String> extraireMots(String chaine) {
        List<String> mots = new ArrayList<String>();
        Pattern pattern = Pattern.compile("\\|(.*?)\\|");
        Matcher matcher = pattern.matcher(chaine);
        while (matcher.find()) {
            mots.add(matcher.group(1));
        }
        return mots;
    }
    private static String stripQuotes(String str) {
        if (str.startsWith("\"") && str.endsWith("\"")) {
            return str.substring(1, str.length() - 1);
        }
        return str;
    }

    private static String valueToString(Object value) {
        if (value instanceof Map) {
            return serialize((Map<String, Object>) value);
        } else if (value instanceof String) {
            return quote((String) value);
        }
        return value.toString();
    }

    private static String quote(String str) {
        return "\"" + str + "\"";
    }
}
