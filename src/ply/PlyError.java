package ply;

public class PlyError {
   public static final int ERROR_NOT_FOUND                     = 0;
   public static final int ERROR_NOT_PLY                       = 1;
   public static final int ERROR_FORMAT_NOT_FOUND              = 100;
   public static final int ERROR_FORMAT_NOT_RECOGNISED         = 101;
   public static final int ERROR_HEADER_NOT_FOUND              = 200;
   public static final int ERROR_HEADER_NOT_RECOGNISED         = 201;
   public static final int ERROR_HEADER_UNEXPECTED             = 202;
   public static final int ERROR_HEADER_ELEMENT_COUNT_NOT_READ = 210;
   public static final int ERROR_HEADER_ELEMENTS_MISSING       = 211;
   public static final int ERROR_DATA_NOT_FOUND                = 300;
   public static final int ERROR_DATA_INSUFFICIENT             = 301;
   public static final int ERROR_DATA_NOT_READ                 = 400;
   public static final int ERROR_DATA_NOT_WRITTEN              = 500;
}