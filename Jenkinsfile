pipeline {
    agent any

    environment {
        REGISTRY        = credentials('docker-registry-url')
        DEPLOY_HOST     = credentials('mufti-saleh-deploy-host')
        DEPLOY_USER     = 'deploy'
        IMAGE_TAG       = "${env.GIT_COMMIT[0..7]}"
        API_IMAGE       = "${REGISTRY}/mufti-saleh-api:${IMAGE_TAG}"
        WEB_IMAGE       = "${REGISTRY}/mufti-saleh-web:${IMAGE_TAG}"
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            parallel {
                stage('Frontend') {
                    steps {
                        dir('web') {
                            sh 'npm ci'
                            sh 'npm run typecheck'
                            sh 'npm run build'
                        }
                    }
                }
                stage('Backend') {
                    steps {
                        dir('server') {
                            sh './gradlew test jacocoTestReport --no-daemon'
                        }
                    }
                    post {
                        always {
                            junit 'server/build/test-results/**/*.xml'
                        }
                    }
                }
            }
        }

        stage('Docker Build') {
            parallel {
                stage('API Image') {
                    steps {
                        dir('server') {
                            sh "DOCKER_BUILDKIT=1 docker build -t ${API_IMAGE} ."
                        }
                    }
                }
                stage('Web Image') {
                    steps {
                        dir('web') {
                            sh "DOCKER_BUILDKIT=1 docker build -t ${WEB_IMAGE} ."
                        }
                    }
                }
            }
        }

        stage('Smoke Test Images') {
            steps {
                sh """
                    docker run --rm -d --name ms-api-smoke -p 18081:8080 \
                        -e APP_JWT_SECRET=smoke-test-secret-at-least-32-chars-long \
                        -e SPRING_DATASOURCE_URL=jdbc:h2:mem:smoketest \
                        ${API_IMAGE} || true
                    sleep 10
                    docker stop ms-api-smoke || true
                """
                sh """
                    docker run --rm --name ms-web-smoke -p 13000:80 -d ${WEB_IMAGE}
                    sleep 3
                    docker exec ms-web-smoke wget -qO- http://localhost:80/ | grep -q 'mufti-saleh'
                    docker stop ms-web-smoke
                """
            }
        }

        stage('Push Images') {
            when { branch 'main' }
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-registry-creds',
                        usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh 'echo $DOCKER_PASS | docker login $REGISTRY -u $DOCKER_USER --password-stdin'
                }
                sh "docker push ${API_IMAGE}"
                sh "docker push ${WEB_IMAGE}"
                sh "docker tag ${API_IMAGE} ${REGISTRY}/mufti-saleh-api:latest"
                sh "docker tag ${WEB_IMAGE} ${REGISTRY}/mufti-saleh-web:latest"
                sh "docker push ${REGISTRY}/mufti-saleh-api:latest"
                sh "docker push ${REGISTRY}/mufti-saleh-web:latest"
            }
        }

        stage('Deploy') {
            when { branch 'main' }
            steps {
                sshagent(['mufti-saleh-deploy-key']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_HOST} '
                            cd /opt/mufti-saleh &&
                            export IMAGE_TAG=${IMAGE_TAG} &&
                            docker compose -f docker-compose.prod.yml pull &&
                            docker compose -f docker-compose.prod.yml up -d --remove-orphans
                        '
                    """
                }
            }
        }

        stage('Health Check') {
            when { branch 'main' }
            steps {
                sshagent(['mufti-saleh-deploy-key']) {
                    sh """
                        for i in \$(seq 1 12); do
                            STATUS=\$(ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_HOST} \
                                'curl -sf http://localhost:8081/actuator/health | python3 -c "import sys,json; print(json.load(sys.stdin)[\"status\"])" 2>/dev/null || echo DOWN')
                            echo "Attempt \$i: \$STATUS"
                            [ "\$STATUS" = "UP" ] && exit 0
                            sleep 10
                        done
                        echo "Health check failed" && exit 1
                    """
                }
            }
        }
    }

    post {
        always {
            sh "docker rmi ${API_IMAGE} ${WEB_IMAGE} || true"
        }
        failure {
            echo "Build failed for branch ${env.BRANCH_NAME} — commit ${env.GIT_COMMIT}"
        }
    }
}
