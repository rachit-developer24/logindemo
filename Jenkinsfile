pipeline {
    agent any

    stages {
        stage('Build, Test & Coverage') {
            steps {
                sh 'chmod +x ./mvnw'
                sh './mvnw clean verify'
            }
        }
    }

    post {
        success {
            echo 'SUCCESS - build, tests, and 80% coverage all passed!'
        }
        failure {
            echo 'FAILED - check the logs above.'
        }
    }
}