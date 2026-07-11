#!/usr/bin/env python3

###############################################################################
# KVS Infrastructure Automation Worker
#
# Purpose : Enterprise Queue Worker
# Author  : Vinoth Kumar
#
# Phase 3
# --------
# • Runs as a systemd service
# • Polls Amazon SQS
# • Receives Jobs
# • Processes Jobs
# • Deletes Completed Jobs
# • Writes Heartbeats
###############################################################################

import os
import json
import time
import socket
import logging
import urllib.request

import boto3

###############################################################################
# Logging
###############################################################################

logging.basicConfig(
    filename="/var/log/kvs-worker.log",
    level=logging.INFO,
    format="%(asctime)s %(levelname)s %(message)s"
)

###############################################################################
# Configuration
###############################################################################

REGION = "ap-southeast-1"

QUEUE_URL = "https://sqs.ap-southeast-1.amazonaws.com/350025135544/kvs-job-queue"

###############################################################################
# AWS Client
###############################################################################

sqs = boto3.client(
    "sqs",
    region_name=REGION
)

###############################################################################
# Metadata Helper
###############################################################################

def metadata(path):

    try:

        return urllib.request.urlopen(
            f"http://169.254.169.254/latest/meta-data/{path}",
            timeout=2
        ).read().decode()

    except Exception:

        return "Unavailable"

###############################################################################
# Instance Information
###############################################################################

HOSTNAME = socket.gethostname()

INSTANCE_ID = metadata("instance-id")

AMI_ID = metadata("ami-id")

AZ = metadata("placement/availability-zone")

PUBLIC_IP = metadata("public-ipv4")

###############################################################################
# Startup Banner
###############################################################################

logging.info("===============================================================")
logging.info("KVS Queue Worker Started")
logging.info("Hostname      : %s", HOSTNAME)
logging.info("Instance ID   : %s", INSTANCE_ID)
logging.info("AMI           : %s", AMI_ID)
logging.info("AZ            : %s", AZ)
logging.info("Public IP     : %s", PUBLIC_IP)
logging.info("Worker PID    : %s", os.getpid())
logging.info("Queue         : kvs-job-queue")
logging.info("Status        : READY")
logging.info("===============================================================")

###############################################################################
# Main Loop
###############################################################################

heartbeat = 1

while True:

    try:

        response = sqs.receive_message(

            QueueUrl=QUEUE_URL,

            MaxNumberOfMessages=1,

            WaitTimeSeconds=20

        )

        messages = response.get("Messages", [])

        if not messages:

            logging.info(
                "Heartbeat #%s | Queue Empty | Worker Ready",
                heartbeat
            )

            heartbeat += 1

            continue

        message = messages[0]

        body = message["Body"]

        receipt = message["ReceiptHandle"]

        message_id = message["MessageId"]

        logging.info("------------------------------------------------------")
        logging.info("JOB RECEIVED")
        logging.info("Message ID : %s", message_id)
        logging.info("Payload    : %s", body)

        #######################################################################
        # Future Processing Logic
        #######################################################################

        logging.info("Processing Job...")

        time.sleep(3)

        logging.info("Job Completed Successfully")

        #######################################################################
        # Delete Message
        #######################################################################

        sqs.delete_message(

            QueueUrl=QUEUE_URL,

            ReceiptHandle=receipt

        )

        logging.info("Message Deleted")
        logging.info("------------------------------------------------------")

    except Exception as e:

        logging.error("Worker Exception : %s", str(e))

        time.sleep(10)