#pragma once
#include <string>

namespace sj {
    std::string readAllText(const std::string& path);
    bool writeAllText(const std::string& path, const std::string& text);

    // very small JSON helpers (manual parse, simple keys only)
    std::string getString(const std::string& json, const std::string& key);
    int getInt(const std::string& json, const std::string& key, int def = 0);
    bool getBool(const std::string& json, const std::string& key, bool def = false);

    std::string esc(const std::string& s);
}
