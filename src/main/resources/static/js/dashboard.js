"use strict";

document.addEventListener("DOMContentLoaded", function () {

    const canvas = document.getElementById("attendanceChart");

    if (!canvas || typeof Chart === "undefined") {
        return;
    }

    const data = window.dashboardAttendance || {
        present: 0,
        absent: 0,
        late: 0
    };

    const present = Number(data.present) || 0;
    const absent = Number(data.absent) || 0;
    const late = Number(data.late) || 0;

    new Chart(canvas, {
        type: "doughnut",

        data: {
            labels: ["Present", "Absent", "Late"],

            datasets: [{
                data: [present, absent, late],
                backgroundColor: [
                    "#15966b",
                    "#d84d5a",
                    "#d58a25"
                ],
                borderWidth: 0,
                hoverOffset: 7
            }]
        },

        options: {
            responsive: true,
            maintainAspectRatio: false,
            cutout: "72%",

            plugins: {
                legend: {
                    display: false
                },

                tooltip: {
                    callbacks: {
                        label: function (context) {
                            return context.label + ": " + context.raw;
                        }
                    }
                }
            }
        }
    });
});