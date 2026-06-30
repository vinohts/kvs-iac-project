pipeline {

    agent any

    options {
        timestamps()
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
                wsl bash -c "cd /mnt/d/kvs-iac-project/packer && packer init ."
                '''
            }
        }

        stage('Packer Validate') {
            steps {
                bat '''
                wsl bash -c "cd /mnt/d/kvs-iac-project/packer && packer validate ."
                '''
            }
        }

        stage('Build Golden AMI') {
            steps {
                bat '''
                wsl bash -c "cd /mnt/d/kvs-iac-project/packer && packer build -color=false ."
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