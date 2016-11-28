package tests;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class AllTests {
   private final Comparator<Class<?>> classNameComparator = new Comparator<Class<?>>() {
      @Override
      public int compare(Class<?> a, Class<?> b) {
         if (a != null && b != null)
            return a.getSimpleName().compareTo(b.getSimpleName());
         if (a != null)
            return 1;
         if (b != null)
            return -1;
         return 0;
      }
   };

   @Test
   public void test() {
      Class<?> allTestsClass = getClass();
      Path allTestsBasePath = PathExt.getBasePath(allTestsClass);
      List<Path> files = new ArrayList<Path>();
      IPathVisitor pathVisitor = createPathVisitor(files);
      PathTraverseOptions options = createOptions(allTestsClass.getSimpleName());
      PathTraverser.traverse(allTestsBasePath, pathVisitor, options);
      List<Class<?>> testClasses = testPathsToTestClasses(allTestsBasePath, files);
      Collections.sort(testClasses, classNameComparator);
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
      Set<Class<?>> testClassSet = new HashSet<>();
      for (Path testPath : testPaths) {
         String className = toClassName(testPath.toString());
         String classFileName = className.substring(baseDirectoryNameLength, className.length() - classExtensionLength);
         try {
            Class<?> testClass = Class.forName(classFileName);
            if (isValidTestClass(testClass))
               testClassSet.add(testClass);
         } catch (ClassNotFoundException e) {
         }
      }
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
            && isAnnotationPresent(testMethod, Test.class));
   }

   private static boolean isAnnotationPresent(Method method, Class<? extends Annotation> annotationClass) {
      for (Method superMethod = method; superMethod != null; superMethod = getSuperMethod(superMethod))
         if (superMethod.isAnnotationPresent(annotationClass))
            return true;
      return false;
   }

   private static Method getSuperMethod(Method method) {
      for (Class<?> superClass = method.getDeclaringClass().getSuperclass(); superClass != Object.class
            && superClass != null; superClass = superClass.getSuperclass()) {
         try {
            return superClass.getMethod(method.getName(), method.getParameterTypes());
         } catch (NoSuchMethodException e) {
         } catch (SecurityException e) {
         }
         // method not in this particular class, the next higher super class will be tried next
      }
      return null;
   }

   private void runTestsAndShowResults(List<Class<?>> testClasses) {
      assertNonZeroTests(testClasses.size());
      SortedMap<Class<?>, List<Failure>> failures = runTestsAndGetFailures(testClasses);
      SortedSet<Class<?>> passes = new TreeSet<>(classNameComparator);
      passes.addAll(testClasses);
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
      SortedMap<Class<?>, List<Failure>> testFailures = new TreeMap<>(classNameComparator);
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
