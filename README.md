# KVS IAC PROJECT - PHASE 1 SUMMARY

## Project Name

KVS IAC Project – Golden AMI Rotation & Auto Scaling Group

---

# Objective

Build a practical Infrastructure as Code project using:

* GitHub
* Jenkins
* Ansible
* Packer
* AWS EC2
* Golden AMI
* Launch Template
* Auto Scaling Group
* Application Load Balancer
* AWS Systems Manager

Goal:

Create a Golden AMI and deploy it through an Auto Scaling Group behind an Application Load Balancer.

---

# Environment Details

## Local Environment

Operating System:

Windows 11

Tools Installed:

* Jenkins
* WSL Ubuntu
* AWS CLI
* Packer
* Ansible
* Git

Repository Location:

D:\kvs-iac-project

GitHub Repository:

https://github.com/vinohts/kvs-iac-project

---

# AWS Environment

Account ID:

350025135544

Region:

ap-southeast-1

IAM User:

kvsadmin

Permissions:

AdministratorAccess

---

# Existing AWS Infrastructure

## VPC

Name:

kvs-iac-project-vpc

ID:

vpc-0373e053c8ddb2bb3

---

## Public Subnets

subnet-088cee33520533800

subnet-0c4cd667cae1ac8cb

subnet-039830bde83c2fd96

---

## Internet Gateway

igw-0ac47a3b0eb2aeaa6

---

## Route Table

rtb-0fa06ba82fd8041b9

Routes:

10.0.0.0/16 → local

0.0.0.0/0 → Internet Gateway

---

# IAM Configuration

## EC2SSMRole

Created IAM Role:

EC2SSMRole

Policy Attached:

AmazonSSMManagedInstanceCore

Purpose:

Allow EC2 instances to connect to AWS Systems Manager.

---

# GitHub Repository Setup

Created repository:

kvs-iac-project

Created project structure:

kvs-iac-project/

├── ansible/

├── packer/

├── jenkins/

├── docs/

└── README.md

---

# Website Deployment

Created a sample web application.

Files:

index.html

style.css

app.js

Purpose:

Validate Apache deployment from Golden AMI.

---

# Ansible Configuration

Created:

ansible/playbook.yml

Tasks:

* Update packages
* Install Apache
* Install Git
* Install Java 17
* Install SSM Agent
* Enable Apache
* Start Apache
* Deploy Website
* Restart Apache

Result:

Fully automated server configuration.

---

# Packer Configuration

Created:

packer/golden-ami.pkr.hcl

Base Image:

Amazon Linux 2023

Provisioning Method:

ansible-local

Reason:

Avoid Windows ↔ WSL Ansible plugin issues.

---

# Jenkins Integration

Verified Jenkins can execute commands inside WSL.

Validation:

wsl hostname

wsl whoami

wsl pwd

Result:

SUCCESS

---

# AWS Access Validation

Verified Jenkins AWS access.

Command:

aws sts get-caller-identity

Result:

User:

kvsadmin

Account:

350025135544

Status:

SUCCESS

---

# Golden AMI Creation

Jenkins Pipeline:

Packer Init

Packer Validate

Packer Build

Result:

Golden AMI Created Successfully

AMI ID:

ami-0ca8140b8f5fbf942

---

# AMI Validation

Launched EC2 manually using:

ami-0ca8140b8f5fbf942

Validation:

Apache running

Git installed

Java installed

Website accessible

Result:

SUCCESS

---

# Launch Template

Created:

kvs-iac-project-lt

Configuration:

AMI:

ami-0ca8140b8f5fbf942

Instance Type:

t3.small

IAM Role:

EC2SSMRole

Public IP:

Enabled

Result:

SUCCESS

---

# Auto Scaling Group

Created:

kvs-iac-project-asg

Configuration:

Min:

2

Desired:

2

Max:

3

Subnets:

All three public subnets

Result:

Instances launched successfully.

---

# Important Learning

During ASG creation:

AWS was configured with:

MixedInstancesPolicy

Instead of:

Launch Template Only

Result:

AWS launched:

c7i-flex.large

instead of:

t3.small

Reason:

Instance Requirements was enabled.

Lesson Learned:

For Golden AMI projects, use:

Launch Template Only

Avoid:

Mixed Instances Policy

unless intentionally required.

---

# Application Load Balancer

Created:

kvs-iac-project-alb

Created Target Group:

kvs-iac-project-TG

Health Check Path:

/

Result:

Website available through ALB.

---

# Troubleshooting Performed

## Target Group Unhealthy

Issue:

Request Timed Out

Root Cause:

Security Group configuration

Resolution:

Allowed required inbound traffic

Result:

Targets Healthy

---

## SSM Offline

Issue:

SSM Agent running

Instance Offline

Error:

dial tcp x.x.x.x:443: i/o timeout

Root Cause:

Security Group outbound rules blocked

Resolution:

Allowed outbound traffic

All Traffic → 0.0.0.0/0

Result:

SSM Online

Instance registered successfully

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

↓

Apache + Git + Java + SSM Agent

---

# Cost Optimization

Deleted:

* Auto Scaling Group
* EC2 Instances
* Application Load Balancer

Reason:

Avoid unnecessary AWS charges.

---

# Resources Retained

Golden AMI:

ami-0ca8140b8f5fbf942

Launch Template:

kvs-iac-project-lt

IAM Role:

EC2SSMRole

VPC

Subnets

Route Table

Internet Gateway

GitHub Repository

Jenkins Configuration

Ansible Code

Packer Code

---

# Project Status

GitHub Integration:
COMPLETED

Ansible Automation:
COMPLETED

Packer Build:
COMPLETED

Golden AMI:
COMPLETED

Jenkins Pipeline:
COMPLETED

Launch Template:
COMPLETED

Auto Scaling Group:
COMPLETED

Application Load Balancer:
COMPLETED

Systems Manager:
COMPLETED

Cost Cleanup:
COMPLETED

Overall Status:

PHASE 1 SUCCESSFULLY COMPLETED

---

# Next Session Plan

Phase 2 – Golden AMI Rotation

Tasks:

1. Modify Website (Version 3)
2. Build New Golden AMI
3. Create Launch Template Version
4. Update ASG
5. Trigger Instance Refresh
6. Validate New Version Through ALB
7. Automate Entire Process Using Jenkins

Expected Outcome:

Fully Automated Golden AMI Rotation Pipeline.
