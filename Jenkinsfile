pipeline {
    agent any

    stages {
        stage('Build, Test & Coverage') {
            steps {
                sh 'chmod +x ./mvnw'
                sh './mvnw clean verify'
            }
        }

        stage('SonarQube Scan') {
            steps {
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    sh './mvnw sonar:sonar -Dsonar.host.url=http://host.docker.internal:9000 -Dsonar.token=$SONAR_TOKEN -Dsonar.projectKey=logindemo'
                }
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
            echo 'SUCCESS - build, tests, coverage, scan, image, and push all done!'
        }
        failure {
            echo 'FAILED - check the logs above.'
        }
    }
}