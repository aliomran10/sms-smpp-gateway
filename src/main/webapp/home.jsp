<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard — SMS Platform</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        *, *::before, *::after { margin: 0; padding: 0; box-sizing: border-box; }

        body {
            font-family: 'Inter', sans-serif;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            background: #0f0c1d;
            overflow-x: hidden;
            position: relative;
            padding: 20px;
        }

        /* Animated gradient orbs */
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

        .dashboard-container {
            position: relative;
            z-index: 10;
            width: 100%;
            max-width: 650px;
            background: rgba(255,255,255,0.05);
            backdrop-filter: blur(20px);
            -webkit-backdrop-filter: blur(20px);
            border: 1px solid rgba(255,255,255,0.1);
            border-radius: 28px;
            padding: 48px;
            box-shadow: 0 30px 80px rgba(0,0,0,0.5);
            animation: slideUp 0.6s cubic-bezier(0.16,1,0.3,1) both;
        }
        @keyframes slideUp {
            from { opacity: 0; transform: translateY(30px); }
            to   { opacity: 1; transform: translateY(0); }
        }

        .header {
            text-align: center;
            margin-bottom: 40px;
        }
        .header h1 {
            color: #f1f5f9;
            font-size: 28px;
            font-weight: 700;
            letter-spacing: -0.5px;
            margin-bottom: 8px;
        }
        .header p {
            color: #94a3b8;
            font-size: 14px;
        }

        .grid-menu {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }

        .menu-card {
            display: flex;
            flex-direction: column;
            align-items: flex-start;
            padding: 24px;
            background: rgba(255,255,255,0.03);
            border: 1px solid rgba(255,255,255,0.08);
            border-radius: 20px;
            text-decoration: none;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            position: relative;
            overflow: hidden;
        }
        .menu-card::before {
            content: '';
            position: absolute;
            top: 0; left: 0; width: 100%; height: 100%;
            background: linear-gradient(135deg, rgba(124,58,237,0.15) 0%, rgba(79,70,229,0.15) 100%);
            opacity: 0;
            transition: opacity 0.3s;
        }
        .menu-card:hover {
            transform: translateY(-5px);
            border-color: rgba(124,58,237,0.4);
            box-shadow: 0 12px 30px rgba(124,58,237,0.15);
        }
        .menu-card:hover::before {
            opacity: 1;
        }

        .card-icon {
            font-size: 28px;
            width: 52px;
            height: 52px;
            background: rgba(255,255,255,0.06);
            border-radius: 14px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-bottom: 20px;
            position: relative;
            z-index: 2;
            transition: transform 0.3s;
        }
        .menu-card:hover .card-icon {
            transform: scale(1.1);
            background: rgba(124,58,237,0.2);
        }

        .card-title {
            color: #f1f5f9;
            font-size: 16px;
            font-weight: 600;
            margin-bottom: 6px;
            position: relative;
            z-index: 2;
        }
        .card-desc {
            color: #64748b;
            font-size: 12px;
            line-height: 1.5;
            position: relative;
            z-index: 2;
        }

        .logout-card {
            grid-column: span 2;
            flex-direction: row;
            align-items: center;
            justify-content: center;
            gap: 12px;
            padding: 18px;
            border-color: rgba(239, 68, 68, 0.2);
        }
        .logout-card:hover {
            border-color: rgba(239, 68, 68, 0.5);
            box-shadow: 0 12px 30px rgba(239, 68, 68, 0.15);
        }
        .logout-card::before {
            background: linear-gradient(135deg, rgba(239, 68, 68, 0.1) 0%, rgba(220, 38, 38, 0.1) 100%);
        }
        .logout-card .card-icon {
            margin-bottom: 0;
            width: 40px;
            height: 40px;
            font-size: 20px;
        }
        .logout-card .card-title {
            margin-bottom: 0;
            color: #fca5a5;
        }

        @media (max-width: 580px) {
            .grid-menu {
                grid-template-columns: 1fr;
            }
            .logout-card {
                grid-column: span 1;
            }
            .dashboard-container {
                padding: 32px 24px;
            }
        }
    </style>
</head>
<body>
    <div class="dashboard-container">
        <div class="header">
            <h1>SMS Management Portal</h1>
            <p>Select a module to continue</p>
        </div>

        <div class="grid-menu">
            <a href="profile" class="menu-card">
                <div class="card-icon">👤</div>
                <div class="card-title">User Profile</div>
                <div class="card-desc">Update your personal information and configure Twilio connection credentials.</div>
            </a>

            <a href="SendSMSServlet" class="menu-card">
                <div class="card-icon">💬</div>
                <div class="card-title">Send SMS</div>
                <div class="card-desc">Compose messages and dispatch them instantly via configured API services.</div>
            </a>

            <a href="search-sms" class="menu-card">
                <div class="card-icon">📊</div>
                <div class="card-title">Manage SMS</div>
                <div class="card-desc">Review your delivery history, search logs, delete records, and audit statuses.</div>
            </a>

            <a href="logout" class="menu-card logout-card">
                <div class="card-icon">🔑</div>
                <div class="card-title">Logout Session</div>
            </a>
        </div>
    </div>
</body>
</html>