pipeline {

    agent any

    parameters {

        gitParameter(
            name: 'BRANCH',
            type: 'PT_BRANCH',
            defaultValue: 'main',
            branchFilter: 'origin/(.*)',
            selectedValue: 'DEFAULT',
            sortMode: 'ASCENDING_SMART',
            description: 'Select Git Branch to Build'
        )

    }

    options {
        timestamps()
    }

    stages {

        /**********************************************************************
         * Checkout Source Code
         **********************************************************************/
        stage('Checkout Source') {
            steps {

                git(
                    branch: params.BRANCH,
                    credentialsId: 'github',
                    url: 'https://github.com/vinohts/kvs-iac-project.git'
                )

            }
        }

        /**********************************************************************
         * Verify Build Environment
         **********************************************************************/
        stage('Verify Environment') {
            steps {
                bat '''
                echo =====================================================
                echo Verifying Build Environment
                echo =====================================================

                echo Selected Branch : %BRANCH%

                wsl hostname
                wsl whoami
                wsl aws sts get-caller-identity
                wsl packer version
                wsl ansible-playbook --version
                '''
            }
        }

        /**********************************************************************
         * Initialize Packer Plugins
         **********************************************************************/
        stage('Packer Init') {
            steps {
                bat '''
                echo =====================================================
                echo Initializing Packer
                echo =====================================================

                wsl bash -c "cd /mnt/d/kvs-iac-project/packer && packer init ."
                '''
            }
        }

        /**********************************************************************
         * Validate Packer Template
         **********************************************************************/
        stage('Packer Validate') {
            steps {
                bat '''
                echo =====================================================
                echo Validating Packer Template
                echo =====================================================

                wsl bash -c "cd /mnt/d/kvs-iac-project/packer && packer validate ."
                '''
            }
        }

        /**********************************************************************
         * Build Golden AMI
         **********************************************************************/
        stage('Build Golden AMI') {
            steps {
                bat '''
                echo =====================================================
                echo Building Golden AMI
                echo Jenkins Build Number : %BUILD_NUMBER%
                echo Selected Branch      : %BRANCH%
                echo =====================================================

                wsl bash -c "cd /mnt/d/kvs-iac-project/packer && packer build -var 'build_number=%BUILD_NUMBER%' -color=false ."
                '''
            }
        }

        /**********************************************************************
         * Update Launch Template
         **********************************************************************/
        stage('Update Launch Template') {
            steps {
                bat '''
                echo =====================================================
                echo Updating Launch Template
                echo =====================================================

                wsl bash -c "cd /mnt/d/kvs-iac-project/scripts && chmod +x update_lt.sh && ./update_lt.sh"
                '''
            }
        }

        /**********************************************************************
         * Refresh Auto Scaling Group
         **********************************************************************/
        stage('Start ASG Instance Refresh') {
            steps {
                bat '''
                echo =====================================================
                echo Starting Auto Scaling Instance Refresh
                echo =====================================================

                wsl bash -c "cd /mnt/d/kvs-iac-project/scripts && chmod +x start_refresh.sh && ./start_refresh.sh"
                '''
            }
        }

    }

    post {

        success {

            echo '====================================================='
            echo 'KVS Infrastructure Automation Pipeline'
            echo '====================================================='
            echo "Git Branch           : ${params.BRANCH}"
            echo "Jenkins Build Number : ${env.BUILD_NUMBER}"
            echo 'Golden AMI Created Successfully'
            echo 'Launch Template Updated'
            echo 'Auto Scaling Group Refresh Started'
            echo 'Pipeline Completed Successfully'
            echo '====================================================='

        }

        failure {

            echo '====================================================='
            echo 'KVS Infrastructure Automation Pipeline'
            echo '====================================================='
            echo "Git Branch           : ${params.BRANCH}"
            echo "Jenkins Build Number : ${env.BUILD_NUMBER}"
            echo 'Pipeline Execution Failed'
            echo 'Review Jenkins Console Output'
            echo '====================================================='

        }

        always {
            cleanWs()
        }

    }

}