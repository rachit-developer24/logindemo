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

        stage('Push to Registry') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker tag logindemo $DOCKER_USER/logindemo:latest
                        docker push $DOCKER_USER/logindemo:latest
                    '''
                }
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
}