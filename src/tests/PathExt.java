package tests;

import java.io.File;
import java.nio.file.Path;

public class PathExt {
   public static Path getBasePath(Class<?> forClass) {
      String pathName = forClass.getProtectionDomain().getCodeSource().getLocation().getPath();
      File classFile = new File(pathName);
      return classFile.toPath();
   }
}
