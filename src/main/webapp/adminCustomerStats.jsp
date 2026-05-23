<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"  uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%-- Pull the donut numbers in a scriptlet to avoid EL arithmetic errors --%>
<%
    com.mycompany.twilio.model.CustomerStats _s
            = (com.mycompany.twilio.model.CustomerStats) request.getAttribute("stats");
    int _sent = (_s != null) ? _s.getTotalSent() : 0;
    int _received = (_s != null) ? _s.getTotalReceived() : 0;
    int _total = _sent + _received;
    // SVG donut: circumference of r=54 circle ≈ 339.3
    double circ = 339.3;
    double sentDash = (_total > 0) ? (_sent * circ / _total) : 0;
    double receivedDash = (_total > 0) ? (_received * circ / _total) : 0;
    pageContext.setAttribute("_sentDash", String.format("%.1f", sentDash));
    pageContext.setAttribute("_receivedDash", String.format("%.1f", receivedDash));
    pageContext.setAttribute("_circ", String.format("%.1f", circ));
    pageContext.setAttribute("_sentOffset", String.format("%.1f", -sentDash));
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <title>Admin — Customer Stats: ${customer.fullName}</title>
        <link rel="preconnect" href="https://fonts.googleapis.com"/>
        <link href="https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=DM+Mono:wght@400;500&display=swap" rel="stylesheet"/>
        <style>
            :root{
                --bg:#0d0f14;
                --surface:#161920;
                --border:#252832;
                --accent:#5b6af0;
                --accent2:#3ecf8e;
                --danger:#f04e5e;
                --warn:#f0a04e;
                --text:#e8eaf0;
                --muted:#6b7280;
                --radius:10px;
                --font:'Syne',sans-serif;
                --mono:'DM Mono',monospace;
            }
            *,*::before,*::after{
                box-sizing:border-box;
                margin:0;
                padding:0;
            }
            body{
                font-family:var(--font);
                background:var(--bg);
                color:var(--text);
                min-height:100vh;
                display:flex;
            }

            .sidebar{
                width:230px;
                min-height:100vh;
                background:var(--surface);
                border-right:1px solid var(--border);
                display:flex;
                flex-direction:column;
                padding:28px 0;
                flex-shrink:0;
                position:sticky;
                top:0;
                height:100vh;
            }
            .sidebar-brand{
                padding:0 24px 28px;
                border-bottom:1px solid var(--border);
                margin-bottom:16px;
            }
            .logo-mark{
                width:36px;
                height:36px;
                background:var(--accent);
                border-radius:8px;
                display:flex;
                align-items:center;
                justify-content:center;
                font-size:18px;
                margin-bottom:10px;
            }
            .sidebar-brand h2{
                font-size:15px;
                font-weight:700;
            }
            .sidebar-brand p{
                font-size:11px;
                color:var(--muted);
                margin-top:2px;
            }
            .nav-item{
                display:flex;
                align-items:center;
                gap:10px;
                padding:11px 24px;
                color:var(--muted);
                text-decoration:none;
                font-size:13.5px;
                font-weight:600;
                transition:all .15s;
                border-left:3px solid transparent;
            }
            .nav-item:hover{
                color:var(--text);
                background:rgba(255,255,255,.04);
            }
            .nav-item.active{
                color:var(--accent);
                border-left-color:var(--accent);
                background:rgba(91,106,240,.08);
            }
            .nav-item .icon{
                font-size:16px;
                width:20px;
                text-align:center;
            }
            .sidebar-footer{
                margin-top:auto;
                padding:16px 24px;
                border-top:1px solid var(--border);
            }
            .logout-btn{
                display:flex;
                align-items:center;
                gap:8px;
                color:var(--danger);
                text-decoration:none;
                font-size:13px;
                font-weight:600;
            }
            .logout-btn:hover{
                opacity:.75;
            }

            .main{
                flex:1;
                padding:36px 40px;
                overflow-y:auto;
            }
            .breadcrumb{
                display:flex;
                align-items:center;
                gap:8px;
                font-size:12px;
                color:var(--muted);
                margin-bottom:12px;
            }
            .breadcrumb a{
                color:var(--accent);
                text-decoration:none;
            }
            .breadcrumb a:hover{
                text-decoration:underline;
            }

            .profile-card{
                background:var(--surface);
                border:1px solid var(--border);
                border-radius:14px;
                padding:28px 32px;
                display:flex;
                align-items:center;
                gap:28px;
                margin-bottom:32px;
                position:relative;
                overflow:hidden;
            }
            .profile-card::before{
                content:'';
                position:absolute;
                top:0;
                left:0;
                right:0;
                height:4px;
                background:linear-gradient(90deg,var(--accent),var(--accent2));
            }
            .avatar{
                width:72px;
                height:72px;
                border-radius:50%;
                background:linear-gradient(135deg,var(--accent),var(--accent2));
                display:flex;
                align-items:center;
                justify-content:center;
                font-size:28px;
                font-weight:800;
                color:#fff;
                flex-shrink:0;
            }
            .profile-info h2{
                font-size:22px;
                font-weight:800;
            }
            .profile-info .meta{
                display:flex;
                flex-wrap:wrap;
                gap:16px;
                margin-top:8px;
            }
            .meta-item{
                display:flex;
                align-items:center;
                gap:6px;
                font-size:12px;
                color:var(--muted);
                font-family:var(--mono);
            }
            .profile-actions{
                margin-left:auto;
                display:flex;
                gap:10px;
            }

            .stats-grid{
                display:grid;
                grid-template-columns:repeat(3,1fr);
                gap:16px;
                margin-bottom:36px;
            }
            .stat-card{
                background:var(--surface);
                border:1px solid var(--border);
                border-radius:var(--radius);
                padding:22px 24px;
            }
            .stat-card .label{
                font-size:11px;
                font-weight:700;
                color:var(--muted);
                letter-spacing:.08em;
                text-transform:uppercase;
            }
            .stat-card .value{
                font-size:42px;
                font-weight:800;
                font-family:var(--mono);
                margin-top:6px;
                line-height:1;
            }
            .stat-card .sub{
                font-size:12px;
                color:var(--muted);
                margin-top:6px;
            }
            .stat-card.blue  .value{
                color:var(--accent);
            }
            .stat-card.green .value{
                color:var(--accent2);
            }
            .stat-card.warn  .value{
                color:var(--warn);
            }

            .donut-section{
                background:var(--surface);
                border:1px solid var(--border);
                border-radius:var(--radius);
                padding:28px;
                display:flex;
                align-items:center;
                gap:40px;
                margin-bottom:32px;
            }
            .donut-wrap{
                position:relative;
                flex-shrink:0;
            }
            .donut-center{
                position:absolute;
                inset:0;
                display:flex;
                flex-direction:column;
                align-items:center;
                justify-content:center;
                text-align:center;
            }
            .donut-center .big{
                font-size:28px;
                font-weight:800;
                font-family:var(--mono);
            }
            .donut-center .tiny{
                font-size:10px;
                color:var(--muted);
                letter-spacing:.06em;
                text-transform:uppercase;
                margin-top:2px;
            }
            .donut-legend{
                display:flex;
                flex-direction:column;
                gap:14px;
            }
            .legend-item{
                display:flex;
                align-items:center;
                gap:10px;
            }
            .legend-dot{
                width:10px;
                height:10px;
                border-radius:50%;
                flex-shrink:0;
            }
            .legend-label{
                font-size:13px;
                color:var(--muted);
            }
            .legend-value{
                font-family:var(--mono);
                font-size:14px;
                font-weight:700;
                margin-left:auto;
                padding-left:20px;
            }

            .section-heading{
                font-size:14px;
                font-weight:800;
                margin-bottom:16px;
                display:flex;
                align-items:center;
                gap:10px;
            }
            .section-heading::after{
                content:'';
                flex:1;
                height:1px;
                background:var(--border);
            }

            .info-grid{
                display:grid;
                grid-template-columns:1fr 1fr;
                gap:16px;
            }
            .info-card{
                background:var(--surface);
                border:1px solid var(--border);
                border-radius:var(--radius);
                padding:20px 22px;
            }
            .info-card h4{
                font-size:11px;
                font-weight:700;
                letter-spacing:.08em;
                text-transform:uppercase;
                color:var(--muted);
                margin-bottom:14px;
            }
            .info-row{
                display:flex;
                justify-content:space-between;
                align-items:center;
                padding:7px 0;
                border-bottom:1px solid var(--border);
                font-size:13px;
            }
            .info-row:last-child{
                border-bottom:none;
            }
            .info-row .key{
                color:var(--muted);
            }
            .info-row .val{
                font-family:var(--mono);
                font-size:12px;
            }

            .btn{
                padding:9px 18px;
                border-radius:8px;
                border:none;
                font-family:var(--font);
                font-size:13px;
                font-weight:700;
                cursor:pointer;
                text-decoration:none;
                display:inline-flex;
                align-items:center;
                gap:6px;
                transition:opacity .15s,transform .1s;
            }
            .btn:hover{
                opacity:.85;
                transform:translateY(-1px);
            }
            .btn-ghost{
                background:transparent;
                border:1px solid var(--border);
                color:var(--text);
            }
            .btn-sm{
                padding:6px 12px;
                font-size:12px;
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
            <a href="${pageContext.request.contextPath}/admin/customers"    class="nav-item"><span class="icon">👥</span> Customers</a>
            <a href="${pageContext.request.contextPath}/admin/add-customer" class="nav-item"><span class="icon">➕</span> Add Customer</a>
            <a href="${pageContext.request.contextPath}/admin/stats"        class="nav-item active"><span class="icon">📊</span> Statistics</a>
            <div class="sidebar-footer">
                <a href="${pageContext.request.contextPath}/logout" class="logout-btn"><span>⬅</span> Logout</a>
            </div>
        </aside>

        <main class="main">
            <div class="breadcrumb">
                <a href="${pageContext.request.contextPath}/admin/customers">Customers</a>
                <span>›</span>
                <a href="${pageContext.request.contextPath}/admin/stats">Statistics</a>
                <span>›</span>
                <span>${customer.fullName}</span>
            </div>

            <!-- Profile card -->
            <div class="profile-card">
                <div class="avatar">${fn:substring(customer.fullName, 0, 1)}</div>
                <div class="profile-info">
                    <h2>${customer.fullName}</h2>
                    <div class="meta">
                        <span class="meta-item">✉ ${customer.email}</span>
                        <span class="meta-item">📱 ${customer.msisdn}</span>
                        <c:if test="${not empty customer.job}">
                            <span class="meta-item">💼 ${customer.job}</span>
                        </c:if>
                        <c:if test="${not empty stats.lastActivityAt}">
                            <span class="meta-item">🕐 Last active: ${stats.lastActivityAt}</span>
                        </c:if>
                    </div>
                </div>
                <div class="profile-actions">
                    <a href="${pageContext.request.contextPath}/admin/edit-customer?id=${customer.userId}"
                       class="btn btn-ghost btn-sm">✏ Edit Profile</a>
                    <a href="${pageContext.request.contextPath}/admin/stats" class="btn btn-ghost btn-sm">← All Stats</a>
                </div>
            </div>

            <!-- KPI cards -->
            <div class="stats-grid">
                <div class="stat-card blue">
                    <div class="label">Messages Sent</div>
                    <div class="value">${stats.totalSent}</div>
                    <div class="sub">Outbound SMS via Twilio</div>
                </div>
                <div class="stat-card green">
                    <div class="label">Messages Received</div>
                    <div class="value">${stats.totalReceived}</div>
                    <div class="sub">Inbound SMS via webhook</div>
                </div>
                <div class="stat-card warn">
                    <div class="label">Total Activity</div>
                    <div class="value">${stats.totalMessages}</div>
                    <div class="sub">Sent + received combined</div>
                </div>
            </div>

            <!-- Donut chart — values computed safely in the scriptlet above -->
            <div class="section-heading">Message Distribution</div>
            <div class="donut-section">
                <div class="donut-wrap">
                    <svg width="160" height="160" viewBox="0 0 160 160">
                    <circle cx="80" cy="80" r="54" fill="none" stroke="#252832" stroke-width="18"/>
                    <circle cx="80" cy="80" r="54" fill="none" stroke="#3ecf8e" stroke-width="18"
                            stroke-dasharray="${_receivedDash} ${_circ}"
                            stroke-dashoffset="${_sentOffset}"
                            transform="rotate(-90 80 80)" stroke-linecap="round"/>
                    <circle cx="80" cy="80" r="54" fill="none" stroke="#5b6af0" stroke-width="18"
                            stroke-dasharray="${_sentDash} ${_circ}"
                            stroke-dashoffset="0"
                            transform="rotate(-90 80 80)" stroke-linecap="round"/>
                    </svg>
                    <div class="donut-center">
                        <span class="big">${stats.totalMessages}</span>
                        <span class="tiny">Total</span>
                    </div>
                </div>
                <div class="donut-legend">
                    <div class="legend-item">
                        <div class="legend-dot" style="background:var(--accent)"></div>
                        <span class="legend-label">Sent</span>
                        <span class="legend-value">${stats.totalSent}</span>
                    </div>
                    <div class="legend-item">
                        <div class="legend-dot" style="background:var(--accent2)"></div>
                        <span class="legend-label">Received</span>
                        <span class="legend-value">${stats.totalReceived}</span>
                    </div>
                </div>
            </div>

            <!-- Profile detail -->
            <div class="section-heading">Account Details</div>
            <div class="info-grid">
                <div class="info-card">
                    <h4>Personal Info</h4>
                    <div class="info-row"><span class="key">Full Name</span> <span class="val">${customer.fullName}</span></div>
                    <div class="info-row"><span class="key">Email</span>     <span class="val">${customer.email}</span></div>
                    <div class="info-row"><span class="key">MSISDN</span>    <span class="val">${customer.msisdn}</span></div>
                    <div class="info-row"><span class="key">Birthday</span>  <span class="val">${not empty customer.birthday ? customer.birthday : '—'}</span></div>
                    <div class="info-row"><span class="key">Job</span>       <span class="val">${not empty customer.job ? customer.job : '—'}</span></div>
                    <div class="info-row"><span class="key">Address</span>   <span class="val">${not empty customer.physicalAddress ? customer.physicalAddress : '—'}</span></div>
                </div>
                <div class="info-card">
                    <h4>Twilio Configuration</h4>
                    <div class="info-row">
                        <span class="key">Account SID</span>
                        <span class="val">
                            <c:choose>
                                <c:when test="${not empty customer.twilioAccountSid}">
                                    ${fn:substring(customer.twilioAccountSid, 0, 8)}••••••••
                                </c:when>
                                <c:otherwise>—</c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                    <div class="info-row"><span class="key">Auth Token</span>  <span class="val">••••••••••••</span></div>
                    <div class="info-row"><span class="key">Sender ID</span>   <span class="val">${not empty customer.twilioSenderId ? customer.twilioSenderId : '—'}</span></div>
                    <div class="info-row"><span class="key">Last Activity</span><span class="val">${not empty stats.lastActivityAt ? stats.lastActivityAt : 'Never'}</span></div>
                </div>
            </div>
        </main>
    </body>
</html>