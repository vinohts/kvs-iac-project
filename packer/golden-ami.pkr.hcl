```hcl
###############################################################################
# KVS Infrastructure Automation Project
#
# File Name  : golden-ami.pkr.hcl
# Purpose    : Build Enterprise Golden AMI
# Author     : Vinoth Kumar
#
# Components Installed
# --------------------
# - Amazon Linux 2023
# - Apache HTTP Server
# - Git
# - Java 17 (Amazon Corretto)
# - Python 3
# - Enterprise Dashboard Website
# - KVS Worker Service
#
# Build Tool : Packer
# Provisioner: Ansible Local
###############################################################################

packer {

  required_plugins {

    amazon = {
      source  = "github.com/hashicorp/amazon"
      version = ">= 1.3.0"
    }

    ansible = {
      source  = "github.com/hashicorp/ansible"
      version = ">= 1.1.0"
    }

  }

}

###############################################################################
# Amazon EBS Builder
###############################################################################

source "amazon-ebs" "golden" {

  ####################################################
  # AWS Configuration
  ####################################################

  region = "ap-southeast-1"

  subnet_id = "subnet-088cee33520533800"

  associate_public_ip_address = true

  iam_instance_profile = "EC2SSMRole"

  ####################################################
  # EC2 Build Configuration
  ####################################################

  instance_type = "t3.small"

  ssh_username = "ec2-user"

  ####################################################
  # Golden AMI Naming
  ####################################################

  ami_name = "kvs-iac-golden-ami-v3-worker-{{timestamp}}"

  ####################################################
  # Resource Tags
  ####################################################

  tags = {

    Name          = "kvs-iac-golden-ami"

    Project       = "kvs-iac-project"

    Environment   = "Development"

    CreatedBy     = "Jenkins"

    Owner         = "Vinoth Kumar"

    Version       = "v3"

    Configuration = "Apache-Git-Java17-Python3-Worker"

  }

  ####################################################
  # Temporary EC2 Build Instance Tags
  ####################################################

  run_tags = {

    Name = "packer-build-instance"

    Project = "kvs-iac-project"

  }

  ####################################################
  # Source AMI
  ####################################################

  source_ami_filter {

    filters = {

      name                = "al2023-ami-2023*"

      root-device-type    = "ebs"

      virtualization-type = "hvm"

      architecture        = "x86_64"

    }

    owners = ["137112412989"]

    most_recent = true

  }

}

###############################################################################
# Build Section
###############################################################################

build {

  sources = [

    "source.amazon-ebs.golden"

  ]

  ####################################################
  # Install Ansible inside Build Instance
  ####################################################

  provisioner "shell" {

    inline = [

      "sudo dnf install -y ansible-core"

    ]

  }

  ####################################################
  # Execute Ansible Playbook
  ####################################################

  provisioner "ansible-local" {

    playbook_file = "../ansible/playbook.yml"

    playbook_dir  = "../ansible"

  }

  ####################################################
  # Generate Build Manifest
  ####################################################

  post-processor "manifest" {

    output = "manifest.json"

  }

}
```
