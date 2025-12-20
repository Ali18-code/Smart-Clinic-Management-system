#include "json_util.h"
#include <fstream>
#include <sstream>

namespace sj {
    std::string readAllText(const std::string& path) {
        std::ifstream in(path.c_str(), std::ios::in);
        if (!in) return "";
        std::ostringstream ss; ss << in.rdbuf();
        return ss.str();
    }

    bool writeAllText(const std::string& path, const std::string& text) {
        std::ofstream out(path.c_str(), std::ios::out | std::ios::trunc);
        if (!out) return false;
        out << text;
        return true;
    }

    static int findKey(const std::string& j, const std::string& key) {
        std::string pat = "\"" + key + "\"";
        return (int)j.find(pat);
    }

    std::string getString(const std::string& json, const std::string& key) {
        int k = findKey(json, key);
        if (k < 0) return "";
        int colon = (int)json.find(':', k);
        if (colon < 0) return "";
        int firstQuote = (int)json.find('"', colon + 1);
        if (firstQuote < 0) return "";
        int secondQuote = (int)json.find('"', firstQuote + 1);
        if (secondQuote < 0) return "";
        return json.substr(firstQuote + 1, secondQuote - firstQuote - 1);
    }

    int getInt(const std::string& json, const std::string& key, int def) {
        int k = findKey(json, key);
        if (k < 0) return def;
        int colon = (int)json.find(':', k);
        if (colon < 0) return def;
        int i = colon + 1;
        while (i < (int)json.size() && (json[i] == ' ' || json[i] == '\n' || json[i] == '\r' || json[i] == '\t')) i++;
        bool neg = false;
        if (i < (int)json.size() && json[i] == '-') { neg = true; i++; }
        long long val = 0;
        bool any = false;
        while (i < (int)json.size() && json[i] >= '0' && json[i] <= '9') {
            any = true;
            val = val * 10 + (json[i] - '0');
            i++;
        }
        if (!any) return def;
        if (neg) val = -val;
        return (int)val;
    }

    bool getBool(const std::string& json, const std::string& key, bool def) {
        int k = findKey(json, key);
        if (k < 0) return def;
        int colon = (int)json.find(':', k);
        if (colon < 0) return def;
        int i = colon + 1;
        while (i < (int)json.size() && (json[i] == ' ' || json[i] == '\n' || json[i] == '\r' || json[i] == '\t')) i++;
        if (json.compare(i, 4, "true") == 0) return true;
        if (json.compare(i, 5, "false") == 0) return false;
        return def;
    }

    std::string esc(const std::string& s) {
        std::string r;
        for (int i = 0; i < (int)s.size(); i++) {
            char c = s[i];
            if (c == '\\') r += "\\\\";
            else if (c == '"') r += "\\\"";
            else if (c == '\n') r += "\\n";
            else r += c;
        }
        return r;
    }
}
