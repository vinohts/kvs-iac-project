/******************************************************************************
 KVS Infrastructure Automation Portal
 Author  : Vinoth Kumar
 Version : 2.0
******************************************************************************/

const API_URL = "https://yiqa8qmiua.execute-api.ap-southeast-1.amazonaws.com/prod/jobs";

async function submitJob() {

    const job = document.getElementById("job").value;
    const packageName = document.getElementById("package").value.trim();
    const requestedBy = document.querySelector("input[value='Vinoth Kumar']").value;

    if (job === "InstallPackage" && packageName === "") {

        alert("Please enter a package name.");
        return;

    }

    let payload = {
        job: job
    };

    if (job === "InstallPackage") {
        payload.package = packageName;
    }

    try {

        const response = await fetch(API_URL, {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify(payload)

        });

        const result = await response.json();

        let body = result.body;

        if (typeof body === "string") {
            body = JSON.parse(body);
        }

        alert(
            "Job Submitted Successfully!\n\n" +
            "Job ID : " + body.JobId +
            "\nJob : " + job +
            "\nStatus : QUEUED"
        );

        addJob(body.JobId, job, requestedBy);

        clearForm();

    }
    catch (error) {

        console.error(error);

        alert("Unable to submit the job.");

    }

}

function addJob(jobId, job, requestedBy) {

    const table = document.querySelector("table");

    const row = table.insertRow(-1);

    row.innerHTML = `
        <td>${jobId}</td>
        <td>${job}</td>
        <td class="queued">Queued</td>
        <td>${requestedBy}</td>
    `;

}

function clearForm() {

    document.getElementById("package").value = "";

}

document.getElementById("job").addEventListener("change", function () {

    const packageField = document.getElementById("package");

    if (this.value === "InstallPackage") {

        packageField.disabled = false;
        packageField.placeholder = "Enter Package Name";

    } else {

        packageField.value = "";
        packageField.disabled = true;
        packageField.placeholder = "Not Required";

    }

});

window.onload = function () {

    document.getElementById("package").disabled = true;

    console.log("========================================");
    console.log("KVS Infrastructure Automation Portal");
    console.log("Version : 2.0");
    console.log("Connected to AWS API Gateway");
    console.log("========================================");

};