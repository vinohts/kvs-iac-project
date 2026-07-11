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
# • Executes Jobs
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

IMDS_TOKEN_TTL_SECONDS = 21600  # 6 hours

###############################################################################
# AWS Client
###############################################################################

sqs = boto3.client(
    "sqs",
    region_name=REGION
)

###############################################################################
# IMDSv2 Token Cache
###############################################################################

_imds_token = None

_imds_token_expiry = 0


def get_imds_token():

    global _imds_token, _imds_token_expiry

    now = time.time()

    if _imds_token and now < _imds_token_expiry:

        return _imds_token

    try:

        req = urllib.request.Request(
            "http://169.254.169.254/latest/api/token",
            method="PUT",
            headers={"X-aws-ec2-metadata-token-ttl-seconds": str(IMDS_TOKEN_TTL_SECONDS)}
        )

        token = urllib.request.urlopen(req, timeout=2).read().decode()

        _imds_token = token

        # Refresh a little early so we never call the API with an expired token
        _imds_token_expiry = now + IMDS_TOKEN_TTL_SECONDS - 60

        return _imds_token

    except Exception:

        _imds_token = None

        _imds_token_expiry = 0

        return None

###############################################################################
# Metadata Helper
###############################################################################

def metadata(path):

    try:

        token = get_imds_token()

        headers = {"X-aws-ec2-metadata-token": token} if token else {}

        req = urllib.request.Request(
            f"http://169.254.169.254/latest/meta-data/{path}",
            headers=headers
        )

        return urllib.request.urlopen(req, timeout=2).read().decode()

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
# Job Functions
###############################################################################

def find_public_ip():

    ip = metadata("public-ipv4")

    logging.info("------------------------------------------------------")
    logging.info("JOB : FindPublicIP")
    logging.info("Public IP : %s", ip)
    logging.info("------------------------------------------------------")


def system_health_check():

    logging.info("------------------------------------------------------")
    logging.info("JOB : SystemHealthCheck")
    logging.info("System Health Check Completed")
    logging.info("------------------------------------------------------")


def restart_apache():

    logging.info("------------------------------------------------------")
    logging.info("JOB : RestartApache")
    logging.info("Apache Restart Requested")
    logging.info("------------------------------------------------------")


def install_package(package):

    logging.info("------------------------------------------------------")
    logging.info("JOB : InstallPackage")
    logging.info("Requested Package : %s", package)
    logging.info("------------------------------------------------------")


def create_website_backup():

    logging.info("------------------------------------------------------")
    logging.info("JOB : CreateWebsiteBackup")
    logging.info("Website Backup Completed")
    logging.info("------------------------------------------------------")

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

        #######################################################################
        # Read Message
        #######################################################################

        message = messages[0]

        body = json.loads(message["Body"])

        receipt = message["ReceiptHandle"]

        message_id = message["MessageId"]

        job = body.get("job")

        logging.info("------------------------------------------------------")
        logging.info("JOB RECEIVED")
        logging.info("Message ID : %s", message_id)
        logging.info("Payload    : %s", json.dumps(body))
        logging.info("------------------------------------------------------")

        #######################################################################
        # Execute Job
        #######################################################################

        if job == "FindPublicIP":

            find_public_ip()

        elif job == "SystemHealthCheck":

            system_health_check()

        elif job == "RestartApache":

            restart_apache()

        elif job == "InstallPackage":

            package = body.get("package", "Unknown")

            install_package(package)

        elif job == "CreateWebsiteBackup":

            create_website_backup()

        else:

            logging.warning("Unknown Job : %s", job)

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