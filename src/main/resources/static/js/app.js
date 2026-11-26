"use strict";

(function () {
    const TOAST_ICONS = {
        success: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="12" cy="12" r="10"></circle><path d="m9 12 2 2 4-4"></path></svg>',
        error: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="12" cy="12" r="10"></circle><path d="m15 9-6 6"></path><path d="m9 9 6 6"></path></svg>',
        warning: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m21.73 18-8-14a2 2 0 0 0-3.48 0l-8 14A2 2 0 0 0 4 21h16a2 2 0 0 0 1.73-3Z"></path><path d="M12 9v4"></path><path d="M12 17h.01"></path></svg>',
        info: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><circle cx="12" cy="12" r="10"></circle><path d="M12 16v-4"></path><path d="M12 8h.01"></path></svg>'
    };

    const DEFAULT_TITLES = {
        success: "Success",
        error: "Validation Error",
        warning: "Warning",
        info: "Notice"
    };

    function normalizeType(type) {
        if (type === "danger") {
            return "error";
        }
        if (type === "success" || type === "error" || type === "warning" || type === "info") {
            return type;
        }
        return "info";
    }

    function getToastContainer() {
        let container = document.getElementById("st-toast-container");
        if (!container) {
            container = document.createElement("div");
            container.id = "st-toast-container";
            container.className = "st-toast-container";
            container.setAttribute("aria-live", "polite");
            container.setAttribute("aria-atomic", "false");
            document.body.appendChild(container);
        }
        return container;
    }

    function dismissToast(toast) {
        if (!toast || toast.dataset.closing === "true") {
            return;
        }
        toast.dataset.closing = "true";
        if (toast._dismissTimer) {
            window.clearTimeout(toast._dismissTimer);
        }
        toast.classList.add("st-toast-leaving");
        window.setTimeout(function () {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        }, 240);
    }

    function scheduleDismiss(toast, duration) {
        if (toast._dismissTimer) {
            window.clearTimeout(toast._dismissTimer);
        }
        if (duration > 0) {
            toast._dismissTimer = window.setTimeout(function () {
                dismissToast(toast);
            }, duration);
        }
    }

    function showNotification(message, type, options) {
        const cleanMessage = (message || "").trim();
        if (!cleanMessage) {
            return null;
        }

        const normalizedType = normalizeType(type);
        const opts = options || {};
        const duration = typeof opts.duration === "number"
            ? opts.duration
            : (normalizedType === "error" || normalizedType === "warning" ? 6500 : 4800);
        const title = opts.title || DEFAULT_TITLES[normalizedType];
        const key = normalizedType + "::" + cleanMessage.toLowerCase();

        const container = getToastContainer();
        const existing = Array.from(container.querySelectorAll(".st-toast")).find(function (node) {
            return node.dataset.notifyKey === key && node.dataset.closing !== "true";
        });

        if (existing) {
            existing.classList.remove("st-toast-pulse");
            void existing.offsetWidth;
            existing.classList.add("st-toast-pulse");
            scheduleDismiss(existing, duration);
            return existing;
        }

        const toast = document.createElement("div");
        toast.className = "st-toast st-toast-" + normalizedType;
        toast.dataset.notifyKey = key;
        toast.setAttribute("role", normalizedType === "error" || normalizedType === "warning" ? "alert" : "status");

        const iconWrap = document.createElement("div");
        iconWrap.className = "st-toast-icon";
        iconWrap.innerHTML = TOAST_ICONS[normalizedType];

        const body = document.createElement("div");
        body.className = "st-toast-body";

        const titleEl = document.createElement("strong");
        titleEl.className = "st-toast-title";
        titleEl.textContent = title;

        const msgEl = document.createElement("p");
        msgEl.className = "st-toast-message";
        msgEl.textContent = cleanMessage;

        body.appendChild(titleEl);
        body.appendChild(msgEl);

        const closeBtn = document.createElement("button");
        closeBtn.type = "button";
        closeBtn.className = "st-toast-close";
        closeBtn.setAttribute("aria-label", "Close notification");
        closeBtn.innerHTML = "&times;";
        closeBtn.addEventListener("click", function () {
            dismissToast(toast);
        });

        toast.appendChild(iconWrap);
        toast.appendChild(body);
        toast.appendChild(closeBtn);

        toast.addEventListener("mouseenter", function () {
            if (toast._dismissTimer) {
                window.clearTimeout(toast._dismissTimer);
            }
        });

        toast.addEventListener("mouseleave", function () {
            scheduleDismiss(toast, duration);
        });

        container.appendChild(toast);
        window.requestAnimationFrame(function () {
            toast.classList.add("st-toast-visible");
        });

        scheduleDismiss(toast, duration);
        return toast;
    }

    function clearNotifications(type) {
        const container = document.getElementById("st-toast-container");
        if (!container) {
            return;
        }
        const normalized = type ? normalizeType(type) : null;
        container.querySelectorAll(".st-toast").forEach(function (toast) {
            if (!normalized || toast.classList.contains("st-toast-" + normalized)) {
                dismissToast(toast);
            }
        });
    }

    window.SchoolTrackNotify = {
        show: showNotification,
        success: function (msg, opts) { return showNotification(msg, "success", opts); },
        error: function (msg, opts) { return showNotification(msg, "error", Object.assign({ title: "Error" }, opts)); },
        warning: function (msg, opts) { return showNotification(msg, "warning", opts); },
        info: function (msg, opts) { return showNotification(msg, "info", opts); },
        clear: clearNotifications
    };

    function getFieldLabel(field) {
        if (field.dataset.fieldLabel) {
            return field.dataset.fieldLabel;
        }
        const group = field.closest(".form-group");
        let label = null;
        if (field.id) {
            label = document.querySelector('label[for="' + CSS.escape(field.id) + '"]');
        }
        if (!label && group) {
            label = group.querySelector("label");
        }
        if (label) {
            return label.textContent.replace(/\*/g, "").trim();
        }
        return field.getAttribute("aria-label") || field.name || "This field";
    }

    function getErrorElement(field) {
        const group = field.closest(".form-group");
        if (!group) {
            return null;
        }
        let errorEl = group.querySelector(".field-error");
        if (!errorEl) {
            errorEl = document.createElement("small");
            errorEl.className = "field-error";
            errorEl.setAttribute("aria-live", "polite");
            group.appendChild(errorEl);
        }
        return errorEl;
    }

    function setFieldError(field, message) {
        field.classList.add("is-invalid");
        field.classList.remove("is-valid");
        field.setAttribute("aria-invalid", "true");
        const errorEl = getErrorElement(field);
        if (errorEl) {
            errorEl.textContent = message;
        }
    }

    function clearFieldError(field) {
        field.classList.remove("is-invalid");
        field.removeAttribute("aria-invalid");
        const errorEl = field.closest(".form-group")
            ? field.closest(".form-group").querySelector(".field-error")
            : null;
        if (errorEl) {
            errorEl.textContent = "";
        }
    }

    function parseLocalIsoDate(value) {
        if (!/^\d{4}-\d{2}-\d{2}$/.test(value)) {
            return null;
        }
        const parts = value.split("-").map(Number);
        const date = new Date(parts[0], parts[1] - 1, parts[2]);
        if (date.getFullYear() !== parts[0] ||
            date.getMonth() !== parts[1] - 1 ||
            date.getDate() !== parts[2]) {
            return null;
        }
        date.setHours(0, 0, 0, 0);
        return date;
    }

    function getTodayMidnight() {
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        return today;
    }

    function validateField(field) {
        if (!field || field.disabled || field.type === "hidden" || field.type === "submit" || field.type === "button") {
            return "";
        }

        const tag = field.tagName.toLowerCase();
        const type = (field.getAttribute("type") || "").toLowerCase();
        const rawValue = field.value == null ? "" : String(field.value);
        const value = type === "password" ? rawValue : rawValue.trim();
        const label = getFieldLabel(field);

        if (field.hasAttribute("required")) {
            if (type === "checkbox" && !field.checked) {
                return field.dataset.msgRequired || ("Please check " + label.toLowerCase() + ".");
            }
            if (value === "") {
                if (field.dataset.msgRequired) {
                    return field.dataset.msgRequired;
                }
                if (tag === "select") {
                    return "Please select " + label.toLowerCase() + ".";
                }
                return "Please enter " + label.toLowerCase() + ".";
            }
        }

        if (value === "") {
            return "";
        }

        if (type === "email") {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;
            if (!emailRegex.test(value)) {
                return field.dataset.msgEmail || "Please enter a valid email address.";
            }
        }

        if (field.dataset.validateType === "phone" || type === "tel") {
            if (field.dataset.phoneMode === "india-mobile") {
                if (!/^[6-9][0-9]{9}$/.test(value)) {
                    return field.dataset.msgPattern || "Phone number must be a valid 10-digit mobile number starting with 6-9.";
                }
            } else if (field.dataset.phoneMode === "contact") {
                if (!/^[0-9+() -]{6,15}$/.test(value)) {
                    return field.dataset.msgPattern || "Phone number must contain a valid number of digits (6-15 characters).";
                }
            }
        }

        if (field.dataset.validateType === "bus-number") {
            const normalizedBus = value.replace(/\s+/g, " ").toUpperCase();
            if (!/^[A-Z]{2}\s[0-9]{1,2}\s[A-Z]{1,3}\s[0-9]{4}$/.test(normalizedBus)) {
                return field.dataset.msgPattern || "Enter bus number like MH 10 CR 3451.";
            }
        }

        const minLen = field.getAttribute("minlength");
        if (minLen && value.length < parseInt(minLen, 10)) {
            return field.dataset.msgMinlength || (label + " must be at least " + minLen + " characters.");
        }

        const maxLen = field.getAttribute("maxlength");
        if (maxLen && value.length > parseInt(maxLen, 10)) {
            return field.dataset.msgMaxlength || (label + " cannot exceed " + maxLen + " characters.");
        }

        if (type === "number") {
            const num = Number(value);
            if (!Number.isFinite(num)) {
                return "Please enter a valid number for " + label.toLowerCase() + ".";
            }
            const min = field.getAttribute("min");
            if (min !== null && min !== "" && num < Number(min)) {
                return field.dataset.msgMin || (label + " must be at least " + min + ".");
            }
            const max = field.getAttribute("max");
            if (max !== null && max !== "" && num > Number(max)) {
                return field.dataset.msgMax || (label + " cannot exceed " + max + ".");
            }
        }

        if (type === "date") {
            const parsedDate = parseLocalIsoDate(value);
            if (!parsedDate) {
                return "Please select a valid date for " + label.toLowerCase() + ".";
            }
            const today = getTodayMidnight();
            if (field.dataset.dateRule === "past" && parsedDate >= today) {
                return field.dataset.msgDate || (label + " must be in the past.");
            }
            if (field.dataset.dateRule === "past-or-present" && parsedDate > today) {
                return field.dataset.msgDate || (label + " cannot be in the future.");
            }
        }

        const pattern = field.getAttribute("pattern");
        if (pattern && !field.dataset.validateType) {
            const regex = new RegExp("^(?:" + pattern + ")$");
            if (!regex.test(value)) {
                return field.dataset.msgPattern || field.getAttribute("title") || ("Please enter a valid " + label.toLowerCase() + ".");
            }
        }

        if (field.dataset.match) {
            const target = document.querySelector(field.dataset.match);
            if (target && rawValue !== String(target.value || "")) {
                return field.dataset.msgMatch || "Passwords do not match.";
            }
        }

        return "";
    }

    function validateCustomFormRules(form) {
        const errors = [];

        if (form.dataset.validateBulk === "attendance") {
            const rows = Array.from(form.querySelectorAll("tbody tr")).filter(function (row) {
                return row.querySelector(".student-checkbox");
            });
            const selectedRows = rows.filter(function (row) {
                const cb = row.querySelector(".student-checkbox");
                return cb && cb.checked;
            });

            if (rows.length > 0 && selectedRows.length === 0) {
                errors.push({ field: null, message: "Please select at least one student to save attendance." });
            } else {
                selectedRows.forEach(function (row) {
                    const select = row.querySelector(".status-select");
                    if (select && !select.value) {
                        const studentNameCell = row.children[2];
                        const studentName = studentNameCell ? studentNameCell.textContent.trim() : "student";
                        const msg = "Please select attendance status for " + studentName + ".";
                        setFieldError(select, msg);
                        errors.push({ field: select, message: msg });
                    }
                });
            }
        }

        if (form.dataset.validateAssignments === "true") {
            const seen = new Set();
            const assignmentRows = form.querySelectorAll("#assignmentRows .assignment-row");
            assignmentRows.forEach(function (row) {
                const tenantSelect = row.querySelector('select[name*="tenantId"]');
                const roleSelect = row.querySelector('select[name*="roleId"]');
                if (tenantSelect && roleSelect && tenantSelect.value && roleSelect.value) {
                    const key = tenantSelect.value + ":" + roleSelect.value;
                    if (seen.has(key)) {
                        const msg = "Duplicate school and role assignment selected.";
                        setFieldError(roleSelect, msg);
                        errors.push({ field: roleSelect, message: msg });
                    }
                    seen.add(key);
                }
            });
        }

        return errors;
    }

    function validateForm(form) {
        const errors = [];
        const fields = Array.from(form.querySelectorAll("input, select, textarea"));

        fields.forEach(function (field) {
            const msg = validateField(field);
            if (msg) {
                setFieldError(field, msg);
                errors.push({ field: field, message: msg });
            } else {
                clearFieldError(field);
            }
        });

        const customErrors = validateCustomFormRules(form);
        errors.push.apply(errors, customErrors);

        return errors;
    }

    window.SchoolTrackValidate = {
        validateField: validateField,
        validateForm: validateForm
    };

    function resetFormSubmitState(form) {
        form.dataset.submitting = "false";
        form.querySelectorAll("button[type='submit'], input[type='submit']").forEach(function (btn) {
            btn.disabled = false;
            if (btn.dataset.originalText) {
                btn.textContent = btn.dataset.originalText;
            }
        });
    }

    function lockFormSubmit(form, submitter) {
        if ((form.method || "get").toLowerCase() === "get") {
            return;
        }
        form.dataset.submitting = "true";
        const targetBtn = submitter || form.querySelector("button[type='submit']");
        if (targetBtn) {
            if (!targetBtn.dataset.originalText) {
                targetBtn.dataset.originalText = targetBtn.textContent.trim();
            }
            window.setTimeout(function () {
                if (form.dataset.submitting === "true") {
                    targetBtn.disabled = true;
                    targetBtn.textContent = "Please wait...";
                }
            }, 0);
        }
    }

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

        // Password visibility toggle (supports #togglePassword + #password and [data-password-toggle])
        const togglePassword = document.getElementById("togglePassword");
        const passwordInput = document.getElementById("password");
        if (togglePassword && passwordInput) {
            togglePassword.addEventListener("click", function () {
                const isPassword = passwordInput.type === "password";
                passwordInput.type = isPassword ? "text" : "password";
                togglePassword.textContent = isPassword ? "Hide" : "Show";
            });
        }

        document.querySelectorAll("[data-password-toggle]").forEach(function (btn) {
            btn.addEventListener("click", function () {
                const targetId = btn.getAttribute("data-password-toggle");
                const targetInput = targetId ? document.getElementById(targetId) : null;
                if (!targetInput) {
                    return;
                }
                const isPassword = targetInput.type === "password";
                targetInput.type = isPassword ? "text" : "password";
                btn.textContent = isPassword ? "Hide" : "Show";
            });
        });

        // Enhance server-rendered alerts with manual close + unified deduplicated toast popups
        document.querySelectorAll(".alert").forEach(function (alert) {
            const text = (alert.textContent || "").trim();
            if (!text) {
                return;
            }

            if (!alert.querySelector(".alert-close")) {
                const closeBtn = document.createElement("button");
                closeBtn.type = "button";
                closeBtn.className = "alert-close";
                closeBtn.setAttribute("aria-label", "Close message");
                closeBtn.innerHTML = "&times;";
                closeBtn.addEventListener("click", function () {
                    alert.remove();
                });
                alert.appendChild(closeBtn);
            }

            let alertType = "info";
            let alertTitle = "Notice";
            if (alert.classList.contains("alert-success")) {
                alertType = "success";
                alertTitle = "Success";
                window.setTimeout(function () {
                    alert.style.transition = "opacity 0.4s ease";
                    alert.style.opacity = "0";
                    window.setTimeout(function () {
                        if (alert.parentNode) {
                            alert.remove();
                        }
                    }, 450);
                }, 4500);
            } else if (alert.classList.contains("alert-error") || alert.classList.contains("alert-danger")) {
                alertType = "error";
                alertTitle = "Error";
            } else if (alert.classList.contains("alert-warning")) {
                alertType = "warning";
                alertTitle = "Warning";
            }

            if (alert.dataset.toast !== "false") {
                showNotification(text, alertType, { title: alertTitle });
            }
        });

        // Surface server-side field errors that already exist on page load
        document.querySelectorAll("form").forEach(function (form) {
            let hasServerFieldError = false;
            form.querySelectorAll(".field-error, .text-danger").forEach(function (errEl) {
                if (errEl.tagName.toLowerCase() === "small" && errEl.textContent.trim() !== "") {
                    hasServerFieldError = true;
                    const group = errEl.closest(".form-group");
                    if (group) {
                        const input = group.querySelector("input, select, textarea");
                        if (input) {
                            input.classList.add("is-invalid");
                        }
                    }
                }
            });
            if (hasServerFieldError && !form.querySelector(".alert-error, .alert-danger")) {
                showNotification("Please correct the highlighted fields.", "error", { title: "Validation Error" });
            }
        });

        // Setup form validation and safe double-submit protection
        document.querySelectorAll("form").forEach(function (form) {
            const usesCustomValidation = form.dataset.validate === "true";

            if (usesCustomValidation) {
                form.setAttribute("novalidate", "novalidate");

                form.addEventListener("input", function (event) {
                    const target = event.target;
                    if (target && target.matches("input, select, textarea")) {
                        if (target.classList.contains("is-invalid") || target.dataset.match) {
                            const msg = validateField(target);
                            if (msg) {
                                setFieldError(target, msg);
                            } else {
                                clearFieldError(target);
                            }
                        }
                        if (target.id) {
                            form.querySelectorAll('[data-match="#' + CSS.escape(target.id) + '"]').forEach(function (dep) {
                                if (dep.value !== "" || dep.classList.contains("is-invalid")) {
                                    const depMsg = validateField(dep);
                                    if (depMsg) {
                                        setFieldError(dep, depMsg);
                                    } else {
                                        clearFieldError(dep);
                                    }
                                }
                            });
                        }
                    }
                });

                form.addEventListener("change", function (event) {
                    const target = event.target;
                    if (target && target.matches("input, select, textarea")) {
                        const msg = validateField(target);
                        if (msg) {
                            setFieldError(target, msg);
                        } else {
                            clearFieldError(target);
                        }
                    }
                });
            }

            form.addEventListener("invalid", function () {
                resetFormSubmitState(form);
            }, true);

            form.addEventListener("submit", function (event) {
                if (event.defaultPrevented) {
                    resetFormSubmitState(form);
                    return;
                }

                if (form.dataset.submitting === "true") {
                    event.preventDefault();
                    return;
                }

                if (usesCustomValidation) {
                    const errors = validateForm(form);
                    if (errors.length > 0) {
                        event.preventDefault();
                        resetFormSubmitState(form);
                        showNotification(errors[0].message, "error", { title: "Validation Error" });
                        if (errors[0].field && typeof errors[0].field.focus === "function") {
                            errors[0].field.focus();
                        }
                        return;
                    }
                } else if (typeof form.checkValidity === "function" && !form.checkValidity()) {
                    event.preventDefault();
                    resetFormSubmitState(form);
                    if (typeof form.reportValidity === "function") {
                        form.reportValidity();
                    }
                    return;
                }

                if (form.dataset.confirmMessage && !window.confirm(form.dataset.confirmMessage)) {
                    event.preventDefault();
                    resetFormSubmitState(form);
                    return;
                }

                lockFormSubmit(form, event.submitter);
            });
        });
    });

    window.addEventListener("pageshow", function () {
        document.querySelectorAll("form").forEach(resetFormSubmitState);
    });
})();

function confirmDelete(message) {
    return window.confirm(message || "Are you sure you want to delete this record?");
}
