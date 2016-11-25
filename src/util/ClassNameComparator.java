package util;

import java.util.Comparator;

public class ClassNameComparator implements Comparator<Class<?>> {
   private static final Comparator<String> NAME_COMPARATOR = new StringComparator(false);

   @Override
   public int compare(Class<?> a, Class<?> b) {
      String aName = (a == null)
            ? null
            : a.getName();
      String bName = (b == null)
            ? null
            : b.getName();
      return NAME_COMPARATOR.compare(aName, bName);
   }
}
