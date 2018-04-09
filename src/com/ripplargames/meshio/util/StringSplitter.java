package com.ripplargames.meshio.util;

import java.util.ArrayList;
import java.util.List;

public class StringSplitter {
    public static List<String> splitChar(String line, char splitter) {
        List<String> parts = new ArrayList<String>();
        int beginIndex;
        int endIndex = 0;
        do {
            beginIndex = indexOf(line, endIndex, splitter, false);
            if (beginIndex != -1) {
                endIndex = indexOf(line, beginIndex, splitter, true);
                if (endIndex != -1) {
                    String part = line.substring(beginIndex, endIndex);
                    parts.add(part);
                }
            }
        } while ((beginIndex != -1) && (endIndex != -1));
        if (beginIndex != -1) {
            String part = line.substring(beginIndex);
            parts.add(part);
        }
        return parts;
    }

    public static int indexOf(String line, int fromIndex, char splitter, boolean isMatch) {
        for (int i = fromIndex; i < line.length(); i++) {
            char c = line.charAt(i);
            boolean isCharMatch = (c == splitter);
            if (isCharMatch == isMatch)
                return i;
        }
        return -1;
    }
}
