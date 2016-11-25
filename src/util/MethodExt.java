package util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class MethodExt {
   public static boolean isAnnotationPresent(Method method, Class<? extends Annotation> annotationClass) {
      for (Method superMethod = method; superMethod != null; superMethod = getSuperMethod(superMethod))
         if (superMethod.isAnnotationPresent(annotationClass))
            return true;
      return false;
   }

   public static Method getSuperMethod(Method method) {
      for (Class<?> superClass = method.getDeclaringClass().getSuperclass(); superClass != Object.class && superClass != null; superClass = superClass
            .getSuperclass()) {
         try {
            return superClass.getMethod(method.getName(), method.getParameterTypes());
         } catch (NoSuchMethodException e) {
         } catch (SecurityException e) {
         }
         // method not in this particular class, the next higher super class will be tried next
      }
      return null;
   }
}
