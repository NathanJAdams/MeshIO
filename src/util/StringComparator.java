package util;

import java.util.Comparator;

public class StringComparator implements Comparator<String> {
   private final boolean ignoreCase;

   public StringComparator(boolean ignoreCase) {
      this.ignoreCase = ignoreCase;
   }

   @Override
   public int compare(String a, String b) {
      if (a != null)
         return compareNotNull(a, b);
      if (b != null)
         return -compareNotNull(b, a);
      return 0;
   }

   public int compareNotNull(String a, String b) {
      return ignoreCase
            ? a.compareToIgnoreCase(b)
            : a.compareTo(b);
   }
}
