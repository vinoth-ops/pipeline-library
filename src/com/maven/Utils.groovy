package com.maven

class Utils {
  public static String getcurrentdir() {
   try {
        
        return "Method successfully executed"
    } catch (Exception e) {
        println "Exception: ${e.message}"
    }
  }
}
