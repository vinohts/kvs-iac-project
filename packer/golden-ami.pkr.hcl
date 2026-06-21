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

source "amazon-ebs" "golden" {

  region = "ap-southeast-1"

  instance_type = "t3.small"

  ssh_username = "ec2-user"

  ami_name = "kvs-iac-golden-ami-V2.0-{{timestamp}}"

  tags = {
    Name        = "kvs-iac-golden-ami"
    Project     = "kvs-iac-project"
    Environment = "Lab"
    CreatedBy   = "Jenkins"
  }

  run_tags = {
    Name = "packer-build-instance"
  }

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

  subnet_id = "subnet-088cee33520533800"

  associate_public_ip_address = true

  iam_instance_profile = "EC2SSMRole"
}

build {

  sources = [
    "source.amazon-ebs.golden"
  ]

  provisioner "shell" {
    inline = [
      "sudo dnf install -y ansible-core"
    ]
  }

  provisioner "ansible-local" {
    playbook_file = "../ansible/playbook.yml"
    playbook_dir  = "../ansible"
  }

}