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

        stage('Checkout Source') {
            steps {
                git(
                    branch: params.BRANCH,
                    url: 'https://github.com/vinohts/kvs-iac-project.git'
                )
            }
        }

        stage('Select Deployment Environment') {
            steps {
                script {
                    if (params.BRANCH == "develop") {
                        env.AWS_REGION = "ap-southeast-1"
                        env.SUBNET_ID = "subnet-088cee33520533800"
                        env.LAUNCH_TEMPLATE_ID = "lt-07e3799cf0eb75d78"
                        env.ASG_NAME = "kvs-iac-asg"
                        env.ENVIRONMENT_NAME = "Development"
                    } else if (params.BRANCH == "main") {
                        env.AWS_REGION = "ap-south-1"
                        env.SUBNET_ID = "subnet-0155b7d44ca5d6c98"
                        env.LAUNCH_TEMPLATE_ID = "lt-0edcf35969e3b8ba4"
                        env.ASG_NAME = "kvs-iac-prod-asg"
                        env.ENVIRONMENT_NAME = "Production"
                    } else {
                        error("Unsupported Branch : ${params.BRANCH}")
                    }

                    echo "=============================================="
                    echo "Environment : ${env.ENVIRONMENT_NAME}"
                    echo "Region      : ${env.AWS_REGION}"
                    echo "Subnet      : ${env.SUBNET_ID}"
                    echo "Launch Temp : ${env.LAUNCH_TEMPLATE_ID}"
                    echo "ASG         : ${env.ASG_NAME}"
                    echo "=============================================="
                }
            }
        }

        stage('Verify Environment') {
            steps {
                bat """
                echo =====================================================
                echo Verifying Build Environment
                echo =====================================================

                echo Selected Branch : %BRANCH%
                echo AWS Region      : ${env.AWS_REGION}
                echo Subnet          : ${env.SUBNET_ID}
                echo Environment     : ${env.ENVIRONMENT_NAME}
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

        stage('Packer Init') {
            steps {
                bat 'wsl bash -c "cd /mnt/d/kvs-iac-project/packer && packer init ."'
            }
        }

        stage('Packer Validate') {
            steps {
                bat "wsl bash -c \"cd /mnt/d/kvs-iac-project/packer && packer validate -var aws_region=${env.AWS_REGION} -var subnet_id=${env.SUBNET_ID} -var environment=${env.ENVIRONMENT_NAME} .\""
            }
        }

        stage('Build Golden AMI') {
            steps {
                bat "wsl bash -c \"cd /mnt/d/kvs-iac-project/packer && packer build -var aws_region=${env.AWS_REGION} -var subnet_id=${env.SUBNET_ID} -var environment=${env.ENVIRONMENT_NAME} -var build_number=%BUILD_NUMBER% -color=false .\""
            }
        }

        stage('Update Launch Template') {
            steps {
                bat "wsl bash -c \"export AWS_REGION=${env.AWS_REGION}; export LAUNCH_TEMPLATE_ID=${env.LAUNCH_TEMPLATE_ID}; cd /mnt/d/kvs-iac-project/scripts && chmod +x update_lt.sh && ./update_lt.sh\""
            }
        }

        stage('Start ASG Instance Refresh') {
            steps {
                bat "wsl bash -c \"export AWS_REGION=${env.AWS_REGION}; export ASG_NAME=${env.ASG_NAME}; cd /mnt/d/kvs-iac-project/scripts && chmod +x start_refresh.sh && ./start_refresh.sh\""
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
            echo "Subnet               : ${env.SUBNET_ID}"
            echo "Launch Template      : ${env.LAUNCH_TEMPLATE_ID}"
            echo "Auto Scaling Group   : ${env.ASG_NAME}"
            echo "Jenkins Build Number : ${env.BUILD_NUMBER}"
            echo 'Pipeline Completed Successfully'
            echo '====================================================='
        }

        failure {
            echo '====================================================='
            echo 'Pipeline Execution Failed'
            echo "Environment          : ${env.ENVIRONMENT_NAME}"
            echo "Git Branch           : ${params.BRANCH}"
            echo "AWS Region           : ${env.AWS_REGION}"
            echo "Subnet               : ${env.SUBNET_ID}"
            echo '====================================================='
        }

        always {
            cleanWs()
        }
    }
}