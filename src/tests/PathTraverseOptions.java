package tests;

import java.nio.file.Path;

public class PathTraverseOptions {
   private final boolean           recurse;
   private final IPredicate1<Path> directoryPathFilter;
   private final IPredicate1<Path> filePathFilter;

   public PathTraverseOptions() {
      this(true, createAccepter(), createAccepter());
   }

   private static <T> IPredicate1<T> createAccepter() {
      return new IPredicate1<T>() {
         @Override
         public boolean accepts(T t) {
            return true;
         }
      };
   }

   private PathTraverseOptions(boolean recurse, IPredicate1<Path> directoryPathFilter, IPredicate1<Path> filePathFilter) {
      this.recurse = recurse;
      this.directoryPathFilter = directoryPathFilter;
      this.filePathFilter = filePathFilter;
   }

   public boolean isRecurse() {
      return recurse;
   }

   public IPredicate1<Path> getDirectoryPathFilter() {
      return directoryPathFilter;
   }

   public IPredicate1<Path> getFilePathFilter() {
      return filePathFilter;
   }

   public PathTraverseOptions withRecurse(boolean recurse) {
      return new PathTraverseOptions(recurse, directoryPathFilter, filePathFilter);
   }

   public PathTraverseOptions withDirectoryPathFilter(IPredicate1<Path> directoryPathFilter) {
      return new PathTraverseOptions(recurse, directoryPathFilter, filePathFilter);
   }

   public PathTraverseOptions withFilePathFilter(IPredicate1<Path> filePathFilter) {
      return new PathTraverseOptions(recurse, directoryPathFilter, filePathFilter);
   }
}
