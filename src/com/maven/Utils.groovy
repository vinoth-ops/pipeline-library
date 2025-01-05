package com.maven

class Utils {
    Script script;
    public static String getcurrentdir() {
        try {
            script.echo "message from Utils.getcurrentdir: entering method execution"
            return "Method successfully executed"
        } catch (Exception e) {
            println "Exception: ${e.message}"
            return null
        }
    }
}
