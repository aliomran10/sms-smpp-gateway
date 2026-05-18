<%-- 
    Document   : home
    Created on : May 18, 2026, 1:29:11 PM
    Author     : mohamed
--%>

<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>

<html>

<head>

    <title>Home</title>

    <style>

        body {

            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
        }

        .container {

            width: 400px;
            margin: 60px auto;
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0px 0px 10px gray;
        }

        h2 {

            text-align: center;
            margin-bottom: 30px;
        }

        .menu a {

            display: block;
            text-decoration: none;
            background: #007BFF;
            color: white;
            padding: 12px;
            margin-top: 15px;
            border-radius: 5px;
            text-align: center;
        }

        .menu a:hover {

            background: #0056b3;
        }

    </style>

</head>

<body>

<div class="container">

    <h2>SMS Management System</h2>

    <div class="menu">

        <a href="profile">
            Profile
        </a>

        <a href="SendSMSServlet">
            Send SMS
        </a>

        <a href="search-sms">
            Manage SMS
        </a>

        <a href="logout">
            Logout
        </a>

    </div>

</div>

</body>

</html>