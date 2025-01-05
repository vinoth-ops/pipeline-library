// vars/deploy.groovy
import com.maven.MavenUtils
import com.maven.Utils

script {
    System.setOut(new PrintStream(new OutputStream() {
        void write(int b) {
            echo new String([b] as byte[])
        }
    }))
}


def call() {
    echo 'message from Deploy.groovy: Deploying the application...'

    try {
        def res = Utils.getcurrentdir()
        echo "Result from Utils.getcurrentdir: ${res}"
    } catch (Exception e) {
        echo "Exception in deploy.groovy: ${e.message}"
        e.printStackTrace()
    }

    echo "after calling Utils"
}
