// =============================================================================
//  Mufti Saleh — build & deploy pipeline
// =============================================================================
//
//  Builds server/ and web/, pushes Docker images (optional), deploys to the
//  production server via SSH on `main`.
//
// -----------------------------------------------------------------------------
//  A. What the Jenkins machine needs
// -----------------------------------------------------------------------------
//   - Plugins: Pipeline, Git, Docker Pipeline, SSH Agent.
//   - Docker available to the Jenkins user:  sudo usermod -aG docker jenkins
//   - SSH key on the deploy server:
//       ssh-keygen -t ed25519 -C "jenkins@mufti-saleh"
//       ssh-copy-id -i ~/.ssh/id_ed25519.pub deploy@<server>
//     Then add the private key in Jenkins:
//       Manage Jenkins → Credentials → Global → Add → SSH Username with private key
//       ID: mufti-saleh-deploy-key   Username: deploy
//
// -----------------------------------------------------------------------------
//  B. Required Jenkins credential
// -----------------------------------------------------------------------------
//   Secret file · ID: MUFTISALEH_ENV_FILE
//     Upload your filled muftisaleh.env as a secret file. It is SCP'd to the
//     server as /opt/mufti-saleh/.env before every compose up.
//
//   Secret text · ID: mufti-saleh-deploy-host
//     The IP or hostname of the production server.
//
// -----------------------------------------------------------------------------
//  C. Optional: Docker registry
// -----------------------------------------------------------------------------
//   Leave DOCKER_REGISTRY empty to build images on the agent and rsync them to
//   the server with `docker save | ssh docker load`. Set it to push instead:
//       ghcr.io/your-org   or   docker.io/your-dockerhub
//   Then add:
//       Kind: Username with password
//       ID:   docker-registry
//   and the pipeline will push and pull normally.
//
// -----------------------------------------------------------------------------
//  D. Create the job
// -----------------------------------------------------------------------------
//   Jenkins → New Item → Multibranch Pipeline
//     Branch Sources → Git → https://github.com/your-org/mufti-saleh.git
//     Build Configuration → Script Path: Jenkinsfile
//     Scan Triggers → Periodically: 5 minutes  (or set up a webhook)
// =============================================================================

pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 45, unit: 'MINUTES')
        disableConcurrentBuilds()
        timestamps()
    }

    environment {
        DEPLOY_HOST    = credentials('mufti-saleh-deploy-host')
        DEPLOY_USER    = 'deploy'
        DEPLOY_DIR     = '/opt/mufti-saleh'
        // Set to e.g. 'ghcr.io/your-org' to push images; leave empty to scp them.
        DOCKER_REGISTRY = ''
        IMAGE_API      = 'mufti-saleh/api'
        IMAGE_WEB      = 'mufti-saleh/web'
    }

    stages {

        stage('Checkout') {
            steps {
                script {
                    checkout scm
                    env.TARGET_BRANCH  = env.BRANCH_NAME?.trim() ?: 'main'
                    env.IMAGE_TAG      = sh(script: 'git rev-parse --short=8 HEAD', returnStdout: true).trim()
                    env.IMAGE_BRANCH_TAG = env.TARGET_BRANCH.replaceAll('[^A-Za-z0-9_.-]', '-')
                    echo "Branch: ${env.TARGET_BRANCH}  Tag: ${env.IMAGE_TAG}"
                }
                sh 'git --no-pager log -1 --pretty="%h %an %s"'
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
                            sh './gradlew test --no-daemon'
                        }
                    }
                    post {
                        always {
                            junit allowEmptyResults: true, testResults: 'server/build/test-results/**/*.xml'
                        }
                    }
                }
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    withEnv(['DOCKER_BUILDKIT=1']) {
                        sh """
                            docker build -t ${IMAGE_API}:${env.IMAGE_TAG} \
                                         -t ${IMAGE_API}:${env.IMAGE_BRANCH_TAG} \
                                         server
                        """
                        sh """
                            docker build -t ${IMAGE_WEB}:${env.IMAGE_TAG} \
                                         -t ${IMAGE_WEB}:${env.IMAGE_BRANCH_TAG} \
                                         web
                        """
                    }
                }
            }
        }

        stage('Smoke Test') {
            steps {
                sh """
                    set -e
                    cid=\$(docker run -d ${IMAGE_WEB}:${env.IMAGE_TAG})
                    echo "\$cid" > .web-cid
                    for i in \$(seq 1 15); do
                        docker exec "\$cid" wget -qO /dev/null http://127.0.0.1/ && break
                        sleep 1
                    done
                    for path in / /admin; do
                        docker exec "\$cid" wget -qO /dev/null "http://127.0.0.1\$path"
                        echo "\$path -> ok"
                    done
                """
            }
            post {
                failure {
                    sh '[ -f .web-cid ] && docker logs "\$(cat .web-cid)" 2>&1 | tail -40 || true'
                }
                cleanup {
                    sh '[ -f .web-cid ] && docker rm -f "\$(cat .web-cid)" >/dev/null 2>&1 || true; rm -f .web-cid'
                }
            }
        }

        stage('Push Images') {
            when {
                allOf {
                    branch 'main'
                    expression { return env.DOCKER_REGISTRY?.trim() }
                }
            }
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker-registry',
                        usernameVariable: 'REGISTRY_USER', passwordVariable: 'REGISTRY_TOKEN')]) {
                    sh 'echo "$REGISTRY_TOKEN" | docker login "${DOCKER_REGISTRY%%/*}" -u "$REGISTRY_USER" --password-stdin'
                }
                script {
                    for (img in [env.IMAGE_API, env.IMAGE_WEB]) {
                        for (tag in [env.IMAGE_TAG, env.IMAGE_BRANCH_TAG]) {
                            sh "docker tag  ${img}:${tag} ${env.DOCKER_REGISTRY}/${img}:${tag}"
                            sh "docker push ${env.DOCKER_REGISTRY}/${img}:${tag}"
                        }
                    }
                }
            }
        }

        stage('Deploy') {
            when { branch 'main' }
            steps {
                withCredentials([file(credentialsId: 'MUFTISALEH_ENV_FILE', variable: 'ENV_FILE')]) {
                    sshagent(['mufti-saleh-deploy-key']) {
                        script {
                            // Upload .env
                            sh "scp -o StrictHostKeyChecking=no \$ENV_FILE ${DEPLOY_USER}@${DEPLOY_HOST}:${DEPLOY_DIR}/.env"

                            if (env.DOCKER_REGISTRY?.trim()) {
                                // Registry mode: pull on server
                                sh """
                                    ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_HOST} '
                                        cd ${DEPLOY_DIR} &&
                                        export IMAGE_TAG=${env.IMAGE_TAG} &&
                                        export REGISTRY=${env.DOCKER_REGISTRY} &&
                                        docker compose -f docker-compose.prod.yml pull &&
                                        docker compose -f docker-compose.prod.yml up -d --remove-orphans
                                    '
                                """
                            } else {
                                // No registry: stream images directly to the server
                                sh """
                                    docker save ${IMAGE_API}:${env.IMAGE_TAG} ${IMAGE_WEB}:${env.IMAGE_TAG} | \
                                    ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_HOST} 'docker load'
                                """
                                sh """
                                    ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_HOST} '
                                        cd ${DEPLOY_DIR} &&
                                        export IMAGE_TAG=${env.IMAGE_TAG} &&
                                        export REGISTRY=mufti-saleh &&
                                        docker compose -f docker-compose.prod.yml up -d --remove-orphans
                                    '
                                """
                            }
                        }
                    }
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
                                'curl -sf http://localhost:8082/actuator/health | python3 -c "import sys,json; print(json.load(sys.stdin)[\"status\"])" 2>/dev/null || echo DOWN')
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
            script {
                if (env.IMAGE_TAG) {
                    sh """
                        docker image rm -f ${IMAGE_API}:${env.IMAGE_TAG} \
                                         ${IMAGE_API}:${env.IMAGE_BRANCH_TAG} \
                                         ${IMAGE_WEB}:${env.IMAGE_TAG} \
                                         ${IMAGE_WEB}:${env.IMAGE_BRANCH_TAG} >/dev/null 2>&1 || true
                    """
                }
            }
        }
        success {
            echo "OK  ${env.TARGET_BRANCH ?: 'build'} — ${env.IMAGE_TAG}"
        }
        failure {
            echo "FAILED  ${env.TARGET_BRANCH ?: 'build'} — see stage log above."
        }
    }
}
