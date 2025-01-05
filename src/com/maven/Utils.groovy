package com.maven

class Utils {
    public static String getcurrentdir(script) {
        try {
            script.echo "message from Utils.getcurrentdir: entering method execution"
            String cwd = script.pwd()
            script.echo "Current working directory: ${cwd}"
            return cwd
        } catch (Exception e) {
            println "Exception: ${e.message}"
            return null
        }
    }
}
