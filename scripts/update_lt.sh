#!/bin/bash

###############################################################################
# KVS Infrastructure Automation Project
#
# Script Name : update_lt.sh
# Purpose     : Update Launch Template with Newly Created Golden AMI
# Author      : Vinoth Kumar
#
# Phase 2.1
# ----------
# * Read latest AMI from manifest.json
# * Create new Launch Template Version
# * Attach latest Golden AMI
# * Attach IAM Instance Profile
# * Set new version as Default
###############################################################################

set -e

###############################################################################
# Configuration
###############################################################################

# These values are passed from Jenkins

REGION="${AWS_REGION}"
LAUNCH_TEMPLATE_ID="${LAUNCH_TEMPLATE_ID}"

INSTANCE_PROFILE_NAME="KVSIACProjectRole"

MANIFEST="/mnt/d/kvs-iac-project/packer/manifest.json"

###############################################################################
# Validate Environment Variables
###############################################################################

if [ -z "$REGION" ]; then
    echo "ERROR: AWS_REGION environment variable not set."
    exit 1
fi

if [ -z "$LAUNCH_TEMPLATE_ID" ]; then
    echo "ERROR: LAUNCH_TEMPLATE_ID environment variable not set."
    exit 1
fi

###############################################################################
# Read Latest AMI
###############################################################################

echo "======================================================"
echo "KVS Launch Template Update"
echo "======================================================"

echo "AWS Region         : $REGION"
echo "Launch Template ID : $LAUNCH_TEMPLATE_ID"

if [ ! -f "$MANIFEST" ]; then
    echo "ERROR: manifest.json not found."
    exit 1
fi

AMI_ID=$(grep -o 'ami-[a-zA-Z0-9]*' "$MANIFEST" | tail -1)

if [ -z "$AMI_ID" ]; then
    echo "ERROR: Unable to determine latest AMI."
    exit 1
fi

echo ""
echo "Latest Golden AMI : $AMI_ID"
echo ""

###############################################################################
# Create New Launch Template Version
###############################################################################

echo "Creating Launch Template Version..."

aws ec2 create-launch-template-version \
    --region "$REGION" \
    --launch-template-id "$LAUNCH_TEMPLATE_ID" \
    --source-version '$Latest' \
    --launch-template-data "{
        \"ImageId\":\"$AMI_ID\",
        \"IamInstanceProfile\":{
            \"Name\":\"$INSTANCE_PROFILE_NAME\"
        }
    }"

###############################################################################
# Obtain Latest Version Number
###############################################################################

LATEST_VERSION=$(aws ec2 describe-launch-template-versions \
    --region "$REGION" \
    --launch-template-id "$LAUNCH_TEMPLATE_ID" \
    --versions '$Latest' \
    --query 'LaunchTemplateVersions[0].VersionNumber' \
    --output text)

echo ""
echo "Latest Launch Template Version : $LATEST_VERSION"
echo ""

###############################################################################
# Set Default Version
###############################################################################

echo "Updating Default Launch Template Version..."

aws ec2 modify-launch-template \
    --region "$REGION" \
    --launch-template-id "$LAUNCH_TEMPLATE_ID" \
    --default-version "$LATEST_VERSION"

###############################################################################
# Completed
###############################################################################

echo ""
echo "======================================================"
echo "Launch Template Updated Successfully"
echo "======================================================"
echo "AWS Region         : $REGION"
echo "Launch Template ID : $LAUNCH_TEMPLATE_ID"
echo "Version            : $LATEST_VERSION"
echo "Golden AMI         : $AMI_ID"
echo "IAM Role           : $INSTANCE_PROFILE_NAME"
echo "======================================================"