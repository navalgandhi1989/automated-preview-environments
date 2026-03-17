pipeline {
    agent any

    parameters {
        string(name: 'BRANCH_NAME', defaultValue: 'main', description: 'Branch to build and deploy')
        booleanParam(name: 'CLEANUP', defaultValue: false, description: 'Remove ephemeral environment')
    }

    environment {
        BUILD_TAG = 'latest'
        UI_BUILD_NUMBER = 'latest'
        BACKEND_BUILD_NUMBER = 'latest'
		CLEANED_BRANCH_NAME = ''
		PARSED_CLEANUP_FLAG = params.CLEANUP.toString()
    }

    stages {
	
		stage('Parse Webhook Parameters') {
            steps {
                script {
					CLEANED_BRANCH_NAME = params.BRANCH_NAME.replaceAll('refs/heads/', '')
					echo "Deploying branch: ${CLEANED_BRANCH_NAME}"
					
					echo "Github event: ${env.x_github_event}"
					
					if (env.x_github_event == "delete") {
						PARSED_CLEANUP_FLAG = "true"
					} 
					
					echo "Cleanup: ${PARSED_CLEANUP_FLAG}"
					
					
					CLEANED_BRANCH_NAME = params.BRANCH_NAME.replaceAll('refs/heads/', '')
					echo "Deploying branch: ${CLEANED_BRANCH_NAME}"

					echo "Github event: ${env.x_github_event}"
					echo "Deleted flag: ${env.deleted}"

					// Skip push deletion events
					if (env.x_github_event == "push" && env.deleted == "true") {
						PARSED_CLEANUP_FLAG = "true"
					}

					if (env.x_github_event == "delete") {
						PARSED_CLEANUP_FLAG = "true"
					}

					echo "Cleanup: ${PARSED_CLEANUP_FLAG}"
                    
                }
            }
        }
	
	
        stage('Build Docker Images') {
			when {
				expression { PARSED_CLEANUP_FLAG != 'true' }
			}
            steps {
                script {
                    def sampleAppBuild = build job: 'sample-app-build',
                          propagate: false,
                          wait: true, 
                          parameters: [
                              string(name: 'BRANCH_NAME', value: CLEANED_BRANCH_NAME)
                          ]
                    
                    BUILD_TAG = sampleAppBuild.displayName
                    echo "BUILD_TAG is: ${BUILD_TAG}"

                    if (BUILD_TAG == 'latest') {
                        error "Failed to capture BUILD_TAG from the sample-app-build."
                    }
                    
                    if (sampleAppBuild.result != 'SUCCESS') {
                        error "sample-app-build failed with status: ${sampleAppBuild.result}"
                    }
                }
            }
        }

        stage('Deploy Ephemeral Environment Images') {
            steps {
                script {
                    // Use the captured BUILD_TAG for deployment
                    def UI_BUILD_NUMBER = BUILD_TAG
                    def BACKEND_BUILD_NUMBER = BUILD_TAG
					boolean CLEANUP_FLAG = (PARSED_CLEANUP_FLAG == 'true')

                    def sampleAppEphemeralDeployment = build job: 'sample-app-ephemeral-deployment',
                          propagate: false,
                          wait: true, 
                          parameters: [
							  string(name: 'BRANCH_NAME', value: CLEANED_BRANCH_NAME),
                              string(name: 'UI_BUILD_NUMBER', value: UI_BUILD_NUMBER),
                              string(name: 'BACKEND_BUILD_NUMBER', value: BACKEND_BUILD_NUMBER),
                              booleanParam(name: 'CLEANUP', value: CLEANUP_FLAG)
                          ]
                                        
                    if (sampleAppEphemeralDeployment.result != 'SUCCESS') {
                        error "sample-app-ephemeral-deployment failed with status: ${sampleAppEphemeralDeployment.result}"
                    }
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline execution completed.'
        }
        success {
            echo 'Pipelines executed successfully.'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}
