package com.campusflow.util;

import java.util.List;

/** Tiny JSON writer (no library needed). Strings are escaped, numbers/booleans written as-is. */
public final class Json {
    private Json() {}

    /** Wrapper for text that is already valid JSON (nested object or array). */
    public static final class Raw {
        final String text;
        public Raw(String text) { this.text = text; }
    }

    public static Raw raw(String text) { return new Raw(text); }

    public static String value(Object v) {
        if (v == null) return "null";
        if (v instanceof Raw r) return r.text;
        if (v instanceof Number || v instanceof Boolean) return v.toString();
        return quote(v.toString());
    }

    public static String quote(String s) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> { if (c < 0x20) sb.append(String.format("\\u%04x", (int) c)); else sb.append(c); }
            }
        }
        return sb.append('"').toString();
    }

    /** obj("a", 1, "b", "x") -> {"a":1,"b":"x"} */
    public static String obj(Object... kv) {
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < kv.length; i += 2) {
            if (i > 0) sb.append(',');
            sb.append(quote((String) kv[i])).append(':').append(value(kv[i + 1]));
        }
        return sb.append('}').toString();
    }

    public static String arr(List<String> jsonItems) {
        return "[" + String.join(",", jsonItems) + "]";
    }
}
