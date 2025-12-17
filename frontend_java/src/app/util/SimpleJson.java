package app.util;

/**
 * A very simple utility class for parsing flat JSON strings.
 * This parser is highly basic and is not intended for complex, nested, or malformed JSON.
 * It only supports extracting top-level string, integer, and boolean values.
 * For robust JSON parsing, consider using libraries like Jackson or Gson.
 */
public class SimpleJson {

    /**
     * Extracts a string value associated with a given key from a flat JSON string.
     * Assumes the JSON is well-formed and the value is a string enclosed in double quotes.
     * Example: {"key":"value"}
     *
     * @param json The JSON string to parse.
     * @param key The key whose string value is to be extracted.
     * @return The string value associated with the key, or an empty string if the key is not found
     *         or the value cannot be parsed as a string.
     */
    public static String getString(String json, String key) {
        String pat = "\"" + key + "\"";
        int k = json.indexOf(pat);
        if (k < 0) return "";
        int colon = json.indexOf(':', k + pat.length()); // Search colon after the key
        if (colon < 0) return "";

        // Find the start of the string value, skipping whitespace
        int q1 = -1;
        for (int i = colon + 1; i < json.length(); i++) {
            char c = json.charAt(i);
            if (Character.isWhitespace(c)) continue;
            if (c == '"') {
                q1 = i;
                break;
            }
            // If we find something else before a quote, it's not a string value
            return "";
        }
        if (q1 < 0) return "";
        int q2 = json.indexOf('"', q1 + 1);
        if (q2 < 0) return "";
        return json.substring(q1 + 1, q2);
    }

    /**
     * Extracts an integer value associated with a given key from a flat JSON string.
     * Assumes the JSON is well-formed and the value is an integer.
     * Example: {"key":123} or {"key": -456}
     *
     * @param json The JSON string to parse.
     * @param key The key whose integer value is to be extracted.
     * @param def The default value to return if the key is not found or the value cannot be parsed as an integer.
     * @return The integer value associated with the key, or the default value if parsing fails.
     */
    public static int getInt(String json, String key, int def) {
        String pat = "\"" + key + "\"";
        int k = json.indexOf(pat);
        if (k < 0) return def;
        int colon = json.indexOf(':', k + pat.length()); // Search colon after the key
        if (colon < 0) return def;

        int i = colon + 1;
        while (i < json.length() && Character.isWhitespace(json.charAt(i))) i++;

        int startNum = i;
        while (i < json.length() && (Character.isDigit(json.charAt(i)) || json.charAt(i) == '-')) {
            i++; // Find end of number
        }
        String numStr = json.substring(startNum, i).trim();

        try {
            return Integer.parseInt(numStr);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    /**
     * Extracts a boolean value associated with a given key from a flat JSON string.
     * Assumes the JSON is well-formed and the value is either "true" or "false" (case-sensitive).
     * Example: {"key":true}
     *
     * @param json The JSON string to parse.
     * @param key The key whose boolean value is to be extracted.
     * @param def The default value to return if the key is not found or the value cannot be parsed as a boolean.
     * @return The boolean value associated with the key, or the default value if parsing fails.
     */
    public static boolean getBool(String json, String key, boolean def) {
        String pat = "\"" + key + "\"";
        int k = json.indexOf(pat);
        if (k < 0) return def;
        int colon = json.indexOf(':', k + pat.length()); // Search colon after the key
        if (colon < 0) return def;
        int i = colon + 1;
        while (i < json.length() && Character.isWhitespace(json.charAt(i))) i++;
        if (json.startsWith("true", i)) return true;
        if (json.startsWith("false", i)) return false;
        return def;
    }

    /**
     * Escapes a string so it can be safely embedded as a JSON string value.
     * It escapes backslashes, double quotes, and newlines.
     *
     * @param s The string to escape.
     * @return The escaped string.
     */
    public static String esc(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}