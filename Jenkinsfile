pipeline {
    agent any

    tools {
        jdk 'JDK21'
        maven 'Maven'
    }

    options {
        skipDefaultCheckout(true)
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
    }

    triggers {
        // GitHub webhooks are preferred; Poll SCM is the built-in fallback.
        pollSCM('H/5 * * * *')
    }

    environment {
        DOCKERHUB_USERNAME = 'ibrahimmi124'
        IMAGE_NAME = "${DOCKERHUB_USERNAME}/calculator-service"
        PROXY_PORT = '8080'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/IbrahimMI124/RESTCalculator.git'

                sh 'git rev-parse --short HEAD'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn -B test'
            }

            post {
                always {
                    junit(
                        testResults: 'target/surefire-reports/*.xml',
                        allowEmptyResults: false
                    )
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh '''
                    set -eu

                    docker build \
                      --pull \
                      -t "${IMAGE_NAME}:${BUILD_NUMBER}" \
                      -t "${IMAGE_NAME}:latest" .
                '''
            }
        }

        stage('Push') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'docker-hub-credentials',
                        usernameVariable: 'DH_USER',
                        passwordVariable: 'DH_TOKEN'
                    )
                ]) {
                    sh '''
                        set -eu
                        set +x

                        echo "$DH_TOKEN" | docker login \
                          --username "$DH_USER" \
                          --password-stdin

                        docker push "${IMAGE_NAME}:${BUILD_NUMBER}"
                        docker push "${IMAGE_NAME}:latest"

                        docker logout
                    '''
                }
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    set -eu

                    export IMAGE_TAG="${BUILD_NUMBER}"

                    docker compose config --quiet

                    # Pull first so a registry failure does not take down the
                    # currently running stack.
                    docker compose pull calculator

                    # Remove stale containers and the old project network so a
                    # previous failed deploy cannot leave services stranded
                    # without network connectivity.
                    docker compose down --remove-orphans

                    docker compose up \
                      -d \
                      --no-build \
                      --wait \
                      --wait-timeout 120

                    docker compose ps
                '''

                sh '''
                    set -eu

                    for i in $(seq 1 30); do
                      if curl -fsS \
                        "http://localhost:${PROXY_PORT}/actuator/health"; then
                        echo "Application health check passed."
                        exit 0
                      fi

                      sleep 2
                    done

                    echo "Health check failed."
                    docker compose ps
                    docker compose logs --tail=100
                    exit 1
                '''
            }
        }
    }

    post {
        success {
            echo 'Calculator CI/CD pipeline succeeded.'

            emailext(
                to: 'mohdibrahim124.mi@gmail.com',
                subject: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """Calculator CI/CD pipeline succeeded.

Job: ${env.JOB_NAME}
Build: #${env.BUILD_NUMBER}
Commit: ${env.GIT_COMMIT ?: 'See checkout log'}
Build URL: ${env.BUILD_URL}
"""
            )
        }

        failure {
            echo 'Calculator CI/CD pipeline failed.'

            emailext(
                to: 'mohdibrahim124.mi@gmail.com',
                subject: "FAILURE: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """Calculator CI/CD pipeline failed.

Job: ${env.JOB_NAME}
Build: #${env.BUILD_NUMBER}
Build URL: ${env.BUILD_URL}

Inspect the Jenkins console output for details.
"""
            )
        }

        always {
            echo 'Pipeline execution completed.'
        }
    }
}
