package tests;

import java.nio.file.Path;

public interface IPathVisitor {
   void visitDirectory(Path directoryPath);

   void visitFile(Path filePath);
}
