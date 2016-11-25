package util;

public class FunctionalIdentities {
   public static <T> IPredicate1<T> predicate1Accepter() {
      return new IPredicate1<T>() {
         @Override
         public boolean accepts(T input) {
            return true;
         }
      };
   }
}
