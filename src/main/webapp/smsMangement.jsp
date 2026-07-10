<%@page import="com.mycompany.twilio.model.Message"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SMS Management — SMS Platform</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        *, *::before, *::after { margin: 0; padding: 0; box-sizing: border-box; }

        body {
            font-family: 'Inter', sans-serif;
            min-height: 100vh;
            background: #0f0c1d;
            display: flex;
            align-items: flex-start;
            justify-content: center;
            padding: 40px 20px;
            position: relative;
            overflow-x: hidden;
        }
        body::before, body::after {
            content: '';
            position: fixed;
            border-radius: 50%;
            filter: blur(80px);
            opacity: 0.3;
            pointer-events: none;
        }
        body::before {
            width: 500px; height: 500px;
            background: radial-gradient(circle, #7c3aed, #4f46e5);
            top: -150px; left: -150px;
        }
        body::after {
            width: 400px; height: 400px;
            background: radial-gradient(circle, #0ea5e9, #6366f1);
            bottom: -100px; right: -100px;
        }

        .container {
            position: relative; z-index: 10;
            width: 100%; max-width: 1100px;
            background: rgba(255,255,255,0.05);
            backdrop-filter: blur(20px);
            -webkit-backdrop-filter: blur(20px);
            border: 1px solid rgba(255,255,255,0.1);
            border-radius: 24px;
            overflow: hidden;
            box-shadow: 0 30px 80px rgba(0,0,0,0.5);
            animation: slideUp 0.6s cubic-bezier(0.16,1,0.3,1) both;
        }
        @keyframes slideUp {
            from { opacity: 0; transform: translateY(30px); }
            to   { opacity: 1; transform: translateY(0); }
        }

        .card-header {
            background: linear-gradient(135deg, #7c3aed 0%, #4f46e5 100%);
            padding: 32px 40px;
            display: flex;
            align-items: center;
            justify-content: space-between;
        }
        .header-left {
            display: flex;
            align-items: center;
            gap: 16px;
        }
        .header-icon {
            width: 48px; height: 48px;
            background: rgba(255,255,255,0.2);
            border-radius: 14px;
            display: flex; align-items: center; justify-content: center;
            font-size: 22px;
        }
        .card-header h1 { color: #fff; font-size: 22px; font-weight: 700; letter-spacing: -0.5px; }
        .card-header p { color: rgba(255,255,255,0.75); font-size: 13px; margin-top: 2px; }

        .back-btn {
            display: inline-flex;
            align-items: center;
            gap: 6px;
            color: #fff;
            background: rgba(255,255,255,0.15);
            border: 1px solid rgba(255,255,255,0.2);
            padding: 8px 16px;
            border-radius: 10px;
            text-decoration: none;
            font-size: 13px;
            font-weight: 600;
            transition: background 0.2s;
        }
        .back-btn:hover {
            background: rgba(255,255,255,0.25);
        }

        .card-body { padding: 40px; }

        .search-form {
            display: flex;
            flex-direction: column;
            gap: 20px;
            margin-bottom: 36px;
            background: rgba(255,255,255,0.02);
            border: 1px solid rgba(255,255,255,0.06);
            padding: 24px;
            border-radius: 16px;
        }

        .top-search {
            display: flex;
            gap: 12px;
        }
        .top-search input {
            flex: 1;
            padding: 13px 18px;
            background: rgba(255,255,255,0.06);
            border: 1px solid rgba(255,255,255,0.1);
            border-radius: 12px;
            color: #f1f5f9;
            font-size: 15px;
            font-family: 'Inter', sans-serif;
            outline: none;
            transition: all 0.2s;
        }
        .top-search input:focus {
            border-color: #7c3aed;
            background: rgba(124,58,237,0.06);
            box-shadow: 0 0 0 3px rgba(124,58,237,0.2);
        }

        .search-btn {
            padding: 13px 28px;
            border: none;
            border-radius: 12px;
            background: linear-gradient(135deg, #7c3aed 0%, #4f46e5 100%);
            color: white;
            font-weight: 600;
            font-size: 15px;
            font-family: 'Inter', sans-serif;
            cursor: pointer;
            transition: transform 0.15s, box-shadow 0.2s;
            box-shadow: 0 4px 15px rgba(124,58,237,0.3);
        }
        .search-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(124,58,237,0.4);
        }

        .date-group {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }
        .field {
            display: flex;
            flex-direction: column;
            gap: 8px;
        }
        .field label {
            color: #94a3b8;
            font-size: 12px;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        .field input {
            padding: 12px 16px;
            background: rgba(255,255,255,0.06);
            border: 1px solid rgba(255,255,255,0.1);
            border-radius: 12px;
            color: #f1f5f9;
            font-size: 14px;
            font-family: 'Inter', sans-serif;
            outline: none;
            transition: all 0.2s;
        }
        .field input:focus {
            border-color: #7c3aed;
            background: rgba(124,58,237,0.06);
            box-shadow: 0 0 0 3px rgba(124,58,237,0.2);
        }
        .field input::-webkit-calendar-picker-indicator { filter: invert(1) opacity(0.5); }

        .table-responsive {
            overflow-x: auto;
            border-radius: 16px;
            border: 1px solid rgba(255,255,255,0.08);
            background: rgba(255,255,255,0.02);
        }

        table {
            width: 100%;
            border-collapse: collapse;
            text-align: left;
        }

        thead {
            background: rgba(255,255,255,0.04);
            border-bottom: 1px solid rgba(255,255,255,0.08);
        }

        th {
            padding: 16px 20px;
            color: #94a3b8;
            font-size: 12px;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        td {
            padding: 16px 20px;
            color: #cbd5e1;
            font-size: 14px;
            border-bottom: 1px solid rgba(255,255,255,0.04);
        }

        tbody tr:last-child td {
            border-bottom: none;
        }

        tbody tr:hover {
            background: rgba(255,255,255,0.02);
        }

        .message-box {
            max-width: 320px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        .delete-btn {
            background: rgba(239, 68, 68, 0.15);
            border: 1px solid rgba(239, 68, 68, 0.3);
            color: #fca5a5;
            padding: 8px 14px;
            border-radius: 8px;
            font-size: 13px;
            font-weight: 500;
            font-family: 'Inter', sans-serif;
            cursor: pointer;
            transition: all 0.2s;
        }

        .delete-btn:hover {
            background: rgba(239, 68, 68, 0.3);
            border-color: rgba(239, 68, 68, 0.5);
            box-shadow: 0 4px 12px rgba(239, 68, 68, 0.25);
        }

        .empty {
            text-align: center;
            padding: 40px;
            font-size: 15px;
            color: #64748b;
        }

        @media (max-width: 768px) {
            .card-body { padding: 20px; }
            .card-header { padding: 24px 20px; flex-direction: column; align-items: flex-start; gap: 16px; }
            .back-btn { align-self: flex-start; }
            .date-group { grid-template-columns: 1fr; gap: 15px; }
            .top-search { flex-direction: column; }
            .search-btn { width: 100%; }
        }
    </style>
</head>
<body>
<div class="container">
    <div class="card-header">
        <div class="header-left">
            <div class="header-icon">📊</div>
            <div>
                <h1>SMS Management</h1>
                <p>View, search and manage sent messages</p>
            </div>
        </div>
        <a href="home" class="back-btn">← Dashboard</a>
    </div>

    <div class="card-body">
        <form action="search-sms" method="get" class="search-form">
            <div class="top-search">
                <input type="search" name="keyword" placeholder="Search message content or contact numbers...">
                <button type="submit" class="search-btn">Search Logs</button>
            </div>

            <div class="date-group">
                <div class="field">
                    <label>From Date</label>
                    <input type="datetime-local" name="fromDate">
                </div>
                <div class="field">
                    <label>To Date</label>
                    <input type="datetime-local" name="toDate">
                </div>
            </div>
        </form>

        <div class="table-responsive">
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Sender</th>
                    <th>Recipient</th>
                    <th>Message</th>
                    <th>Date</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                <%
                    List<Message> messages = (List<Message>) request.getAttribute("messages");
                    if (messages != null && !messages.isEmpty()) {
                        for (Message m : messages) {
                %>
                <tr>
                    <td><%= m.getMsgId() %></td>
                    <td><%= m.getSenderNo() %></td>
                    <td><%= m.getRecipientNo() %></td>
                    <td class="message-box" title="<%= m.getMsg() %>"><%= m.getMsg() %></td>
                    <td><%= m.getSentAt() %></td>
                    <td>
                        <form action="delete-sms" method="post" onsubmit="return confirm('Are you sure you want to delete this message log?');">
                            <input type="hidden" name="msgId" value="<%= m.getMsgId() %>">
                            <button type="submit" class="delete-btn">Delete</button>
                        </form>
                    </td>
                </tr>
                <%
                        }
                    } else {
                %>
                <tr>
                    <td colspan="6" class="empty">
                        No messages found. Refine your keyword or date filter.
                    </td>
                </tr>
                <%
                    }
                %>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>
