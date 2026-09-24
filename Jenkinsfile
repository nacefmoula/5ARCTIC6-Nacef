pipeline {
    agent any

    environment {
        // Identifiants Docker Hub
        DOCKERHUB_USER = 'nacefmoula123'
        BACKEND_IMAGE  = "${DOCKERHUB_USER}/devops-backend"
        FRONTEND_IMAGE = "${DOCKERHUB_USER}/devops-frontend"
        BUILD_TAG      = "${env.BUILD_NUMBER}"
        
        // Permet à Jenkins d'utiliser la configuration Minikube de votre utilisateur
        KUBECONFIG     = '/var/lib/jenkins/.kube/config'
    }

    stages {
        stage('1. Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('2. Build & Test Backend') {
            steps {
                dir('backend') {
                    sh 'mvn clean package -DskipTests'
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
                    withCredentials([usernamePassword(
                        credentialsId: 'dockerhub-creds', 
                        usernameVariable: 'DOCKER_USER', 
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        sh 'echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin'

                        // Build et Push Backend
                        echo "=== Construction et Push de l'image Backend ==="
                        sh "docker build -t ${BACKEND_IMAGE}:${BUILD_TAG} -t ${BACKEND_IMAGE}:latest ./backend"
                        sh "docker push ${BACKEND_IMAGE}:${BUILD_TAG}"
                        sh "docker push ${BACKEND_IMAGE}:latest"

                        // Build et Push Frontend
                        echo "=== Construction et Push de l'image Frontend ==="
                        sh "docker build -t ${FRONTEND_IMAGE}:${BUILD_TAG} -t ${FRONTEND_IMAGE}:latest ./frontend"
                        sh "docker push ${FRONTEND_IMAGE}:${BUILD_TAG}"
                        sh "docker push ${FRONTEND_IMAGE}:latest"
                    }
                }
            }
        }

        stage('5. Deploy to Kubernetes') {
            steps {
                echo "=== Déploiement et redémarrage des PODs Kubernetes ==="
                // Redémarre les pods pour télécharger la dernière image :latest
                sh 'kubectl rollout restart deployment/backend'
                sh 'kubectl rollout restart deployment/frontend'
                
                // Attend que les nouveaux pods soient prêts avant de valider le stage
                sh 'kubectl rollout status deployment/backend --timeout=60s'
                sh 'kubectl rollout status deployment/frontend --timeout=60s'
            }
        }
    } // <-- Fermeture obligatoire du bloc stages

    post {
        always {
            sh 'docker logout'
        }
        success {
            echo "Pipeline exécuté avec succès ! Application déployée sur Kubernetes."
        }
        failure {
            echo "Échec du pipeline. Consultez la console output pour analyser l'erreur."
        }
    }
}