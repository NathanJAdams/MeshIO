package util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MultiSet<K, V> extends AMultiCollection<K, V, Set<V>> {
   public MultiSet() {
   }

   public MultiSet(Map<K, Set<V>> backing) {
      super(backing);
   }

   @Override
   protected Set<V> createNewCollection() {
      return new HashSet<>();
   }
}
