package util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public abstract class AMultiCollection<K, V, C extends Collection<V>> {
   private final Map<K, C> backing;
   private int             countAll;

   public AMultiCollection() {
      this(new HashMap<K, C>());
   }

   public AMultiCollection(Map<K, C> backing) {
      this.backing = backing;
   }

   public void add(K k, Iterable<V> e) {
      C values = getValidValues(k);
      for (V v : e)
         addValueTo(values, v);
   }

   public void add(K k, V v) {
      C values = getValidValues(k);
      addValueTo(values, v);
   }

   public void addToAllKeys(Iterable<V> e) {
      for (V v : e)
         addToAllKeys(v);
   }

   public void addToAllKeys(V v) {
      for (C c : backing.values())
         addValueTo(c, v);
   }

   private void addValueTo(C values, V v) {
      if (values.add(v))
         ++countAll;
   }

   public void clear() {
      for (C collection : backing.values())
         collection.clear();
      countAll = 0;
   }

   public void ensureKey(K k) {
      if (!backing.containsKey(k))
         backing.put(k, createNewCollection());
   }

   public boolean containsKey(K k) {
      return backing.containsKey(k);
   }

   public boolean containsValue(K k, V v) {
      C values = getValues(k);
      return (values != null) && values.contains(v);
   }

   public void recalculateCountAll() {
      countAll = 0;
      for (C collection : backing.values())
         countAll += collection.size();
   }

   public int countAllValues() {
      return countAll;
   }

   public int countKeys() {
      return backing.size();
   }

   public int countValuesFor(K k) {
      C values = getValues(k);
      return (values != null)
            ? values.size()
            : 0;
   }

   public void removeValues(K k, Iterable<V> e) {
      C values = getValues(k);
      if (values != null)
         for (V v : e)
            removeValueFrom(values, v);
   }

   public void removeValue(K k, V v) {
      C values = getValues(k);
      if (values != null)
         removeValueFrom(values, v);
   }

   public void removeValuesFromAllKeys(Iterable<V> e) {
      for (V v : e)
         removeValueFromAllKeys(v);
   }

   public void removeValueFromAllKeys(V v) {
      for (C values : backing.values())
         removeValueFrom(values, v);
   }

   private void removeValueFrom(C values, V v) {
      if (values.remove(v))
         --countAll;
   }

   public void removeKeys(Iterable<K> e) {
      for (K k : e)
         removeKey(k);
   }

   public C removeKey(K k) {
      C values = getValues(k);
      if (values != null) {
         backing.remove(k);
         countAll -= values.size();
      }
      return values;
   }

   public Set<K> keys() {
      return backing.keySet();
   }

   public C values(K k) {
      return getValues(k);
   }

   public Set<Map.Entry<K, C>> entries() {
      return backing.entrySet();
   }

   private C getValues(K k) {
      return backing.get(k);
   }

   private C getValidValues(K k) {
      C values = getValues(k);
      if (values == null) {
         values = createNewCollection();
         backing.put(k, values);
      }
      return values;
   }

   protected abstract C createNewCollection();

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      for (Entry<K, C> entry : backing.entrySet()) {
         sb.append(entry.getKey());
         sb.append("[\n");
         for (V value : entry.getValue()) {
            sb.append("  ");
            sb.append(value);
            sb.append(",\n");
         }
         sb.append("]\n");
      }
      return sb.toString();
   }
}
