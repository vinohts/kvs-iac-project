---

# KVS Infrastructure Automation Platform – Master Project Context

## Project Overview

I am building an **Enterprise Infrastructure Automation Platform** on AWS as a learning project.

This project demonstrates:

* Git Branching Strategy
* Jenkins CI/CD
* Packer Golden AMI
* Ansible Provisioning
* Auto Scaling
* Application Load Balancer
* API Gateway
* Lambda
* DynamoDB
* Amazon SQS
* EC2 Worker Pattern
* Infrastructure Automation Portal

The goal is to build a production-style Infrastructure Automation Platform similar to enterprise tools like AWX/Ansible Tower, Rundeck, or internal DevOps automation portals.

---

# Current Git Branch Strategy

Repository contains two branches:

```
main
develop
```

Current workflow:

* **develop** → Development Environment
* **main** → Production Environment

Currently everything is deployed only in **Singapore (ap-southeast-1)**.

My next goal is:

* Keep **develop** in Singapore
* Deploy **main** into a second AWS Region
* Modify Jenkins so deployments automatically choose the correct region based on the selected branch.

---

# AWS Region (Current)

```
ap-southeast-1
Singapore
```

---

# Networking Resources

## Public Subnets

```
subnet-039830bde83c2fd96

subnet-0c4cd667cae1ac8cb
```

---

## Internet Gateway

```
igw-0ac47a3b0eb2aeaa6
```

---

## Route Table

```
rtb-0fa06ba82fd8041b9
```

---

# IAM Configuration

## IAM User

```
kvsadmin
```

Permission

```
AdministratorAccess
```

---

## EC2 IAM Role

```
EC2SSMRole
```

Attached Policy

```
AmazonSSMManagedInstanceCore
```

Purpose

* Session Manager
* Secure EC2 Login

---

## Project IAM Role

```
KVSIACProjectRole
```

Attached Policies

```
AmazonSSMManagedInstanceCore

KVS-IAC-CloudWatchPolicy

KVS-IAC-DynamoDBPolicy

KVS-IAC-S3Policy

KVS-IAC-SNSPolicy

KVS-IAC-SQSPolicy
```

This role is attached to EC2 instances created through the Launch Template.

---

# Load Balancer

Application Load Balancer ARN

```
arn:aws:elasticloadbalancing:ap-southeast-1:350025135544:loadbalancer/app/kvs-iac-project-alb/9569089ae0e20af6
```

---

# Target Group

```
arn:aws:elasticloadbalancing:ap-southeast-1:350025135544:targetgroup/kvs-iac-project-tg/f89c9daf05587f86
```

---

# Auto Scaling Group

```
arn:aws:autoscaling:ap-southeast-1:350025135544:autoScalingGroup:7023288e-6bf2-4134-a720-8e2373f5c104:autoScalingGroupName/kvs-iac-asg
```

---

# Launch Template

```
lt-07e3799cf0eb75d78
```

Deployment Model

```
Jenkins

↓

Packer

↓

Golden AMI

↓

Update Launch Template

↓

Instance Refresh
```

---

# API Gateway

API Name

```
kvs-infrastructure-api
```

API ID

```
yiqa8qmiua
```

Endpoint

```
POST /jobs
```

REST API is configured.

CORS has been fully configured and tested.

---

# Lambda

Function Name

```
kvs-job-request-handler
```

ARN

```
arn:aws:lambda:ap-southeast-1:350025135544:function:kvs-job-request-handler
```

Environment Variables

```
TABLE_NAME

QUEUE_URL
```

Responsibilities

* Receive Portal Request
* Generate Job ID
* Save Job into DynamoDB
* Send Job into Amazon SQS
* Return HTTP Response

---

# DynamoDB

Table

```
kvs-job-requests
```

ARN

```
arn:aws:dynamodb:ap-southeast-1:350025135544:table/kvs-job-requests
```

Primary Key

```
JobId
```

Capacity Mode

```
On-Demand
```

Purpose

Store all submitted jobs.

---

# Amazon SQS

Queue

```
kvs-job-queue
```

ARN

```
arn:aws:sqs:ap-southeast-1:350025135544:kvs-job-queue
```

Purpose

Acts as the message broker between Lambda and EC2 Worker.

---

# EC2 Worker

Python service

```
worker.py
```

Responsibilities

* Long Poll Amazon SQS
* Receive Job
* Execute Infrastructure Task
* Delete Message from Queue

Worker runs continuously on EC2.

---

# Portal

Technologies

```
HTML

CSS

JavaScript
```

The Portal calls API Gateway using JavaScript fetch().

Flow

```
Portal

↓

API Gateway

↓

Lambda

↓

DynamoDB

↓

Amazon SQS

↓

EC2 Worker

↓

Infrastructure Automation
```

Portal has already been integrated with API Gateway.

CORS issue has been resolved.

Job submission is working successfully.

---

# Jenkins Pipeline

Pipeline performs

```
Checkout

↓

Verify Environment

↓

Packer Init

↓

Packer Validate

↓

Build Golden AMI

↓

Update Launch Template

↓

Instance Refresh

↓

Cleanup
```

Pipeline currently deploys only to Singapore.

---

# Current Status

Everything is fully operational.

Portal

✓

API Gateway

✓

Lambda

✓

DynamoDB

✓

Amazon SQS

✓

Worker

✓

Golden AMI

✓

Launch Template

✓

Auto Scaling

✓

Application Load Balancer

✓

---

# Next Goal

I want to extend this project to support **multi-region deployment**.

Requirements:

* Keep **develop** branch deployed in Singapore.
* Deploy **main** branch into a second AWS Region (Production).
* Modify Jenkins so the deployment region is selected automatically based on the Git branch.
* Reuse as much existing infrastructure and code as possible.
* Follow enterprise DevOps best practices.
* Explain every step in detail so I understand the reasoning behind each decision, not just the implementation.

Please continue from this point without rebuilding the existing Singapore environment.

---

