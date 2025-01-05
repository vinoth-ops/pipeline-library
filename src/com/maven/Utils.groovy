package com.maven

class Utils {
  public static void getcurrentdir() {
    echo "message from utils.getcurrentdir: entering method execution"
    println "message from utils.getcurrentdir: entering method execution"
    println "Current working directory: ${System.getProperty('user.dir')}"
  }
}
