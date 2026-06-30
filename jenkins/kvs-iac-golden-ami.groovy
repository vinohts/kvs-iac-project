pipeline {

    agent any

    options {
        timestamps()
    }

    environment {
        PROJECT_DIR = "/mnt/d/kvs-iac-project"
        PACKER_DIR  = "/mnt/d/kvs-iac-project/packer"
    }

    stages {

        stage('Checkout Source') {
            steps {
                checkout scm
            }
        }

        stage('Verify Environment') {
            steps {
                bat '''
                wsl hostname
                wsl whoami
                wsl aws sts get-caller-identity
                wsl packer version
                wsl ansible-playbook --version
                '''
            }
        }

        stage('Packer Init') {
            steps {
                bat '''
                wsl bash -c "cd ${PACKER_DIR} && packer init ."
                '''
            }
        }

        stage('Packer Validate') {
            steps {
                bat '''
                wsl bash -c "cd ${PACKER_DIR} && packer validate ."
                '''
            }
        }

        stage('Build Golden AMI') {
            steps {
                bat '''
                wsl bash -c "cd ${PACKER_DIR} && packer build -color=false ."
                '''
            }
        }

    }

    post {

        success {
            echo '====================================================='
            echo 'Golden AMI Build Completed Successfully'
            echo '====================================================='
        }

        failure {
            echo '====================================================='
            echo 'Golden AMI Build Failed'
            echo '====================================================='
        }

        always {
            cleanWs()
        }
    }

}