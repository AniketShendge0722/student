"use strict";

document.addEventListener("DOMContentLoaded", function () {

    // Mobile sidebar
    const menuToggle = document.getElementById("menuToggle");
    const sidebar = document.getElementById("sidebar");

    if (menuToggle && sidebar) {
        menuToggle.addEventListener("click", function () {
            sidebar.classList.toggle("open");
        });

        document.addEventListener("click", function (event) {
            const clickedInsideSidebar = sidebar.contains(event.target);
            const clickedToggle = menuToggle.contains(event.target);

            if (!clickedInsideSidebar && !clickedToggle &&
                window.innerWidth <= 760) {
                sidebar.classList.remove("open");
            }
        });
    }

    // Highlight current navigation item
    const currentPath = window.location.pathname;

    document.querySelectorAll(".nav-menu .nav-link").forEach(function (link) {
        const linkPath = new URL(link.href).pathname;

        if (linkPath === currentPath ||
            (linkPath !== "/" && currentPath.startsWith(linkPath + "/"))) {
            link.classList.add("active");
        }
    });

    // Password visibility
    const togglePassword = document.getElementById("togglePassword");
    const passwordInput = document.getElementById("password");

    if (togglePassword && passwordInput) {
        togglePassword.addEventListener("click", function () {
            const isPassword = passwordInput.type === "password";
            passwordInput.type = isPassword ? "text" : "password";
            togglePassword.textContent = isPassword ? "Hide" : "Show";
        });
    }

    // Automatically hide alerts
    document.querySelectorAll(".alert").forEach(function (alert) {
        if (alert.classList.contains("alert-success")) {
            window.setTimeout(function () {
                alert.style.transition = "opacity 0.4s ease";
                alert.style.opacity = "0";

                window.setTimeout(function () {
                    alert.remove();
                }, 450);
            }, 4500);
        }
    });

    // Prevent double form submission
    document.querySelectorAll("form").forEach(function (form) {
        form.addEventListener("submit", function () {
            const submitButton = form.querySelector("button[type='submit']");

            if (submitButton && !form.dataset.confirmed) {
                submitButton.dataset.originalText = submitButton.textContent;
                submitButton.disabled = true;
                submitButton.textContent = "Please wait...";
            }
        });
    });
});

function confirmDelete(message) {
    return window.confirm(message || "Are you sure you want to delete this record?");
}