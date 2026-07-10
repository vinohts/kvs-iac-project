// Set hostname immediately
document.getElementById("hostname").innerHTML = window.location.hostname;

// Set placeholder statuses
document.getElementById("instanceid").innerHTML = "Fetching...";
document.getElementById("privateip").innerHTML = "Fetching...";
document.getElementById("az").innerHTML = "Fetching...";

// Localized timestamp
document.getElementById("time").innerHTML = new Date().toLocaleString();