package tests;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class PathTraverser implements FileVisitor<Path> {
   private final IPathVisitor        pathVisitor;
   private final PathTraverseOptions options;

   public PathTraverser(IPathVisitor pathVisitor, PathTraverseOptions options) {
      this.pathVisitor = pathVisitor;
      this.options = options;
   }

   @Override
   public FileVisitResult postVisitDirectory(Path directoryPath, IOException exception) throws IOException {
      return FileVisitResult.CONTINUE;
   }

   @Override
   public FileVisitResult preVisitDirectory(Path directoryPath, BasicFileAttributes attributes) throws IOException {
      if (options.getDirectoryPathFilter().accepts(directoryPath)) {
         pathVisitor.visitDirectory(directoryPath);
         return options.isRecurse()
               ? FileVisitResult.CONTINUE
               : FileVisitResult.SKIP_SUBTREE;
      }
      return FileVisitResult.SKIP_SUBTREE;
   }

   @Override
   public FileVisitResult visitFile(Path filePath, BasicFileAttributes attributes) throws IOException {
      if (options.getFilePathFilter().accepts(filePath)) {
         pathVisitor.visitFile(filePath);
      }
      return FileVisitResult.CONTINUE;
   }

   @Override
   public FileVisitResult visitFileFailed(Path filePath, IOException exception) throws IOException {
      return FileVisitResult.CONTINUE;
   }

   public static void traverse(Path path, IPathVisitor pathVisitor) {
      traverse(path, pathVisitor, new PathTraverseOptions());
   }

   public static void traverse(Path path, IPathVisitor pathVisitor, PathTraverseOptions options) {
      PathTraverser traverser = new PathTraverser(pathVisitor, options);
      try {
         Files.walkFileTree(path, traverser);
      } catch (IOException e) {
         System.out.println("Error in traversing path: " + path.getFileName());
      }
   }
}
