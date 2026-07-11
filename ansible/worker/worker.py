#!/usr/bin/env python3

import time
import logging
import socket

HOSTNAME = socket.gethostname()

logging.basicConfig(
    filename="/var/log/kvs-worker.log",
    level=logging.INFO,
    format="%(asctime)s %(levelname)s %(message)s"
)

logging.info("===================================================")
logging.info("KVS Infrastructure Automation Worker Started")
logging.info("Hostname : %s", HOSTNAME)
logging.info("Status   : READY")
logging.info("===================================================")

while True:

    logging.info("Heartbeat | Worker Active | Waiting for Jobs")

    time.sleep(10)