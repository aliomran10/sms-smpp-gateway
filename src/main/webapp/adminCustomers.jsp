<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <title>Admin — Customers</title>
        <link rel="preconnect" href="https://fonts.googleapis.com"/>
        <link href="https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=DM+Mono:wght@400;500&display=swap" rel="stylesheet"/>
        <style>
            :root {
                --bg:        #0d0f14;
                --surface:   #161920;
                --border:    #252832;
                --accent:    #5b6af0;
                --accent2:   #3ecf8e;
                --danger:    #f04e5e;
                --warn:      #f0a04e;
                --text:      #e8eaf0;
                --muted:     #6b7280;
                --radius:    10px;
                --font:      'Syne', sans-serif;
                --mono:      'DM Mono', monospace;
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

            /* ── Sidebar ── */
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

            /* ── Main ── */
            .main {
                flex: 1;
                padding: 36px 40px;
                overflow-y: auto;
            }

            .page-header {
                display: flex;
                align-items: flex-end;
                justify-content: space-between;
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

            /* ── Stats bar ── */
            .stats-bar {
                display: grid;
                grid-template-columns: repeat(3, 1fr);
                gap: 16px;
                margin-bottom: 32px;
            }
            .stat-card {
                background: var(--surface);
                border: 1px solid var(--border);
                border-radius: var(--radius);
                padding: 20px 24px;
                position: relative;
                overflow: hidden;
            }
            .stat-card::before {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                height: 3px;
            }
            .stat-card.blue::before  {
                background: var(--accent);
            }
            .stat-card.green::before {
                background: var(--accent2);
            }
            .stat-card.warn::before  {
                background: var(--warn);
            }
            .stat-card .label {
                font-size: 11px;
                font-weight: 600;
                color: var(--muted);
                letter-spacing: .08em;
                text-transform: uppercase;
            }
            .stat-card .value {
                font-size: 34px;
                font-weight: 800;
                margin-top: 6px;
                font-family: var(--mono);
            }
            .stat-card.blue  .value  {
                color: var(--accent);
            }
            .stat-card.green .value  {
                color: var(--accent2);
            }
            .stat-card.warn  .value  {
                color: var(--warn);
            }

            /* ── Toolbar ── */
            .toolbar {
                display: flex;
                align-items: center;
                gap: 12px;
                margin-bottom: 20px;
            }
            .search-box {
                flex: 1;
                max-width: 320px;
                background: var(--surface);
                border: 1px solid var(--border);
                border-radius: var(--radius);
                padding: 9px 14px;
                color: var(--text);
                font-family: var(--font);
                font-size: 13px;
                outline: none;
                transition: border-color .15s;
            }
            .search-box:focus {
                border-color: var(--accent);
            }
            .search-box::placeholder {
                color: var(--muted);
            }

            .btn {
                padding: 9px 18px;
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
                background: var(--surface);
                border: 1px solid var(--border);
                color: var(--text);
            }
            .btn-danger  {
                background: var(--danger);
                color: #fff;
            }
            .btn-sm      {
                padding: 6px 12px;
                font-size: 12px;
            }

            /* ── Flash ── */
            .flash {
                padding: 12px 18px;
                border-radius: var(--radius);
                margin-bottom: 20px;
                font-size: 13px;
                font-weight: 600;
                background: rgba(62,207,142,.12);
                border: 1px solid rgba(62,207,142,.3);
                color: var(--accent2);
            }

            /* ── Table ── */
            .table-wrap {
                background: var(--surface);
                border: 1px solid var(--border);
                border-radius: var(--radius);
                overflow: hidden;
            }
            table {
                width: 100%;
                border-collapse: collapse;
            }
            thead th {
                padding: 13px 16px;
                text-align: left;
                font-size: 11px;
                font-weight: 700;
                letter-spacing: .07em;
                text-transform: uppercase;
                color: var(--muted);
                background: rgba(255,255,255,.03);
                border-bottom: 1px solid var(--border);
                white-space: nowrap;
            }
            tbody tr {
                transition: background .12s;
            }
            tbody tr:hover {
                background: rgba(255,255,255,.03);
            }
            tbody tr + tr td {
                border-top: 1px solid var(--border);
            }
            td {
                padding: 13px 16px;
                font-size: 13px;
                vertical-align: middle;
            }
            .mono {
                font-family: var(--mono);
                font-size: 12px;
                color: var(--muted);
            }

            .badge {
                display: inline-block;
                padding: 2px 8px;
                border-radius: 20px;
                font-size: 11px;
                font-weight: 700;
                letter-spacing: .04em;
            }
            .badge-green {
                background: rgba(62,207,142,.15);
                color: var(--accent2);
            }
            .badge-red   {
                background: rgba(240,78,94,.15);
                color: var(--danger);
            }

            .actions {
                display: flex;
                gap: 6px;
            }
            .empty-row td {
                padding: 48px 16px;
                text-align: center;
                color: var(--muted);
                font-size: 14px;
            }

            /* ── Confirm modal ── */
            .modal-overlay {
                display: none;
                position: fixed;
                inset: 0;
                background: rgba(0,0,0,.65);
                backdrop-filter: blur(4px);
                z-index: 1000;
                align-items: center;
                justify-content: center;
            }
            .modal-overlay.open {
                display: flex;
            }
            .modal {
                background: var(--surface);
                border: 1px solid var(--border);
                border-radius: 14px;
                padding: 32px;
                max-width: 400px;
                width: 90%;
                animation: popIn .2s ease;
            }
            @keyframes popIn {
                from {
                    transform: scale(.94);
                    opacity: 0;
                }
                to   {
                    transform: scale(1);
                    opacity: 1;
                }
            }
            .modal h3 {
                font-size: 18px;
                font-weight: 800;
                margin-bottom: 10px;
            }
            .modal p  {
                font-size: 13px;
                color: var(--muted);
                margin-bottom: 24px;
            }
            .modal-actions {
                display: flex;
                gap: 10px;
                justify-content: flex-end;
            }
        </style>
    </head>
    <body>

        <!-- ── Sidebar ─────────────────────────────────────────── -->
        <aside class="sidebar">
            <div class="sidebar-brand">
                <div class="logo-mark">📡</div>
                <h2>TwilioAdmin</h2>
                <p>Administrator Panel</p>
            </div>

            <a href="${pageContext.request.contextPath}/admin/customers" class="nav-item active">
                <span class="icon">👥</span> Customers
            </a>
            <a href="${pageContext.request.contextPath}/admin/add-customer" class="nav-item">
                <span class="icon">➕</span> Add Customer
            </a>
            <a href="${pageContext.request.contextPath}/admin/stats" class="nav-item">
                <span class="icon">📊</span> Statistics
            </a>

            <div class="sidebar-footer">
                <a href="${pageContext.request.contextPath}/logout" class="logout-btn">
                    <span>⬅</span> Logout
                </a>
            </div>
        </aside>

        <!-- ── Main ────────────────────────────────────────────── -->
        <main class="main">

            <div class="page-header">
                <div>
                    <h1>Customers</h1>
                    <p>Manage all registered customer accounts</p>
                </div>
                <a href="${pageContext.request.contextPath}/admin/add-customer" class="btn btn-primary">
                    ＋ &nbsp;Add Customer
                </a>
            </div>

            <!-- Stats bar -->
            <div class="stats-bar">
                <div class="stat-card blue">
                    <div class="label">Total Customers</div>
                    <div class="value">${totalCustomers}</div>
                </div>
                <div class="stat-card green">
                    <div class="label">Total Messages</div>
                    <div class="value">${totalMessages}</div>
                </div>
                <div class="stat-card warn">
                    <div class="label">Sent Today</div>
                    <div class="value">${totalSentToday}</div>
                </div>
            </div>

            <!-- Flash message -->
            <c:if test="${not empty flashMsg}">
                <div class="flash">✓ &nbsp;${flashMsg}</div>
            </c:if>

            <!-- Toolbar -->
            <div class="toolbar">
                <input class="search-box" type="text" id="searchInput"
                       placeholder="🔍  Search by name, email or MSISDN…"
                       oninput="filterTable(this.value)"/>
                <a href="${pageContext.request.contextPath}/admin/stats" class="btn btn-ghost">
                    📊 &nbsp;View Statistics
                </a>
            </div>

            <!-- Table -->
            <div class="table-wrap">
                <table id="customerTable">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Name</th>
                            <th>Email</th>
                            <th>MSISDN</th>
                            <th>Job</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty customers}">
                                <tr class="empty-row">
                                    <td colspan="7">No customers registered yet.</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="c" items="${customers}" varStatus="s">
                                    <tr data-search="${fn:toLowerCase(c.fullName)} ${fn:toLowerCase(c.email)} ${c.msisdn}">
                                        <td class="mono">${s.index + 1}</td>
                                        <td><strong>${c.fullName}</strong></td>
                                        <td class="mono">${c.email}</td>
                                        <td class="mono">${c.msisdn}</td>
                                        <td>${not empty c.job ? c.job : '—'}</td>
                                        <td>
                                            <%-- is_verified not on User model; show active badge --%>
                                            <span class="badge badge-green">Active</span>
                                        </td>
                                        <td>
                                            <div class="actions">
                                                <a href="${pageContext.request.contextPath}/admin/edit-customer?id=${c.userId}"
                                                   class="btn btn-ghost btn-sm">✏ Edit</a>
                                                <a href="${pageContext.request.contextPath}/admin/stats?id=${c.userId}"
                                                   class="btn btn-ghost btn-sm">📊 Stats</a>
                                                <button type="button" class="btn btn-danger btn-sm"
                                                        onclick="confirmDelete(${c.userId}, '${fn:escapeXml(c.fullName)}')">
                                                    🗑 Delete
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>

        </main>

        <!-- ── Delete Confirm Modal ─────────────────────────────── -->
        <div class="modal-overlay" id="deleteModal">
            <div class="modal">
                <h3>Delete Customer</h3>
                <p id="deleteModalMsg">Are you sure you want to delete this customer? This will also remove all their SMS history.</p>
                <div class="modal-actions">
                    <button class="btn btn-ghost" onclick="closeModal()">Cancel</button>
                    <form id="deleteForm" method="post"
                          action="${pageContext.request.contextPath}/admin/delete-customer">
                        <input type="hidden" name="userId" id="deleteUserId"/>
                        <button type="submit" class="btn btn-danger">Delete</button>
                    </form>
                </div>
            </div>
        </div>

        <script>
            function filterTable(query) {
                const q = query.toLowerCase();
                document.querySelectorAll('#customerTable tbody tr[data-search]').forEach(row => {
                    row.style.display = row.dataset.search.includes(q) ? '' : 'none';
                });
            }

            function confirmDelete(userId, name) {
                document.getElementById('deleteUserId').value = userId;
                document.getElementById('deleteModalMsg').textContent =
                        `Are you sure you want to delete "${name}"? This will also remove all their SMS history and cannot be undone.`;
                document.getElementById('deleteModal').classList.add('open');
            }

            function closeModal() {
                document.getElementById('deleteModal').classList.remove('open');
            }

            // Close on backdrop click
            document.getElementById('deleteModal').addEventListener('click', function (e) {
                if (e.target === this)
                    closeModal();
            });
        </script>
    </body>
</html>
