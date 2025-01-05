package com.maven

class Utils {
  public static void getcurrentdir() {
    
    println "message from utils.getcurrentdir: entering method execution"
    println "Current working directory: ${System.getProperty('user.dir')}"
  }
}
