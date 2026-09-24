pipeline {
    agent any

    environment {
        // Identifiants Docker Hub
        DOCKERHUB_USER = 'nacefmoula123'
        BACKEND_IMAGE  = "${DOCKERHUB_USER}/devops-backend"
        FRONTEND_IMAGE = "${DOCKERHUB_USER}/devops-frontend"
        BUILD_TAG      = "${env.BUILD_NUMBER}"
    }

    stages {
        stage('1. Checkout SCM') {
            steps {
                // Récupère le code depuis le dépôt Git configuré dans le Job
                checkout scm
            }
        }

        stage('2. Build & Test Backend') {
            steps {
                dir('backend') {
                    // Compile le backend et lance les tests unitaires
                    sh 'mvn clean test package -DskipTests'
                }
            }
        }

       stage('3. Analyse SonarQube') {
            steps {
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    dir('backend') {
                        sh '''
                            mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
                              -Dsonar.host.url=http://localhost:9000 \
                              -Dsonar.token=${SONAR_TOKEN} \
                              -Dsonar.projectKey=DevOps-AppGestionDesProjets-Backend \
                              -Dsonar.projectName="DevOps App Gestion Projets Backend"
                        '''
                    }
                }
            }
        }
        
        stage('4. Docker Build & Push') {
            steps {
                script {
                    // Connexion sécurisée à Docker Hub via les credentials Jenkins
                    withCredentials([usernamePassword(
                        credentialsId: 'dockerhub-creds', 
                        usernameVariable: 'DOCKER_USER', 
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        sh 'echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin'

                        // Build et Push de l'image Backend
                        echo "=== Construction et Push de l'image Backend ==="
                        sh "docker build -t ${BACKEND_IMAGE}:${BUILD_TAG} -t ${BACKEND_IMAGE}:latest ./backend"
                        sh "docker push ${BACKEND_IMAGE}:${BUILD_TAG}"
                        sh "docker push ${BACKEND_IMAGE}:latest"

                        // Build et Push de l'image Frontend (Node 22 et Nginx gérés dans le Dockerfile multi-stage)
                        echo "=== Construction et Push de l'image Frontend ==="
                        sh "docker build -t ${FRONTEND_IMAGE}:${BUILD_TAG} -t ${FRONTEND_IMAGE}:latest ./frontend"
                        sh "docker push ${FRONTEND_IMAGE}:${BUILD_TAG}"
                        sh "docker push ${FRONTEND_IMAGE}:latest"
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
    steps {
        sh 'kubectl rollout restart deployment/backend'
        sh 'kubectl rollout restart deployment/frontend'
    }
    }

    post {
        always {
            // Déconnexion de Docker Hub à la fin du pipeline
            sh 'docker logout'
        }
        success {
            echo "Pipeline exécuté avec succès ! Images Docker poussées sur Docker Hub."
        }
        failure {
            echo "Échec du pipeline. Consultez la console output pour analyser l'erreur."
        }
    }
}