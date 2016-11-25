package util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListExt {
   public static <TElement, TTransformed> Set<TTransformed> toSet(List<TElement> list, IFunction1<TTransformed, TElement> transformationFunction) {
      Set<TTransformed> set = new HashSet<>();
      for (TElement element : list) {
         TTransformed transformed = transformationFunction.function(element);
         set.add(transformed);
      }
      return set;
   }
}
