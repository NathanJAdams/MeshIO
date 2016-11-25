package tests;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import util.ClassNameComparator;
import util.IFunction1;
import util.IPredicate1;
import util.ListExt;
import util.MethodExt;

public class AllTests {
   @Test
   public void test() {
      Class<?> allTestsClass = getClass();
      Path allTestsBasePath = PathExt.getBasePath(allTestsClass);
      List<Path> files = new ArrayList<Path>();
      IPathVisitor pathVisitor = createPathVisitor(files);
      PathTraverseOptions options = createOptions(allTestsClass.getSimpleName());
      PathTraverser.traverse(allTestsBasePath, pathVisitor, options);
      List<Class<?>> testClasses = testPathsToTestClasses(allTestsBasePath, files);
      Collections.sort(testClasses, new ClassNameComparator());
      runTestsAndShowResults(testClasses);
   }

   private IPathVisitor createPathVisitor(final List<Path> files) {
      return new IPathVisitor() {
         @Override
         public void visitDirectory(Path directoryPath) {
         }

         @Override
         public void visitFile(Path filePath) {
            files.add(filePath);
         }
      };
   }

   private PathTraverseOptions createOptions(String allTestsClassName) {
      return new PathTraverseOptions().withFilePathFilter(new IPredicate1<Path>() {
         @Override
         public boolean accepts(Path path) {
            return path.toString().endsWith(".class");
         }
      });
   }

   private List<Class<?>> testPathsToTestClasses(Path baseDirectoryPath, List<Path> testPaths) {
      final int baseDirectoryNameLength = baseDirectoryPath.toString().length() + 1;
      final int classExtensionLength = ".class".length();
      Set<Class<?>> testClassSet = ListExt.toSet(testPaths, new IFunction1<Class<?>, Path>() {
         @Override
         public Class<?> function(Path path) {
            String className = toClassName(path.toString());
            String classFileName = className.substring(baseDirectoryNameLength, className.length() - classExtensionLength);
            try {
               Class<?> testClass = Class.forName(classFileName);
               if (isValidTestClass(testClass))
                  return testClass;
            } catch (ClassNotFoundException e) {
            }
            return null;
         }
      });
      testClassSet.remove(null);
      return new ArrayList<Class<?>>(testClassSet);
   }

   private String toClassName(String fileName) {
      return fileName.toString().replace('\\', '.').replace('/', '.');
   }

   private boolean isValidTestClass(Class<?> testClass) {
      if (testClass == null)
         return false;
      if (getClass().isAssignableFrom(testClass))
         return false;
      if (Modifier.isAbstract(testClass.getModifiers()))
         return false;
      if (testClass.isAnonymousClass())
         return false;
      if (testClass.isAnnotation())
         return false;
      if (testClass.isAnnotationPresent(Ignore.class))
         return false;
      if (testClass.isArray())
         return false;
      if (testClass.isEnum())
         return false;
      if (testClass.isInterface())
         return false;
      if (testClass.isPrimitive())
         return false;
      Method[] methods = testClass.getDeclaredMethods();
      for (Method method : methods)
         if (isValidTestMethod(method))
            return true;
      return false;
   }

   private boolean isValidTestMethod(Method testMethod) {
      return (!Modifier.isStatic(testMethod.getModifiers()) && (testMethod.getParameterTypes().length == 0)
            && MethodExt.isAnnotationPresent(testMethod, Test.class));
   }

   private void runTestsAndShowResults(List<Class<?>> testClasses) {
      assertNonZeroTests(testClasses.size());
      SortedMap<Class<?>, List<Failure>> failures = runTestsAndGetFailures(testClasses);
      Set<Class<?>> passes = new HashSet<>(testClasses);
      passes.removeAll(failures.keySet());
      printPassFailNames(passes, failures.keySet());
      printFailureDetails(failures);
      assertAllPassed(testClasses.size(), failures.size());
   }

   private void assertNonZeroTests(int numTests) {
      if (numTests == 0) {
         Assert.fail("No tests found");
      }
   }

   private SortedMap<Class<?>, List<Failure>> runTestsAndGetFailures(List<Class<?>> testClasses) {
      SortedMap<Class<?>, List<Failure>> testFailures = new TreeMap<>(new ClassNameComparator());
      JUnitCore junit = new JUnitCore();
      for (Class<?> testClass : testClasses) {
         List<Failure> failures = runTestAndGetFailures(junit, testClass);
         if (failures != null) {
            testFailures.put(testClass, failures);
         }
      }
      return testFailures;
   }

   private List<Failure> runTestAndGetFailures(JUnitCore junit, Class<?> testClass) {
      System.out.println("Running test: " + testClass.getSimpleName());
      try {
         Result result = junit.run(testClass);
         if (!result.wasSuccessful())
            return result.getFailures();
      } catch (Exception e) {
         // exceptions only caught to allow test runner to continue
      }
      return null;
   }

   private void printPassFailNames(Iterable<Class<?>> passedClasses, Iterable<Class<?>> failedClasses) {
      System.out.println();
      System.out.println("=========Passes=========");
      for (Class<?> testPass : passedClasses) {
         System.out.println("Passed: " + testPass.getSimpleName());
      }
      System.out.println();
      System.out.println("=========Fails==========");
      for (Class<?> testPass : failedClasses) {
         System.out.println("Failed: " + testPass.getSimpleName());
      }
   }

   private void printFailureDetails(Map<Class<?>, List<Failure>> testFailures) {
      System.out.println();
      for (Entry<Class<?>, List<Failure>> testFailure : testFailures.entrySet()) {
         System.out.println("========================");
         System.out.println(testFailure.getKey().getSimpleName());
         for (Failure failure : testFailure.getValue()) {
            System.out.println(failure.getTrace());
         }
      }
   }

   private void assertAllPassed(int numTotal, int numFailed) {
      if (numFailed == 0) {
         System.out.println("All tests passed.");
      } else {
         int numPassed = numTotal - numFailed;
         Assert.fail(numPassed + " tests passed. " + numFailed + " tests failed. " + numTotal + " tests performed.");
      }
   }
}
