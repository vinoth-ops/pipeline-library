package com.maven

class Utils {
  public static String getcurrentdir() {
    println "entering method execution"
    println "Current working directory: ${System.getProperty('user.dir')}"
  }
}
