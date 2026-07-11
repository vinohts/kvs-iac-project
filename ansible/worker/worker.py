#!/usr/bin/env python3

###############################################################################
# KVS Infrastructure Automation Worker
#
# Purpose : Background Worker Service
# Author  : Vinoth Kumar
#
# Phase 2
# --------
# • Starts automatically with systemd
# • Generates heartbeat logs
# • Displays EC2 infrastructure telemetry
# • Uses AWS IMDSv2 (Recommended)
###############################################################################

import time
import logging
import socket
import os
import urllib.request

###############################################################################
# Logging Configuration
###############################################################################

logging.basicConfig(
    filename="/var/log/kvs-worker.log",
    level=logging.INFO,
    format="%(asctime)s %(levelname)s %(message)s"
)

###############################################################################
# AWS IMDSv2 Functions
###############################################################################

METADATA_URL = "http://169.254.169.254/latest"


def get_token():
    """Retrieve IMDSv2 session token"""
    try:
        request = urllib.request.Request(
            METADATA_URL + "/api/token",
            method="PUT",
            headers={
                "X-aws-ec2-metadata-token-ttl-seconds": "21600"
            }
        )

        return urllib.request.urlopen(request, timeout=2).read().decode()

    except Exception:
        return None


TOKEN = get_token()


def get_metadata(path):
    """Retrieve EC2 metadata using IMDSv2"""

    if TOKEN is None:
        return "Unavailable"

    try:

        request = urllib.request.Request(
            METADATA_URL + "/meta-data/" + path,
            headers={
                "X-aws-ec2-metadata-token": TOKEN
            }
        )

        return urllib.request.urlopen(request, timeout=2).read().decode()

    except Exception:
        return "Unavailable"

###############################################################################
# Instance Metadata
###############################################################################

HOSTNAME = socket.gethostname()

INSTANCE_ID = get_metadata("instance-id")
AMI_ID = get_metadata("ami-id")
AZ = get_metadata("placement/availability-zone")
PUBLIC_IP = get_metadata("public-ipv4")

###############################################################################
# Startup Banner
###############################################################################

logging.info("==============================================================")
logging.info("KVS Infrastructure Automation Worker Started")
logging.info("Hostname            : %s", HOSTNAME)
logging.info("Instance ID         : %s", INSTANCE_ID)
logging.info("AMI ID              : %s", AMI_ID)
logging.info("Availability Zone   : %s", AZ)
logging.info("Public IP           : %s", PUBLIC_IP)
logging.info("Worker PID          : %s", os.getpid())
logging.info("Worker Status       : READY")
logging.info("==============================================================")

###############################################################################
# Main Worker Loop
###############################################################################

heartbeat = 1

while True:

    logging.info(
        "Heartbeat #%d | Instance=%s | Worker Active | Waiting for Jobs",
        heartbeat,
        INSTANCE_ID
    )

    heartbeat += 1

    time.sleep(10)