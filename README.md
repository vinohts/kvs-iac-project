# KVS IAC Project – Golden AMI Rotation & Auto Scaling Group

## Project Overview

This project demonstrates a complete Infrastructure as Code (IaC) workflow using:

* GitHub
* Jenkins
* Ansible
* Packer
* AWS EC2
* Golden AMI
* Launch Templates
* Auto Scaling Groups (ASG)
* Application Load Balancer (ALB)
* AWS Systems Manager (SSM)

The objective was to create a reusable Golden AMI and deploy it through an Auto Scaling Group behind an Application Load Balancer.

---

# AWS Environment

## AWS Account

Account ID:

350025135544

Region:

ap-southeast-1

---

## Existing Network Components

### VPC

Name:

kvs-iac-project-vpc

VPC ID:

vpc-0373e053c8ddb2bb3

---

### Public Subnets

Subnet A:

subnet-088cee33520533800

Subnet B:

subnet-0c4cd667cae1ac8cb

Subnet C:

subnet-039830bde83c2fd96

---

### Internet Gateway

Name:

kvs-iac-project-igw

IGW ID:

igw-0ac47a3b0eb2aeaa6

---

### Route Table

Name:

kvs-iac-project-rt

Route Table ID:

rtb-0fa06ba82fd8041b9

Routes:

10.0.0.0/16 → local

0.0.0.0/0 → Internet Gateway

---

## IAM Components

### Build User

kvsadmin

Permissions:

AdministratorAccess

---

### EC2 IAM Role

Role Name:

EC2SSMRole

Policy:

AmazonSSMManagedInstanceCore

Purpose:

Enable AWS Systems Manager Session Manager access.

---

# Local Development Environment

## Operating System

Windows 11

---

## Tools Installed

### Jenkins

Installed locally on Windows

Status:

Working

---

### WSL Ubuntu

Installed and configured

Used for:

* Ansible
* Packer
* AWS CLI

---

### AWS CLI

Configured using:

aws configure

Verified using:

aws sts get-caller-identity

---

# GitHub Repository

Repository:

kvs-iac-project

Local Path:

D:\kvs-iac-project

---

# Project Structure

kvs-iac-project/

├── ansible/

│ ├── playbook.yml

│ └── website/

│ ├── index.html

│ ├── style.css

│ └── app.js

│

├── packer/

│ └── golden-ami.pkr.hcl

│

├── jenkins/

│ └── Jenkinsfile

│

├── docs/

│ └── project-documentation.md

│

└── README.md

---

# Phase 1 – Website Creation

Created a simple static website.

Files:

index.html

style.css

app.js

Purpose:

To validate that Apache serves application content from the Golden AMI.

---

# Phase 2 – Ansible Configuration

Created Ansible playbook.

Tasks:

* Update packages
* Install Apache
* Install Git
* Install Java 17
* Install Amazon SSM Agent
* Enable services
* Copy website files
* Restart Apache

---

# Phase 3 – Packer Configuration

Created:

golden-ami.pkr.hcl

Base Image:

Amazon Linux 2023

Provisioner:

ansible-local

Reason:

Avoid Ansible plugin issues between Windows and WSL.

---

# Phase 4 – Jenkins Integration

Verified Jenkins could execute commands inside WSL.

Tested:

wsl hostname

wsl whoami

wsl pwd

Result:

Success

---

# Phase 5 – AWS Connectivity Validation

Verified Jenkins access to AWS.

Command:

aws sts get-caller-identity

Result:

AWS Account Access Confirmed

Account:

350025135544

User:

kvsadmin

---

# Phase 6 – Golden AMI Build

Executed through Jenkins Pipeline.

Pipeline Stages:

1. Packer Init
2. Packer Validate
3. Packer Build

Result:

Golden AMI successfully created.

AMI ID:

ami-0ca8140b8f5fbf942

Validation:

Manual EC2 launched successfully.

Website accessible.

Apache running.

---

# Phase 7 – Launch Template

Created Launch Template.

Name:

kvs-iac-project-lt

Configuration:

* Amazon Linux 2023 Golden AMI
* t3.small
* EC2SSMRole
* Security Group
* Public IP Enabled

Purpose:

Reusable deployment template for ASG.

---

# Phase 8 – Auto Scaling Group

Created:

kvs-iac-project-asg

Configuration:

Minimum Capacity: 2

Desired Capacity: 2

Maximum Capacity: 4

Subnets:

* subnet-088cee33520533800
* subnet-0c4cd667cae1ac8cb
* subnet-039830bde83c2fd96

Result:

Instances launched successfully.

---

# Phase 9 – Application Load Balancer

Created:

kvs-iac-project-alb

Created Target Group:

kvs-iac-project-TG

Health Check:

/

Architecture:

Internet

↓

ALB

↓

Target Group

↓

ASG

↓

EC2 Instances

---

# Phase 10 – Troubleshooting

## Issue 1

Target Group unhealthy.

Error:

Request timed out

Root Cause:

Security Group configuration issue.

Fix:

Allowed HTTP traffic from ALB.

Result:

Targets became healthy.

---

## Issue 2

SSM Offline

Symptoms:

amazon-ssm-agent running

Instance not visible in Systems Manager

Error:

dial tcp xxx.xxx.xxx.xxx:443: i/o timeout

Root Cause:

Security Group outbound traffic blocked.

Fix:

Allowed outbound traffic:

All Traffic → 0.0.0.0/0

Result:

SSM Agent successfully registered.

Instance appeared Online.

---

# Final Working Architecture

Internet

↓

Application Load Balancer

↓

Target Group

↓

Auto Scaling Group

↓

Launch Template

↓

Golden AMI

↓

Amazon Linux 2023

Apache + Git + Java + SSM Agent

---

# Current Status

GitHub Repository: Complete

Ansible Playbook: Complete

Website Deployment: Complete

Packer Build: Complete

Golden AMI: Complete

Jenkins Integration: Complete

Launch Template: Complete

Auto Scaling Group: Complete

Application Load Balancer: Complete

Target Group: Healthy

SSM Connectivity: Complete

Project Status: Operational

---

# Next Phase

## Golden AMI Rotation Automation

Future work:

1. Jenkins builds new AMI
2. Create new Launch Template version
3. Update ASG
4. Trigger Instance Refresh
5. Replace old instances
6. Remove old AMIs

This will complete the full Golden AMI Rotation lifecycle.
