<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"  uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <title>Admin — Statistics</title>
        <link rel="preconnect" href="https://fonts.googleapis.com"/>
        <link href="https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=DM+Mono:wght@400;500&display=swap" rel="stylesheet"/>
        <style>
            :root {
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
            .page-header{
                margin-bottom:32px;
            }
            .page-header h1{
                font-size:28px;
                font-weight:800;
            }
            .page-header p{
                color:var(--muted);
                font-size:13px;
                margin-top:4px;
            }

            .summary-grid{
                display:grid;
                grid-template-columns:repeat(3,1fr);
                gap:16px;
                margin-bottom:40px;
            }
            .stat-card{
                background:var(--surface);
                border:1px solid var(--border);
                border-radius:var(--radius);
                padding:24px;
                position:relative;
                overflow:hidden;
            }
            .stat-card::before{
                content:'';
                position:absolute;
                top:0;
                left:0;
                right:0;
                height:3px;
            }
            .stat-card.blue::before{
                background:var(--accent);
            }
            .stat-card.green::before{
                background:var(--accent2);
            }
            .stat-card.warn::before{
                background:var(--warn);
            }
            .stat-card .icon-bg{
                font-size:28px;
                margin-bottom:12px;
            }
            .stat-card .label{
                font-size:11px;
                font-weight:700;
                color:var(--muted);
                letter-spacing:.08em;
                text-transform:uppercase;
            }
            .stat-card .value{
                font-size:40px;
                font-weight:800;
                margin-top:4px;
                font-family:var(--mono);
                line-height:1;
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

            .search-box{
                background:var(--surface);
                border:1px solid var(--border);
                border-radius:var(--radius);
                padding:9px 14px;
                color:var(--text);
                font-family:var(--font);
                font-size:13px;
                outline:none;
                transition:border-color .15s;
                width:280px;
                margin-bottom:16px;
            }
            .search-box:focus{
                border-color:var(--accent);
            }
            .search-box::placeholder{
                color:var(--muted);
            }

            .table-wrap{
                background:var(--surface);
                border:1px solid var(--border);
                border-radius:var(--radius);
                overflow:hidden;
            }
            table{
                width:100%;
                border-collapse:collapse;
            }
            thead th{
                padding:12px 16px;
                text-align:left;
                font-size:11px;
                font-weight:700;
                letter-spacing:.07em;
                text-transform:uppercase;
                color:var(--muted);
                background:rgba(255,255,255,.03);
                border-bottom:1px solid var(--border);
                white-space:nowrap;
            }
            tbody tr{
                transition:background .12s;
            }
            tbody tr:hover{
                background:rgba(255,255,255,.03);
            }
            tbody tr+tr td{
                border-top:1px solid var(--border);
            }
            td{
                padding:13px 16px;
                font-size:13px;
                vertical-align:middle;
            }
            .mono{
                font-family:var(--mono);
                font-size:12px;
                color:var(--muted);
            }

            .bar-wrap{
                display:flex;
                align-items:center;
                gap:10px;
                min-width:140px;
            }
            .bar-track{
                flex:1;
                height:6px;
                background:var(--border);
                border-radius:3px;
                overflow:hidden;
            }
            .bar-fill{
                height:100%;
                border-radius:3px;
                background:var(--accent);
            }
            .bar-fill.green{
                background:var(--accent2);
            }
            .bar-val{
                font-family:var(--mono);
                font-size:12px;
                color:var(--text);
                min-width:28px;
                text-align:right;
            }

            .btn{
                padding:7px 14px;
                border-radius:8px;
                border:none;
                font-family:var(--font);
                font-size:12px;
                font-weight:700;
                cursor:pointer;
                text-decoration:none;
                display:inline-flex;
                align-items:center;
                gap:5px;
                transition:opacity .15s;
            }
            .btn:hover{
                opacity:.8;
            }
            .btn-ghost{
                background:var(--surface);
                border:1px solid var(--border);
                color:var(--text);
            }
            .empty-row td{
                padding:48px;
                text-align:center;
                color:var(--muted);
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
            <div class="page-header">
                <h1>Statistics</h1>
                <p>Platform-wide SMS activity and per-customer breakdown</p>
            </div>

            <div class="summary-grid">
                <div class="stat-card blue">
                    <div class="icon-bg">👥</div>
                    <div class="label">Total Customers</div>
                    <div class="value">${totalCustomers}</div>
                </div>
                <div class="stat-card green">
                    <div class="icon-bg">✉️</div>
                    <div class="label">Total Messages</div>
                    <div class="value">${totalMessages}</div>
                </div>
                <div class="stat-card warn">
                    <div class="icon-bg">🚀</div>
                    <div class="label">Sent Today</div>
                    <div class="value">${totalSentToday}</div>
                </div>
            </div>

            <div class="section-heading">Customer Breakdown</div>

            <input class="search-box" type="text" id="searchInput"
                   placeholder="🔍  Filter by name or email…"
                   oninput="filterTable(this.value)"/>

            <div class="table-wrap">
                <table id="statsTable">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Customer</th>
                            <th>MSISDN</th>
                            <th>Sent</th>
                            <th>Received</th>
                            <th>Total</th>
                            <th>Last Activity</th>
                            <th>Detail</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty allStats}">
                                <tr class="empty-row"><td colspan="8">No data available yet.</td></tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="s" items="${allStats}" varStatus="st">
                                    <tr data-search="${fn:toLowerCase(s.fullName)} ${fn:toLowerCase(s.email)}">
                                        <td class="mono">${st.index + 1}</td>
                                        <td>
                                            <div><strong>${s.fullName}</strong></div>
                                            <div class="mono" style="margin-top:2px;">${s.email}</div>
                                        </td>
                                        <td class="mono">${s.msisdn}</td>
                                        <td>
                                            <div class="bar-wrap">
                                                <div class="bar-track">
                                                    <div class="bar-fill" style="width:${s.sentPct}%"></div>
                                                </div>
                                                <span class="bar-val">${s.totalSent}</span>
                                            </div>
                                        </td>
                                        <td>
                                            <div class="bar-wrap">
                                                <div class="bar-track">
                                                    <div class="bar-fill green" style="width:${s.receivedPct}%"></div>
                                                </div>
                                                <span class="bar-val">${s.totalReceived}</span>
                                            </div>
                                        </td>
                                        <td class="mono" style="color:var(--text);font-weight:600;">${s.totalMessages}</td>
                                        <td class="mono">${not empty s.lastActivityAt ? s.lastActivityAt : '—'}</td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/CustomerStatsServlet?id=${s.userId}"
                                               class="btn btn-ghost">📋 View</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </main>

        <script>
            function filterTable(q) {
                q = q.toLowerCase();
                document.querySelectorAll('#statsTable tbody tr[data-search]').forEach(row => {
                    row.style.display = row.dataset.search.includes(q) ? '' : 'none';
                });
            }
        </script>
    </body>
</html>