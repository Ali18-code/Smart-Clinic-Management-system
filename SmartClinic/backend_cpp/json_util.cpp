#include "json_util.h"
#include <fstream>
#include <sstream>

namespace sj {

    // Read the whole file into a string
    std::string readAllText(const std::string& path) {

        std::ifstream file(path.c_str());

        // If file could not open, return empty string
        if (!file.is_open()) {
            return "";
        }

        std::stringstream buffer;
        buffer << file.rdbuf();

        return buffer.str();
    }

    // Write text into a file (overwrite mode)
    bool writeAllText(const std::string& path, const std::string& text) {

        std::ofstream file(path.c_str());

        if (!file.is_open()) {
            return false;
        }

        file << text;

        return true;
    }

    // Helper: find the position of "key" inside json text
    int findKey(const std::string& json, const std::string& key) {

        std::string pattern = "\"" + key + "\"";

        return (int)json.find(pattern);
    }

    // Read a string value from json like: "key" : "value"
    std::string getString(const std::string& json, const std::string& key) {

        int keyPos = findKey(json, key);
        if (keyPos < 0) return "";

        int colonPos = (int)json.find(":", keyPos);
        if (colonPos < 0) return "";

        int firstQuote = (int)json.find("\"", colonPos + 1);
        if (firstQuote < 0) return "";

        int secondQuote = (int)json.find("\"", firstQuote + 1);
        if (secondQuote < 0) return "";

        return json.substr(firstQuote + 1, secondQuote - firstQuote - 1);
    }

    // Read an integer value from json
    int getInt(const std::string& json, const std::string& key, int def) {

        int keyPos = findKey(json, key);
        if (keyPos < 0) return def;

        int colonPos = (int)json.find(":", keyPos);
        if (colonPos < 0) return def;

        int i = colonPos + 1;

        // skip spaces
        while (i < (int)json.size() &&
              (json[i] == ' ' || json[i] == '\n' ||
               json[i] == '\r' || json[i] == '\t')) {
            i++;
        }

        bool negative = false;

        if (i < (int)json.size() && json[i] == '-') {
            negative = true;
            i++;
        }

        long long value = 0;
        bool foundDigit = false;

        while (i < (int)json.size() &&
               json[i] >= '0' && json[i] <= '9') {

            foundDigit = true;
            value = value * 10 + (json[i] - '0');
            i++;
        }

        if (!foundDigit) return def;

        if (negative) value = -value;

        return (int)value;
    }

    // Read true / false from json
    bool getBool(const std::string& json, const std::string& key, bool def) {

        int keyPos = findKey(json, key);
        if (keyPos < 0) return def;

        int colonPos = (int)json.find(":", keyPos);
        if (colonPos < 0) return def;

        int i = colonPos + 1;

        // skip spaces
        while (i < (int)json.size() &&
              (json[i] == ' ' || json[i] == '\n' ||
               json[i] == '\r' || json[i] == '\t')) {
            i++;
        }

        if (json.compare(i, 4, "true") == 0) {
            return true;
        }

        if (json.compare(i, 5, "false") == 0) {
            return false;
        }

        return def;
    }

    // Escape characters inside JSON string values
    std::string esc(const std::string& s) {

        std::string result;

        for (int i = 0; i < (int)s.size(); i++) {

            char c = s[i];

            if (c == '\\') result += "\\\\";
            else if (c == '"') result += "\\\"";
            else if (c == '\n') result += "\\n";
            else result += c;
        }

        return result;
    }

}
