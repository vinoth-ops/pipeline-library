package com.maven

class Utils {
  public static String getcurrentdir() {
   try {
        echo "message from Utils.getcurrentdir: entering method execution"
        
        return "Method successfully executed"
    } catch (Exception e) {
        println "Exception: ${e.message}"
    }
  }
}
