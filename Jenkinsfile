// =============================================================================
//  Mufti Saleh — docker compose deployment pipeline
//
//  Builds and deploys the full stack (API, Web) on the server itself.
//  Jenkins must be running on the same machine as the deployment target.
//
// -----------------------------------------------------------------------------
//  A. Server / Jenkins prerequisites
// -----------------------------------------------------------------------------
//   1. Docker + Compose v2 installed; jenkins user in the docker group:
//        sudo usermod -aG docker jenkins && sudo systemctl restart jenkins
//   2. Jenkins plugins: Pipeline, Git, Credentials Binding
//
// -----------------------------------------------------------------------------
//  B. One credential to add in Jenkins
// -----------------------------------------------------------------------------
//   Manage Jenkins → Credentials → System → Global → Add credential
//     Kind:  Secret file
//     File:  your filled muftisaleh.env
//     ID:    MUFTISALEH_ENV_FILE        ← must match exactly
//
// -----------------------------------------------------------------------------
//  C. Create the Jenkins job
// -----------------------------------------------------------------------------
//   New Item → Pipeline → OK
//   Pipeline section:
//     Definition:   Pipeline script from SCM
//     SCM:          Git
//     URL:          https://github.com/your-org/mufti-saleh.git
//     Branch:       */main
//     Script Path:  Jenkinsfile
//   Triggers: Poll SCM or add a GitHub webhook.
//   Save → Build Now.
// =============================================================================

pipeline {
    agent any

    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '15'))
        timeout(time: 30, unit: 'MINUTES')
        timestamps()
    }

    environment {
        COMPOSE_FILE = 'docker-compose.prod.yml'
        APP_NAME     = 'mufti-saleh'
    }

    stages {

        // ------------------------------------------------------------------
        stage('Checkout') {
        // ------------------------------------------------------------------
            steps {
                checkout scm
            }
        }

        // ------------------------------------------------------------------
        stage('Prepare .env') {
        // ------------------------------------------------------------------
            steps {
                withCredentials([file(credentialsId: 'MUFTISALEH_ENV_FILE', variable: 'ENV_FILE')]) {
                    sh 'cp "$ENV_FILE" .env'
                }
                sh '''
                    set -e
                    for var in JWT_SECRET DB_URL DB_PASSWORD BOOTSTRAP_PASSWORD CORS_ORIGINS; do
                        grep -q "^${var}=" .env \
                            || { echo "ERROR: ${var} missing from .env"; exit 1; }
                        val=$(grep "^${var}=" .env | cut -d= -f2-)
                        [ -n "$val" ] \
                            || { echo "ERROR: ${var} is empty in .env"; exit 1; }
                    done

                    dupes=$(grep -oE "^[A-Za-z_][A-Za-z0-9_]*=" .env | sort | uniq -d || true)
                    [ -z "$dupes" ] \
                        || { echo "ERROR: duplicate keys in .env: $dupes"; exit 1; }

                    echo ".env validated."
                '''
            }
        }

        // ------------------------------------------------------------------
        stage('Build application images') {
        // ------------------------------------------------------------------
            steps {
                sh '''
                    DOCKER_BUILDKIT=1 \
                    docker compose -f ${COMPOSE_FILE} -p ${APP_NAME} \
                        build --pull api

                    DOCKER_BUILDKIT=1 \
                    docker compose -f ${COMPOSE_FILE} -p ${APP_NAME} \
                        build --pull --no-cache web
                '''
            }
        }

        // ------------------------------------------------------------------
        stage('Deploy') {
        // ------------------------------------------------------------------
            steps {
                sh '''
                    docker compose -f ${COMPOSE_FILE} -p ${APP_NAME} \
                        up -d --remove-orphans --no-build
                '''
            }
        }

        // ------------------------------------------------------------------
        stage('Container Status') {
        // ------------------------------------------------------------------
            steps {
                sh '''
                    echo "=== Running containers ==="
                    docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
                    echo "=== Stack containers ==="
                    docker ps -a --filter "name=${APP_NAME}" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
                    echo "=== Web container logs ==="
                    docker logs ${APP_NAME}-web-1 --tail=30 2>&1 || true
                '''
            }
        }

        // ------------------------------------------------------------------
        stage('Health Check') {
        // ------------------------------------------------------------------
            steps {
                script {
                    def apiPort = sh(script: "grep '^API_PORT=' .env | cut -d= -f2- || echo '8082'", returnStdout: true).trim() ?: '8082'
                    def healthy = false
                    for (int i = 1; i <= 18; i++) {
                        sleep(time: 5, unit: 'SECONDS')
                        def rc = sh(
                            script: "curl -fs http://127.0.0.1:${apiPort}/actuator/health | grep -q '\"status\":\"UP\"'",
                            returnStatus: true
                        )
                        if (rc == 0) { healthy = true; break }
                        echo "Waiting for API on port ${apiPort}… attempt ${i}/18"
                    }
                    if (!healthy) {
                        error('API did not become healthy within 90 s.')
                    }
                }
                script {
                    def webPort = sh(script: "grep '^WEB_PORT=' .env | cut -d= -f2- || echo '3000'", returnStdout: true).trim() ?: '3000'
                    def webUp = false
                    for (int i = 1; i <= 12; i++) {
                        sleep(time: 5, unit: 'SECONDS')
                        def rc = sh(
                            script: "curl -fso /dev/null http://127.0.0.1:${webPort}",
                            returnStatus: true
                        )
                        if (rc == 0) { webUp = true; break }
                        echo "Waiting for web on port ${webPort}… attempt ${i}/12"
                    }
                    if (!webUp) {
                        error("Web did not become reachable within 60 s.")
                    }
                }
                echo "All services healthy."
            }
        }

        // ------------------------------------------------------------------
        stage('Prune old images') {
        // ------------------------------------------------------------------
            steps {
                sh 'docker image prune -f'
            }
        }
    }

    post {
        success {
            echo "Deployment successful — stack '${APP_NAME}' is live."
            sh 'rm -f .env'
        }
        failure {
            echo "Deployment FAILED. Dumping recent container logs…"
            sh '''
                docker compose -f ${COMPOSE_FILE} -p ${APP_NAME} \
                    logs --tail=80 || true
            '''
            sh 'rm -f .env'
        }
        aborted {
            sh 'rm -f .env'
        }
    }
}
