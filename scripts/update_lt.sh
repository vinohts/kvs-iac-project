#!/bin/bash

###############################################################################
# KVS Infrastructure Automation Project
#
# Script Name : update_lt.sh
# Purpose     : Update Launch Template with Newly Created AMI
###############################################################################

set -e

echo "======================================================"
echo "Updating Launch Template"
echo "======================================================"

MANIFEST="/mnt/d/kvs-iac-project/packer/manifest.json"

if [ ! -f "$MANIFEST" ]; then
    echo "ERROR: manifest.json not found!"
    exit 1
fi

AMI_ID=$(grep -o 'ami-[a-zA-Z0-9]*' "$MANIFEST" | tail -1)

if [ -z "$AMI_ID" ]; then
    echo "ERROR: Unable to determine AMI ID"
    exit 1
fi

echo "Latest AMI : $AMI_ID"

aws ec2 create-launch-template-version \
    --launch-template-id lt-07e3799cf0eb75d78 \
    --source-version '$Latest' \
    --launch-template-data "{\"ImageId\":\"$AMI_ID\"}"

LATEST_VERSION=$(aws ec2 describe-launch-template-versions \
    --launch-template-id lt-07e3799cf0eb75d78 \
    --versions '$Latest' \
    --query 'LaunchTemplateVersions[0].VersionNumber' \
    --output text)

echo "Latest Launch Template Version : $LATEST_VERSION"

aws ec2 modify-launch-template \
    --launch-template-id lt-07e3799cf0eb75d78 \
    --default-version "$LATEST_VERSION"

echo "======================================================"
echo "Launch Template Updated Successfully"
echo "======================================================"