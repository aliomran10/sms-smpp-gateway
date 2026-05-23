<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <title>Admin — Edit Customer</title>
        <link rel="preconnect" href="https://fonts.googleapis.com"/>
        <link href="https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=DM+Mono:wght@400;500&display=swap" rel="stylesheet"/>
        <style>
            :root {
                --bg:      #0d0f14;
                --surface: #161920;
                --border: #252832;
                --accent:  #5b6af0;
                --accent2: #3ecf8e;
                --danger: #f04e5e;
                --warn: #f0a04e;
                --text:    #e8eaf0;
                --muted:   #6b7280;
                --radius:  10px;
                --font: 'Syne', sans-serif;
                --mono: 'DM Mono', monospace;
            }
            *, *::before, *::after {
                box-sizing: border-box;
                margin: 0;
                padding: 0;
            }
            body {
                font-family: var(--font);
                background: var(--bg);
                color: var(--text);
                min-height: 100vh;
                display: flex;
            }

            .sidebar {
                width: 230px;
                min-height: 100vh;
                background: var(--surface);
                border-right: 1px solid var(--border);
                display: flex;
                flex-direction: column;
                padding: 28px 0;
                flex-shrink: 0;
                position: sticky;
                top: 0;
                height: 100vh;
            }
            .sidebar-brand {
                padding: 0 24px 28px;
                border-bottom: 1px solid var(--border);
                margin-bottom: 16px;
            }
            .sidebar-brand .logo-mark {
                width: 36px;
                height: 36px;
                background: var(--accent);
                border-radius: 8px;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 18px;
                margin-bottom: 10px;
            }
            .sidebar-brand h2 {
                font-size: 15px;
                font-weight: 700;
                letter-spacing: .04em;
            }
            .sidebar-brand p  {
                font-size: 11px;
                color: var(--muted);
                margin-top: 2px;
            }
            .nav-item {
                display: flex;
                align-items: center;
                gap: 10px;
                padding: 11px 24px;
                color: var(--muted);
                text-decoration: none;
                font-size: 13.5px;
                font-weight: 600;
                transition: all .15s;
                border-left: 3px solid transparent;
            }
            .nav-item:hover {
                color: var(--text);
                background: rgba(255,255,255,.04);
            }
            .nav-item.active {
                color: var(--accent);
                border-left-color: var(--accent);
                background: rgba(91,106,240,.08);
            }
            .nav-item .icon  {
                font-size: 16px;
                width: 20px;
                text-align: center;
            }
            .sidebar-footer  {
                margin-top: auto;
                padding: 16px 24px;
                border-top: 1px solid var(--border);
            }
            .logout-btn {
                display: flex;
                align-items: center;
                gap: 8px;
                color: var(--danger);
                text-decoration: none;
                font-size: 13px;
                font-weight: 600;
            }
            .logout-btn:hover {
                opacity: .75;
            }

            .main {
                flex: 1;
                padding: 36px 40px;
            }
            .breadcrumb {
                display: flex;
                align-items: center;
                gap: 8px;
                font-size: 12px;
                color: var(--muted);
                margin-bottom: 12px;
            }
            .breadcrumb a {
                color: var(--accent);
                text-decoration: none;
            }
            .breadcrumb a:hover {
                text-decoration: underline;
            }
            .page-header {
                margin-bottom: 32px;
            }
            .page-header h1 {
                font-size: 28px;
                font-weight: 800;
            }
            .page-header p  {
                color: var(--muted);
                font-size: 13px;
                margin-top: 4px;
            }

            .form-card {
                background: var(--surface);
                border: 1px solid var(--border);
                border-radius: 14px;
                padding: 36px;
                max-width: 780px;
            }
            .section-title {
                font-size: 11px;
                font-weight: 700;
                letter-spacing: .1em;
                text-transform: uppercase;
                color: var(--muted);
                margin: 28px 0 16px;
                padding-bottom: 8px;
                border-bottom: 1px solid var(--border);
            }
            .section-title:first-child {
                margin-top: 0;
            }

            .form-grid {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 16px;
            }
            .form-group {
                display: flex;
                flex-direction: column;
                gap: 6px;
            }
            .form-group.span2 {
                grid-column: span 2;
            }

            label {
                font-size: 12px;
                font-weight: 700;
                color: var(--muted);
                letter-spacing: .04em;
            }
            label .req {
                color: var(--danger);
                margin-left: 2px;
            }

            input, select, textarea {
                background: var(--bg);
                border: 1px solid var(--border);
                border-radius: 8px;
                padding: 10px 14px;
                color: var(--text);
                font-family: var(--font);
                font-size: 13px;
                outline: none;
                transition: border-color .15s;
                width: 100%;
            }
            input:focus {
                border-color: var(--accent);
            }
            input::placeholder {
                color: var(--muted);
            }
            input[type="date"]::-webkit-calendar-picker-indicator {
                filter: invert(1) opacity(.4);
            }

            .hint {
                font-size: 11px;
                color: var(--muted);
            }

            .error-msg {
                background: rgba(240,78,94,.1);
                border: 1px solid rgba(240,78,94,.3);
                color: var(--danger);
                border-radius: 8px;
                padding: 12px 16px;
                font-size: 13px;
                font-weight: 600;
                margin-bottom: 20px;
            }

            .warn-box {
                background: rgba(240,160,78,.08);
                border: 1px solid rgba(240,160,78,.25);
                border-radius: 8px;
                padding: 10px 14px;
                font-size: 12px;
                color: var(--warn);
                margin-bottom: 0;
            }

            .form-actions {
                display: flex;
                gap: 12px;
                justify-content: flex-end;
                margin-top: 32px;
                padding-top: 24px;
                border-top: 1px solid var(--border);
            }
            .btn {
                padding: 10px 22px;
                border-radius: var(--radius);
                border: none;
                font-family: var(--font);
                font-size: 13px;
                font-weight: 700;
                cursor: pointer;
                text-decoration: none;
                display: inline-flex;
                align-items: center;
                gap: 6px;
                transition: opacity .15s, transform .1s;
            }
            .btn:hover {
                opacity: .88;
                transform: translateY(-1px);
            }
            .btn-primary {
                background: var(--accent);
                color: #fff;
            }
            .btn-ghost   {
                background: transparent;
                border: 1px solid var(--border);
                color: var(--text);
            }

            .customer-id-badge {
                display: inline-flex;
                align-items: center;
                gap: 6px;
                background: rgba(91,106,240,.12);
                color: var(--accent);
                border: 1px solid rgba(91,106,240,.25);
                padding: 4px 10px;
                border-radius: 20px;
                font-family: var(--mono);
                font-size: 12px;
                margin-bottom: 20px;
            }

            .twilio-section {
                background: rgba(91,106,240,.06);
                border: 1px solid rgba(91,106,240,.2);
                border-radius: 10px;
                padding: 20px;
                margin-top: 4px;
            }
        </style>
    </head>
    <body>

        <aside class="sidebar">
            <div class="sidebar-brand">
                <div class="logo-mark">📡</div>
                <h2>TwilioAdmin</h2>
                <p>Administrator Panel</p>
            </div>
            <a href="${pageContext.request.contextPath}/admin/customers"    class="nav-item active"><span class="icon">👥</span> Customers</a>
            <a href="${pageContext.request.contextPath}/admin/add-customer" class="nav-item"><span class="icon">➕</span> Add Customer</a>
            <a href="${pageContext.request.contextPath}/admin/stats"        class="nav-item"><span class="icon">📊</span> Statistics</a>
            <div class="sidebar-footer">
                <a href="${pageContext.request.contextPath}/logout" class="logout-btn"><span>⬅</span> Logout</a>
            </div>
        </aside>

        <main class="main">
            <div class="breadcrumb">
                <a href="${pageContext.request.contextPath}/admin/customers">Customers</a>
                <span>›</span>
                <span>Edit — ${customer.fullName}</span>
            </div>

            <div class="page-header">
                <h1>Edit Customer</h1>
                <p>Update the customer's profile and Twilio credentials.</p>
            </div>

            <div class="customer-id-badge">ID #${customer.userId}</div>

            <div class="form-card">
                <c:if test="${not empty error}">
                    <div class="error-msg">⚠ &nbsp;${error}</div>
                </c:if>

                <form method="post" action="${pageContext.request.contextPath}/admin/edit-customer">
                    <input type="hidden" name="userId" value="${customer.userId}"/>

                    <!-- Personal Info -->
                    <div class="section-title">Personal Information</div>
                    <div class="form-grid">
                        <div class="form-group">
                            <label>Full Name <span class="req">*</span></label>
                            <input type="text" name="fullName" value="${fn:escapeXml(customer.fullName)}" required/>
                        </div>
                        <div class="form-group">
                            <label>Birthday</label>
                            <input type="date" name="birthday" value="${customer.birthday}"/>
                        </div>
                        <div class="form-group">
                            <label>Job / Title</label>
                            <input type="text" name="job" value="${fn:escapeXml(customer.job)}"/>
                        </div>
                        <div class="form-group">
                            <label>MSISDN (Phone Number)</label>
                            <input type="text" name="msisdn" value="${fn:escapeXml(customer.msisdn)}"/>
                            <span class="hint">Changing the MSISDN re-links SMS message history.</span>
                        </div>
                        <div class="form-group span2">
                            <label>Physical Address</label>
                            <input type="text" name="physicalAddress" value="${fn:escapeXml(customer.physicalAddress)}"/>
                        </div>
                    </div>

                    <!-- Account Credentials -->
                    <div class="section-title">Account Credentials</div>
                    <div class="form-grid">
                        <div class="form-group">
                            <label>Email <span class="req">*</span></label>
                            <input type="email" name="email" value="${fn:escapeXml(customer.email)}" required/>
                        </div>
                        <div class="form-group">
                            <label>New Password</label>
                            <input type="password" name="password" placeholder="Leave blank to keep current password"/>
                            <span class="hint">Password changes are not handled here — use the profile page or a dedicated reset flow.</span>
                        </div>
                    </div>

                    <!-- Twilio Credentials -->
                    <div class="section-title">Twilio Configuration</div>
                    <div class="twilio-section">
                        <div class="form-grid">
                            <div class="form-group">
                                <label>Account SID</label>
                                <input type="text" name="twilioSid" value="${fn:escapeXml(customer.twilioAccountSid)}" style="font-family:var(--mono);font-size:12px;"/>
                            </div>
                            <div class="form-group">
                                <label>Auth Token</label>
                                <input type="password" name="twilioToken" placeholder="Leave blank to keep current token"/>
                            </div>
                            <div class="form-group span2">
                                <label>Sender ID / Phone Number</label>
                                <input type="text" name="twilioSender" value="${fn:escapeXml(customer.twilioSenderId)}"/>
                            </div>
                        </div>
                        <div class="warn-box" style="margin-top:14px;">
                            ⚠ The auth token is write-only. Leave the field blank to preserve the existing token.
                        </div>
                    </div>

                    <div class="form-actions">
                        <a href="${pageContext.request.contextPath}/admin/customers" class="btn btn-ghost">Cancel</a>
                        <button type="submit" class="btn btn-primary">✓ &nbsp;Save Changes</button>
                    </div>
                </form>
            </div>
        </main>
    </body>
</html>
