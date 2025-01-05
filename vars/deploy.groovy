// vars/deploy.groovy
import com.maven.MavenUtils
import com.maven.Utils

def call() {
    echo 'message from Deploy.groovy: Deploying the application...'

    echo "Utils class: ${Utils}"
    // Add any other deploy steps, such as running scripts or commands
    res = Utils.getcurrentdir()

    println "$res"

    echo "after calling Utils"
}
