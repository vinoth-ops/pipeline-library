package com.maven

class Utils {
  public static void getcurrentdir() {
   try {
        println "message from Utils.getcurrentdir: entering method execution"
        println "Current working directory: ${System.getProperty('user.dir')}"
    } catch (Exception e) {
        println "Exception in getcurrentdir: ${e.message}"
    }
  }
}
