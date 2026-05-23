<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <title>Admin — Add Customer</title>
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
                --text:    #e8eaf0;
                --muted:   #6b7280;
                --radius:  10px;
                --font:    'Syne', sans-serif;
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

            /* Sidebar */
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
            .nav-item .icon {
                font-size: 16px;
                width: 20px;
                text-align: center;
            }
            .sidebar-footer {
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
                transition: opacity .15s;
            }
            .logout-btn:hover {
                opacity: .75;
            }

            /* Main */
            .main {
                flex: 1;
                padding: 36px 40px;
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

            /* Form card */
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
            .form-grid.full {
                grid-template-columns: 1fr;
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
            input:focus, select:focus, textarea:focus {
                border-color: var(--accent);
            }
            input::placeholder, textarea::placeholder {
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
            <a href="${pageContext.request.contextPath}/admin/customers"   class="nav-item"><span class="icon">👥</span> Customers</a>
            <a href="${pageContext.request.contextPath}/admin/add-customer" class="nav-item active"><span class="icon">➕</span> Add Customer</a>
            <a href="${pageContext.request.contextPath}/admin/stats"        class="nav-item"><span class="icon">📊</span> Statistics</a>
            <div class="sidebar-footer">
                <a href="${pageContext.request.contextPath}/logout" class="logout-btn"><span>⬅</span> Logout</a>
            </div>
        </aside>

        <main class="main">
            <div class="breadcrumb">
                <a href="${pageContext.request.contextPath}/admin/customers">Customers</a>
                <span>›</span>
                <span>Add Customer</span>
            </div>

            <div class="page-header">
                <h1>Add Customer</h1>
                <p>Create a new customer account. The account will be immediately active — no OTP verification required.</p>
            </div>

            <div class="form-card">
                <c:if test="${not empty error}">
                    <div class="error-msg">⚠ &nbsp;${error}</div>
                </c:if>

                <form method="post" action="${pageContext.request.contextPath}/admin/add-customer" novalidate>

                    <!-- Personal Info -->
                    <div class="section-title">Personal Information</div>
                    <div class="form-grid">
                        <div class="form-group">
                            <label>Full Name <span class="req">*</span></label>
                            <input type="text" name="fullName" placeholder="e.g. John Doe" required/>
                        </div>
                        <div class="form-group">
                            <label>Birthday</label>
                            <input type="date" name="birthday"/>
                        </div>
                        <div class="form-group">
                            <label>Job / Title</label>
                            <input type="text" name="job" placeholder="e.g. Software Engineer"/>
                        </div>
                        <div class="form-group">
                            <label>MSISDN (Phone Number)</label>
                            <input type="text" name="msisdn" placeholder="e.g. 01012345678"/>
                            <span class="hint">Used to link sent/received SMS messages.</span>
                        </div>
                        <div class="form-group span2">
                            <label>Physical Address</label>
                            <input type="text" name="physicalAddress" placeholder="Street, City, Country"/>
                        </div>
                    </div>

                    <!-- Account Credentials -->
                    <div class="section-title">Account Credentials</div>
                    <div class="form-grid">
                        <div class="form-group">
                            <label>Email <span class="req">*</span></label>
                            <input type="email" name="email" placeholder="customer@example.com" required/>
                        </div>
                        <div class="form-group">
                            <label>Password <span class="req">*</span></label>
                            <input type="password" name="password" placeholder="Set initial password" required/>
                        </div>
                    </div>

                    <!-- Twilio Credentials -->
                    <div class="section-title">Twilio Configuration</div>
                    <div class="twilio-section">
                        <div class="form-grid">
                            <div class="form-group">
                                <label>Account SID</label>
                                <input type="text" name="twilioSid" placeholder="ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" style="font-family: var(--mono); font-size:12px;"/>
                            </div>
                            <div class="form-group">
                                <label>Auth Token</label>
                                <input type="password" name="twilioToken" placeholder="Your Twilio auth token"/>
                            </div>
                            <div class="form-group span2">
                                <label>Sender ID / Phone Number</label>
                                <input type="text" name="twilioSender" placeholder="e.g. +12015551234 or MyBrand"/>
                                <span class="hint">The Twilio number or alphanumeric sender ID used when this customer sends SMS.</span>
                            </div>
                        </div>
                    </div>

                    <div class="form-actions">
                        <a href="${pageContext.request.contextPath}/admin/customers" class="btn btn-ghost">Cancel</a>
                        <button type="submit" class="btn btn-primary">✓ &nbsp;Create Customer</button>
                    </div>
                </form>
            </div>
        </main>
    </body>
</html>
