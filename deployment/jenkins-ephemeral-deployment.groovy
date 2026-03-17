pipeline {
    agent any
    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Branch to deploy')
        string(name: 'BACKEND_BUILD_NUMBER', description: 'Build number for Backend')
        string(name: 'UI_BUILD_NUMBER', description: 'Build number for UI')
		booleanParam(name: 'CLEANUP', defaultValue: false, description: 'Cleanup mode: Remove stack for this branch')
    }
    environment {
        SSH_USER = "root"
        SSH_HOST = "10.1.21.51"
        SSH_KEY = "SSH_KEY_CT-12251"
        TARGET_DIR = "/root/sample-app-ephemeral"
    }
    stages {
        stage('Prepare Variables') {
            steps {
                script {
                    STACK_NAME = params.BRANCH_NAME.replaceAll("[^a-zA-Z0-9-]", "-").toLowerCase()
                    UI_PORT = sh(script: "shuf -i 18000-18999 -n 1", returnStdout: true).trim()
                    MARIADB_PORT = sh(script: "shuf -i 15400-15999 -n 1", returnStdout: true).trim()
                    echo "Stack: ${STACK_NAME}"
                    echo "UI Port: ${UI_PORT}"
                    echo "MariaDB Port: ${MARIADB_PORT}"
                }
            }
        }
     
		
		stage('Cleanup Previous Stack') {
            when {
                expression {
                    params.CLEANUP
                }
            }
            steps {
                script {
                    sshagent(credentials: [SSH_KEY]) {
                        sh """
                      [ -d ~/.ssh ] || mkdir ~/.ssh && chmod 0700 ~/.ssh
                      ssh-keyscan -t rsa,dsa ${SSH_HOST} >> ~/.ssh/known_hosts
					  
					  ssh -t -t  ${SSH_USER}@${SSH_HOST} 'bash -s << 'ENDSSH'
								docker compose -p ${STACK_NAME} down 
								cd ${TARGET_DIR}
								rm -rf ${STACK_NAME}
								exit
                       ENDSSH'
                      """
                    }
                }
            }
        }
		
		stage('Prepare Environment File') {
            when {
                expression {
                    !params.CLEANUP
                }
            }
            steps {
                script {
                    sshagent(credentials: [SSH_KEY]) {
                        sh """
                      [ -d ~/.ssh ] || mkdir ~/.ssh && chmod 0700 ~/.ssh
                      ssh-keyscan -t rsa,dsa ${SSH_HOST} >> ~/.ssh/known_hosts
					  
					  ssh -t -t  ${SSH_USER}@${SSH_HOST} 'bash -s << 'ENDSSH'
								
								cd ${TARGET_DIR}
								mkdir -p ${STACK_NAME}
								
								cp nginx.conf ${STACK_NAME}/nginx.conf
								cp .env.template ${STACK_NAME}/.env.local
								cp docker-compose.ephemeral.yml ${STACK_NAME}/docker-compose.yml
								
								exit
                       ENDSSH'
                      """
                    }
                }
            }
        }
		
		stage('Deploy Stack') {
            when {
                expression {
                    !params.CLEANUP
                }
            }
            steps {
                script {
                    sshagent(credentials: [SSH_KEY]) {
                        sh """
                      [ -d ~/.ssh ] || mkdir ~/.ssh && chmod 0700 ~/.ssh
                      ssh-keyscan -t rsa,dsa ${SSH_HOST} >> ~/.ssh/known_hosts
					  
					  ssh -t -t  ${SSH_USER}@${SSH_HOST} 'bash -s << 'ENDSSH'
			
						docker compose -p ${STACK_NAME} down 
						cd ${TARGET_DIR}
						cd ${STACK_NAME}
						STACK_DIR=${TARGET_DIR}/${STACK_NAME} BACKEND_BUILD_NUMBER=${params.BACKEND_BUILD_NUMBER} UI_BUILD_NUMBER=${params.UI_BUILD_NUMBER} UI_PORT=${UI_PORT} MARIADB_PORT=${MARIADB_PORT} docker compose -p ${STACK_NAME} up -d
						exit
            
						ENDSSH'
                      """
                    }
                }
            }
        }
		
    
        stage('Print Access Info') {
			when {
                expression {
                    !params.CLEANUP
                }
            }
            steps {
                script {
					echo ""
					echo ""
					echo ""
                    echo "Ephemeral Environment Deployed"
                    echo "UI: http://${SSH_HOST}:${UI_PORT}"
                    echo "MariaDB: ${SSH_HOST}:${MARIADB_PORT}"
                    echo "Stack: ${STACK_NAME}"
                    echo "Branch: ${params.BRANCH_NAME}"
                }
            }
        }
    }
    post {
        failure {
            echo "Pipeline failed. Check logs above."
        }
        always {
            echo "Pipeline execution completed."
        }
    }
}
