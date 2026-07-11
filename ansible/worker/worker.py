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
# • Displays infrastructure telemetry
###############################################################################

import time
import logging
import socket
import os
import urllib.request

# ---------------------------------------------------------------------------
# Logging Configuration
# ---------------------------------------------------------------------------

logging.basicConfig(
    filename="/var/log/kvs-worker.log",
    level=logging.INFO,
    format="%(asctime)s %(levelname)s %(message)s"
)

# ---------------------------------------------------------------------------
# Helper Function
# ---------------------------------------------------------------------------

def get_metadata(path):
    try:
        return urllib.request.urlopen(
            f"http://169.254.169.254/latest/meta-data/{path}",
            timeout=2
        ).read().decode()
    except:
        return "Unavailable"

# ---------------------------------------------------------------------------
# Instance Metadata
# ---------------------------------------------------------------------------

HOSTNAME = socket.gethostname()

INSTANCE_ID = get_metadata("instance-id")
AMI_ID = get_metadata("ami-id")
AZ = get_metadata("placement/availability-zone")
PUBLIC_IP = get_metadata("public-ipv4")

# ---------------------------------------------------------------------------
# Startup Banner
# ---------------------------------------------------------------------------

logging.info("==============================================================")
logging.info("KVS Infrastructure Automation Worker Started")
logging.info("Hostname            : %s", HOSTNAME)
logging.info("Instance ID         : %s", INSTANCE_ID)
logging.info("AMI                 : %s", AMI_ID)
logging.info("Availability Zone   : %s", AZ)
logging.info("Public IP           : %s", PUBLIC_IP)
logging.info("Worker PID          : %s", os.getpid())
logging.info("Worker Status       : READY")
logging.info("==============================================================")

# ---------------------------------------------------------------------------
# Main Loop
# ---------------------------------------------------------------------------

heartbeat = 1

while True:

    logging.info(
        "Heartbeat #%s | Worker Active | Waiting for Jobs",
        heartbeat
    )

    heartbeat += 1

    time.sleep(10)