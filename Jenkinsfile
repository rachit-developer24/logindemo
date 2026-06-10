pipeline {
    agent any

    stages {
        stage('Build, Test & Coverage') {
            steps {
                sh 'chmod +x ./mvnw'
                sh './mvnw clean verify'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t logindemo .'
            }
        }
    }

    post {
        success {
            echo 'SUCCESS - build, tests, coverage, and Docker image all done!'
        }
        failure {
            echo 'FAILED - check the logs above.'
        }
    }
}}