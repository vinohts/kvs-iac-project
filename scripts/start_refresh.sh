#!/bin/bash

###############################################################################
# KVS Infrastructure Automation Project
#
# Script Name : start_refresh.sh
# Purpose     : Start Auto Scaling Group Instance Refresh
# Author      : Vinoth Kumar
#
# Auto Scaling Group
# ------------------
# kvs-iac-project-asg
###############################################################################

set -e

ASG_NAME="kvs-iac-asg"

echo "==========================================================="
echo " KVS Infrastructure Automation"
echo " Auto Scaling Group Instance Refresh"
echo "==========================================================="

###############################################################
# Verify AWS CLI Authentication
###############################################################

echo ""
echo "Verifying AWS Credentials..."

aws sts get-caller-identity > /dev/null

echo "AWS Authentication Successful"

###############################################################
# Verify ASG Exists
###############################################################

echo ""
echo "Checking Auto Scaling Group..."

ASG_EXISTS=$(aws autoscaling describe-auto-scaling-groups \
    --auto-scaling-group-names "$ASG_NAME" \
    --query "AutoScalingGroups[0].AutoScalingGroupName" \
    --output text)

if [ "$ASG_EXISTS" == "None" ]; then
    echo "ERROR: Auto Scaling Group not found!"
    exit 1
fi

echo "Auto Scaling Group Found: $ASG_NAME"

###############################################################
# Start Instance Refresh
###############################################################

echo ""
echo "Starting Instance Refresh..."

REFRESH_ID=$(aws autoscaling start-instance-refresh \
    --auto-scaling-group-name "$ASG_NAME" \
    --preferences MinHealthyPercentage=100,InstanceWarmup=60 \
    --query "InstanceRefreshId" \
    --output text)

###############################################################
# Display Results
###############################################################

echo ""
echo "==========================================================="
echo " Instance Refresh Started Successfully"
echo "==========================================================="

echo "Auto Scaling Group : $ASG_NAME"
echo "Refresh ID         : $REFRESH_ID"

echo ""
echo "Monitor Progress Using:"
echo ""
echo "aws autoscaling describe-instance-refreshes \\"
echo "    --auto-scaling-group-name $ASG_NAME"

echo ""
echo "==========================================================="