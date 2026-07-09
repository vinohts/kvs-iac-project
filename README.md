Absolutely. I think this project has grown beyond a collection of notes. It's now a **complete DevOps/IaC portfolio project**, and it deserves professional documentation.

Considering everything we've built together, I recommend organizing it like a real implementation guide rather than just phase notes.

---

# KVS Infrastructure as Code (IaC) Project

## Complete Project Documentation

**Version:** 1.0

**Author:** Vinoth Kumar

**Cloud Provider:** Amazon Web Services (AWS)

**Region:** ap-southeast-1

**Project Type:** Infrastructure as Code (IaC)

**Automation Tools:**

* Packer
* Ansible
* Jenkins
* GitHub
* AWS CLI
* WSL Ubuntu

---

# Table of Contents

## Chapter 1 – Project Overview

1. Introduction
2. Business Problem
3. Project Objectives
4. Solution Overview
5. Project Scope

---

## Chapter 2 – Solution Architecture

* High Level Architecture
* Infrastructure Components
* End-to-End Workflow
* Technology Stack

Architecture Diagram

```text
Developer
      │
      ▼
GitHub Repository
      │
      ▼
Jenkins Pipeline (SCM)
      │
      ▼
WSL Ubuntu
      │
      ▼
Packer
      │
      ▼
Temporary EC2
      │
      ▼
Ansible Local
      │
      ▼
Golden AMI
      │
      ▼
Launch Template
      │
      ▼
Auto Scaling Group
      │
      ▼
Application Load Balancer
      │
      ▼
End Users
```

---

# Chapter 3 – Local Environment Setup

## Windows Machine

* Windows 11
* Git
* Jenkins
* WSL Ubuntu

## WSL Packages

* AWS CLI
* Git
* Packer
* Ansible
* Python
* SSH

---

# Chapter 4 – AWS Environment

## AWS Account

```
350025135544
```

## Region

```
ap-southeast-1
```

---

## Networking

VPC

```
vpc-0373e053c8ddb2bb3
```

Public Subnets

```
subnet-088cee33520533800

subnet-039830bde83c2fd96

subnet-0c4cd667cae1ac8cb
```

Internet Gateway

```
igw-0ac47a3b0eb2aeaa6
```

Route Table

```
rtb-0fa06ba82fd8041b9
```

---

# Chapter 5 – IAM Configuration

IAM User

```
kvsadmin
```

Permissions

```
AdministratorAccess
```

---

IAM Role

```
EC2SSMRole
```

Attached Policy

```
AmazonSSMManagedInstanceCore
```

Purpose

Allow EC2 instances to register with AWS Systems Manager.

---

# Chapter 6 – Repository Structure

```
kvs-iac-project
│
├── ansible
│   ├── playbook.yml
│   ├── inventory.ini
│   └── website
│       ├── index.html
│       ├── style.css
│       └── app.js
│
├── packer
│   ├── golden-ami.pkr.hcl
│   └── variables.pkr.hcl
│
├── jenkins
│   └── kvs-iac-golden-ami.groovy
│
├── scripts
│   ├── ami_rotation.sh
│   ├── update_lt.sh
│   └── start_refresh.sh
│
├── docs
│
└── README.md
```

---

# Chapter 7 – GitHub Integration

Repository

```
https://github.com/vinohts/kvs-iac-project
```

Repository Organization

* Source Code
* Infrastructure Code
* Jenkins Pipeline
* Documentation

---

# Chapter 8 – Packer

Objectives

* Create reusable AMIs
* Standardize server builds
* Eliminate manual installation

Builder

```
amazon-ebs
```

Base Image

```
Amazon Linux 2023
```

Provisioner

```
ansible-local
```

AMI Naming

```
kvs-iac-golden-ami-<timestamp>
```

---

# Chapter 9 – Ansible

Playbook

```
ansible/playbook.yml
```

Tasks

* Update Packages
* Install Apache
* Install Git
* Install Java
* Enable Apache
* Deploy Website
* Restart Apache

---

# Chapter 10 – Website Versions

## Version 1

Basic HTML page

---

## Version 2

Improved UI

---

## Version 3

Modern colorful responsive webpage

Each version generated a new Golden AMI.

---

# Chapter 11 – Jenkins

Pipeline Type

```
Pipeline Script from SCM
```

Repository

GitHub

Pipeline Script

```
jenkins/kvs-iac-golden-ami.groovy
```

---

Pipeline Stages

```
Checkout Source

↓

Verify Environment

↓

Packer Init

↓

Packer Validate

↓

Build Golden AMI
```

---

Verification

```
hostname

whoami

aws sts get-caller-identity

packer version

ansible-playbook --version
```

---

# Chapter 12 – Golden AMI

Build Process

```
GitHub

↓

Jenkins

↓

WSL

↓

Packer

↓

Temporary EC2

↓

Ansible Local

↓

Golden AMI
```

Validation

Created EC2 manually

Verified

* Apache
* Git
* Java
* Website
* SSM

---

# Chapter 13 – Launch Template

Configuration

* Golden AMI
* t3.micro
* Security Group
* IAM Role
* Public IP Enabled

---

# Chapter 14 – Auto Scaling Group

Configuration

```
Min = 2

Desired = 2

Max = 2
```

Subnets

3 Public Subnets

Purpose

Maintain two healthy web servers.

---

# Chapter 15 – Application Load Balancer

Created

```
kvs-iac-project-alb
```

Target Group

```
kvs-iac-project-tg
```

Health Check

```
/
```

Validated

* Healthy Targets
* Traffic Distribution

---

# Chapter 16 – AWS Systems Manager

Role

```
EC2SSMRole
```

Issue

```
Offline
```

Investigation

* IAM
* Agent
* Logs
* Route Table
* Security Group

Root Cause

Outbound Security Group blocked HTTPS.

Solution

```
Outbound

All Traffic

0.0.0.0/0
```

Result

```
SSM Online
```

---

# Chapter 17 – Troubleshooting

### Packer Plugin Missing

Resolution

```
packer init
```

---

### Missing Build Block

Fixed HCL.

---

### SCP Failure

```
/usr/lib/sftp-server
```

Solution

```
ansible-local
```

---

### Jenkins WSL

Corrected

```
wsl bash -c
```

---

### ALB Health Check

Corrected

```
/
```

---

### SSM Offline

Corrected outbound rules.

---

### Mixed Instance Issue

AWS launched

```
c7i-flex.large
```

Reason

Mixed Instance Policy

Solution

Launch Template Only

---

# Chapter 18 – Lessons Learned

* Infrastructure as Code fundamentals
* Immutable infrastructure with Golden AMIs
* Configuration management using Ansible
* CI/CD with Jenkins
* GitHub SCM integration
* WSL automation on Windows
* IAM role design
* SSM troubleshooting
* ALB health checks
* Auto Scaling Group behavior
* Launch Template versioning
* Importance of Security Group egress rules
* Difference between Launch Templates and Mixed Instances Policies

---

# Chapter 19 – Skills Gained

* AWS EC2
* IAM
* VPC
* Route Tables
* Security Groups
* Systems Manager
* Packer
* Ansible
* Git
* GitHub
* Jenkins
* Groovy Pipelines
* WSL
* Launch Templates
* Auto Scaling Groups
* Application Load Balancers
* Golden AMI creation
* Infrastructure troubleshooting

---

# Chapter 20 – Final Architecture

```
GitHub
      │
      ▼
Jenkins Pipeline
      │
      ▼
WSL Ubuntu
      │
      ▼
Packer
      │
      ▼
Temporary EC2
      │
      ▼
Ansible Local
      │
      ▼
Golden AMI
      │
      ▼
Launch Template
      │
      ▼
Auto Scaling Group
      │
      ▼
Application Load Balancer
      │
      ▼
Users
```

---

# Chapter 21 – Current Project Status

| Component                 | Status      |
| ------------------------- | ----------- |
| GitHub Repository         | ✅ Completed |
| Jenkins Pipeline from SCM | ✅ Completed |
| WSL Integration           | ✅ Completed |
| AWS CLI                   | ✅ Completed |
| Packer                    | ✅ Completed |
| Ansible                   | ✅ Completed |
| Golden AMI                | ✅ Completed |
| Website Versioning        | ✅ Completed |
| Launch Template           | ✅ Completed |
| Auto Scaling Group        | ✅ Completed |
| Application Load Balancer | ✅ Completed |
| Target Group              | ✅ Completed |
| Systems Manager           | ✅ Completed |
| Health Checks             | ✅ Completed |
| Troubleshooting           | ✅ Completed |

---

# Phase 2 Roadmap

The next phase of the project will build on this foundation by introducing an event-driven processing workflow:

```
User
    │
    ▼
Web UI
    │
    ▼
Database
(Status = QUEUED)
    │
    ▼
EventBridge
    │
    ▼
Lambda
    │
    ▼
Auto Scaling Group
    │
    ▼
EC2 Worker Instances
    │
    ▼
Process Jobs
    │
    ▼
Update Database
(Status = COMPLETED)
    │
    ▼
Lambda detects empty queue
    │
    ▼
Scale ASG to Zero
```

