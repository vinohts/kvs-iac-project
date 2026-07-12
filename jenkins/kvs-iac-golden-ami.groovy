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

    environment {
        AWS_REGION = ''
        LAUNCH_TEMPLATE_ID = ''
        ASG_NAME = ''
        ENVIRONMENT_NAME = ''
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
         * Select Deployment Environment
         **********************************************************************/
        stage('Select Deployment Environment') {
            steps {
                script {

                    if (params.BRANCH == "develop") {

                        env.AWS_REGION = "ap-southeast-1"
                        env.LAUNCH_TEMPLATE_ID = "lt-07e3799cf0eb75d78"
                        env.ASG_NAME = "kvs-iac-asg"
                        env.ENVIRONMENT_NAME = "Development"

                    }
                    else if (params.BRANCH == "main") {

                        env.AWS_REGION = "ap-south-1"
                        env.LAUNCH_TEMPLATE_ID = "lt-0edcf35969e3b8ba4"
                        env.ASG_NAME = "kvs-iac-prod-asg"
                        env.ENVIRONMENT_NAME = "Production"

                    }
                    else {

                        error("Unsupported Branch : ${params.BRANCH}")

                    }

                    echo "=============================================="
                    echo "Environment : ${env.ENVIRONMENT_NAME}"
                    echo "Region      : ${env.AWS_REGION}"
                    echo "Launch Temp : ${env.LAUNCH_TEMPLATE_ID}"
                    echo "ASG         : ${env.ASG_NAME}"
                    echo "=============================================="

                }
            }
        }

        /**********************************************************************
         * Verify Build Environment
         **********************************************************************/
        stage('Verify Environment') {
            steps {
                bat """
                echo =====================================================
                echo Verifying Build Environment
                echo =====================================================

                echo Selected Branch : %BRANCH%
                echo AWS Region      : ${env.AWS_REGION}
                echo Launch Template : ${env.LAUNCH_TEMPLATE_ID}
                echo Auto Scaling    : ${env.ASG_NAME}

                wsl hostname
                wsl whoami
                wsl aws sts get-caller-identity
                wsl packer version
                wsl ansible-playbook --version
                """
            }
        }

        /**********************************************************************
         * Initialize Packer Plugins
         **********************************************************************/
        stage('Packer Init') {
            steps {
                bat """
                echo =====================================================
                echo Initializing Packer
                echo =====================================================

                wsl bash -c "cd /mnt/d/kvs-iac-project/packer && packer init ."
                """
            }
        }

        /**********************************************************************
         * Validate Packer Template
         **********************************************************************/
        stage('Packer Validate') {
            steps {
                bat """
                echo =====================================================
                echo Validating Packer Template
                echo =====================================================

                wsl bash -c "cd /mnt/d/kvs-iac-project/packer && packer validate ."
                """
            }
        }

        /**********************************************************************
         * Build Golden AMI
         **********************************************************************/
        stage('Build Golden AMI') {
            steps {
                bat """
                echo =====================================================
                echo Building Golden AMI
                echo =====================================================

                wsl bash -c "export AWS_REGION=${env.AWS_REGION} && cd /mnt/d/kvs-iac-project/packer && packer build -var aws_region=${env.AWS_REGION} -var build_number=%BUILD_NUMBER% -color=false ."
                """
            }
        }

        /**********************************************************************
         * Update Launch Template
         **********************************************************************/
        stage('Update Launch Template') {
            steps {
                bat """
                echo =====================================================
                echo Updating Launch Template
                echo =====================================================

                wsl bash -c "export AWS_REGION=${env.AWS_REGION}; export LAUNCH_TEMPLATE_ID=${env.LAUNCH_TEMPLATE_ID}; cd /mnt/d/kvs-iac-project/scripts && chmod +x update_lt.sh && ./update_lt.sh"
                """
            }
        }

        /**********************************************************************
         * Refresh Auto Scaling Group
         **********************************************************************/
        stage('Start ASG Instance Refresh') {
            steps {
                bat """
                echo =====================================================
                echo Starting Auto Scaling Instance Refresh
                echo =====================================================

                wsl bash -c "export AWS_REGION=${env.AWS_REGION}; export ASG_NAME=${env.ASG_NAME}; cd /mnt/d/kvs-iac-project/scripts && chmod +x start_refresh.sh && ./start_refresh.sh"
                """
            }
        }

    }

    post {

        success {

            echo '====================================================='
            echo 'KVS Infrastructure Automation Pipeline'
            echo '====================================================='
            echo "Environment          : ${env.ENVIRONMENT_NAME}"
            echo "Git Branch           : ${params.BRANCH}"
            echo "AWS Region           : ${env.AWS_REGION}"
            echo "Launch Template      : ${env.LAUNCH_TEMPLATE_ID}"
            echo "Auto Scaling Group   : ${env.ASG_NAME}"
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
            echo "Environment          : ${env.ENVIRONMENT_NAME}"
            echo "Git Branch           : ${params.BRANCH}"
            echo "AWS Region           : ${env.AWS_REGION}"
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