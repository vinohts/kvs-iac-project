# KVS IAC Project – Detailed Summary (Phase 1 to Phase 3)

**Project Name:** kvs-iac-project
**AWS Account:** 350025135544
**Region:** ap-southeast-1
**Repository:** `https://github.com/vinohts/kvs-iac-project`

---

# Phase 1 – Environment Preparation

## Objective

Prepare local workstation and tools required for Infrastructure Automation practice.

---

## Local Environment

### Windows 11 Laptop

Installed and configured:

* Jenkins
* WSL Ubuntu
* AWS CLI
* Git
* Packer
* Ansible

---

## AWS Infrastructure Already Available

### VPC

| Resource | ID                    |
| -------- | --------------------- |
| VPC      | vpc-0373e053c8ddb2bb3 |

---

### Public Subnets

| Name     | Subnet ID                |
| -------- | ------------------------ |
| Public-A | subnet-088cee33520533800 |
| Public-B | subnet-0c4cd667cae1ac8cb |
| Public-C | subnet-039830bde83c2fd96 |

---

### Internet Gateway

| Resource | ID                    |
| -------- | --------------------- |
| IGW      | igw-0ac47a3b0eb2aeaa6 |

---

### Route Table

| Resource    | ID                    |
| ----------- | --------------------- |
| Route Table | rtb-0fa06ba82fd8041b9 |

---

### IAM

User:

```text
kvsadmin
```

Permission:

```text
AdministratorAccess
```

---

### EC2 Role

Role:

```text
EC2SSMRole
```

Policy:

```text
AmazonSSMManagedInstanceCore
```

---

# Phase 2 – Build Golden AMI using Packer + Ansible

## Objective

Create reusable Golden AMI containing:

* Amazon Linux 2023
* Apache
* Git
* Java 17
* Custom Web Application

---

# Repository Structure

```text
kvs-iac-project
│
├── ansible
│   ├── playbook.yml
│   └── website
│       ├── index.html
│       ├── style.css
│       └── app.js
│
├── packer
│   └── golden-ami.pkr.hcl
│
├── jenkins
│   └── Jenkinsfile
│
└── docs
```

---

# Ansible Playbook Created

File:

```text
ansible/playbook.yml
```

Tasks:

### Update Packages

```yaml
dnf update
```

---

### Install Apache

```yaml
httpd
```

---

### Install Git

```yaml
git
```

---

### Install Java

```yaml
java-17-amazon-corretto
```

---

### Enable Apache

```yaml
systemctl enable httpd
systemctl start httpd
```

---

### Deploy Website

Copies:

```text
index.html
style.css
app.js
```

to

```text
/var/www/html
```

---

### Restart Apache

```yaml
service:
  name: httpd
  state: restarted
```

---

# Website Version 1

Created a simple webpage displaying:

```text
KVS IAC Project
Golden AMI Version 1
Apache + Git + Java
```

---

# Packer Configuration

File:

```text
packer/golden-ami.pkr.hcl
```

Builder:

```hcl
amazon-ebs
```

Base Image:

```text
Amazon Linux 2023
```

---

Instance Type:

```text
t3.small
```

Subnet:

```text
subnet-088cee33520533800
```

IAM Profile:

```text
EC2SSMRole
```

---

AMI Naming

```text
kvs-iac-golden-ami-<timestamp>
```

Example:

```text
kvs-iac-golden-ami-1782053072
```

---

# Challenges Encountered

## Problem 1

```text
Unknown provisioner type "ansible"
```

Reason:

```text
Packer Ansible plugin missing
```

Resolution:

```bash
packer init .
```

Plugin installed successfully.

---

## Problem 2

```text
ansible-playbook executable not found
```

Reason:

```text
Packer running in Windows
Ansible installed in WSL
```

Resolution:

Moved build execution into WSL.

---

## Problem 3

```text
scp/sftp connection failures
```

Error:

```text
/ usr/lib/sftp-server not found
```

Reason:

Amazon Linux 2023 + ansible provisioner compatibility issue.

---

Resolution

Switched from:

```hcl
provisioner "ansible"
```

to

```hcl
provisioner "ansible-local"
```

and installed Ansible inside build instance.

---

# Jenkins Integration

## Objective

Automate Golden AMI creation.

---

### WSL Validation Pipeline

Verified Jenkins can execute WSL commands.

Output:

```bash
hostname
whoami
pwd
```

Success.

---

### AWS Validation

Executed:

```bash
aws sts get-caller-identity
```

Verified:

```text
Account:
350025135544
```

---

# Jenkins Pipeline Created

Stages:

```text
Packer Init
Packer Validate
Build Golden AMI
```

Commands:

```bash
wsl bash -c "cd /mnt/d/kvs-iac-project/packer && packer init ."

wsl bash -c "cd /mnt/d/kvs-iac-project/packer && packer validate ."

wsl bash -c "cd /mnt/d/kvs-iac-project/packer && packer build ."
```

---

# Golden AMI Successfully Created

Final AMI:

```text
ami-0ca8140b8f5fbf942
```

Validation:

Launched EC2 manually.

Verified:

```bash
systemctl status httpd
```

Success.

---

Opened Browser:

```text
http://<Public-IP>
```

Website loaded successfully.

---

# Phase 3 – Launch Template & Auto Scaling Group

## Objective

Deploy Golden AMI through Auto Scaling.

---

# Launch Template Created

Name:

```text
kvs-iac-project-lt
```

AMI:

```text
ami-0ca8140b8f5fbf942
```

Instance Type:

```text
t3.micro
```

IAM Role:

```text
EC2SSMRole
```

Network:

```text
Auto Assign Public IP = Enabled
```

Security Group:

```text
HTTP 80
SSH 22
```

---

# Auto Scaling Group Created

Name:

```text
kvs-iac-project-asg
```

Subnets:

```text
subnet-088cee33520533800
subnet-0c4cd667cae1ac8cb
subnet-039830bde83c2fd96
```

---

Capacity:

```text
Min     = 2
Desired = 2
Max     = 2
```

Purpose:

```text
Maintain exactly 2 instances
```

---

# ALB Created

Name:

```text
kvs-iac-project-alb
```

Type:

```text
Internet Facing
```

---

# Target Group Created

Name:

```text
kvs-iac-project-tg
```

Protocol:

```text
HTTP
```

Port:

```text
80
```

Health Check Path:

```text
/
```

---

# Issue Encountered – Target Unhealthy

Reason:

Instances not reachable.

Investigation:

```text
Health checks timing out
```

---

# Issue Encountered – SSM Offline

Agent running:

```bash
systemctl status amazon-ssm-agent
```

But logs showed:

```text
dial tcp ssm.ap-southeast-1.amazonaws.com:443
i/o timeout
```

---

Root Cause

Outbound Security Group rules blocked internet access.

---

Fix

Allowed:

```text
Outbound
All Traffic
0.0.0.0/0
```

---

After Fix

Verified:

```bash
curl https://ssm.ap-southeast-1.amazonaws.com
```

Success.

---

SSM Status:

```text
Online
```

---

Target Group:

```text
Healthy
```

---

ALB:

```text
Working
```

---

# Mixed Instance Issue Learned

Observed:

```text
ASG launching c7i-flex.large
```

instead of

```text
t3.micro
```

Reason:

ASG created using:

```text
Mixed Instances Policy
```

with

```text
Instance Requirements
```

enabled.

AWS automatically selected cheapest matching instance.

---

Resolution

Create ASG using:

```text
Launch Template only
```

Disable:

```text
Mixed Instances Policy
```

No Instance Requirements.

---

Expected Result

ASG launches exactly:

```text
t3.micro
```

or

```text
t3.small
```

as specified in Launch Template.

---

# Current Project Status

✅ GitHub Repository Created

✅ Jenkins Working

✅ WSL Working

✅ AWS CLI Working

✅ Packer Working

✅ Ansible Working

✅ Golden AMI Created

✅ Website Deployed

✅ Launch Template Tested

✅ Auto Scaling Tested

✅ ALB Tested

✅ Target Group Tested

✅ SSM Working

✅ Learned Mixed Instance Behavior

✅ Cleaned up ALB, TG and ASG to avoid costs

---

## Next Phase (Phase 4)

We can continue with:

1. Create clean Launch Template (fixed instance type)
2. Create clean ASG
3. Create Version 2 / Version 3 colorful website
4. Build New Golden AMI
5. Launch Template Versioning
6. AMI Rotation Automation
7. Jenkins Pipeline for ASG Refresh
8. Fully Automated Golden AMI Rotation Project

This will complete the end-to-end Golden AMI + ASG rotation practice project.
