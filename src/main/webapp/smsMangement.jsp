<%-- 
    Document   : smsManagent.jsp
    Created on : May 18, 2026, 4:08:37 PM
    Author     : mohamed
--%>

<%@page import="com.mycompany.twilio.model.Message"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%@page import="java.util.List"%>


<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>SMS Management</title>

    <style>

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: Arial, sans-serif;
        }

        body {

            background: #f4f6f9;
            padding: 40px;
        }

        .container {

            max-width: 1200px;
            margin: auto;
            background: white;
            padding: 30px;
            border-radius: 14px;
            box-shadow: 0 4px 25px rgba(0,0,0,0.1);
        }

        h1 {

            text-align: center;
            margin-bottom: 30px;
            color: #333;
        }

        .search-form {

            display: flex;
            flex-direction: column;
            gap: 20px;
            margin-bottom: 30px;
        }

        .top-search {

            display: flex;
            gap: 15px;
        }

        .top-search input {

            flex: 1;
            padding: 14px;
            border: 1px solid #ccc;
            border-radius: 8px;
            font-size: 16px;
            outline: none;
            transition: 0.3s;
        }

        .top-search input:focus {

            border-color: #007bff;
            box-shadow: 0 0 5px rgba(0,123,255,0.3);
        }

        .top-search button {

            padding: 14px 25px;
            border: none;
            border-radius: 8px;
            background: #007bff;
            color: white;
            font-size: 16px;
            cursor: pointer;
            transition: 0.3s;
        }

        .top-search button:hover {

            background: #0056b3;
        }

        .date-group {

            display: flex;
            gap: 15px;
        }

        .field {

            flex: 1;
            display: flex;
            flex-direction: column;
        }

        .field label {

            margin-bottom: 8px;
            color: #555;
            font-size: 14px;
        }

        .field input {

            padding: 12px;
            border: 1px solid #ccc;
            border-radius: 8px;
            outline: none;
            font-size: 15px;
        }

        .field input:focus {

            border-color: #007bff;
            box-shadow: 0 0 5px rgba(0,123,255,0.3);
        }

        table {

            width: 100%;
            border-collapse: collapse;
            overflow: hidden;
            border-radius: 10px;
        }

        thead {

            background: #007bff;
            color: white;
        }

        th, td {

            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }

        tbody tr:hover {

            background: #f8f9fa;
        }

        .message-box {

            max-width: 350px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        .delete-btn {

            background: #dc3545;
            color: white;
            border: none;
            padding: 10px 16px;
            border-radius: 6px;
            cursor: pointer;
            transition: 0.3s;
        }

        .delete-btn:hover {

            background: #b02a37;
        }

        .empty {

            text-align: center;
            padding: 25px;
            font-size: 18px;
            color: #777;
        }

        @media (max-width: 768px) {

            body {

                padding: 15px;
            }

            .top-search,
            .date-group {

                flex-direction: column;
            }

            table {

                font-size: 14px;
            }

            th, td {

                padding: 10px;
            }
        }

    </style>

</head>

<body>

<div class="container">

    <h1>SMS Search</h1>

    <form action="search-sms"
          method="get"
          class="search-form">

        <div class="top-search">

            <input
                    type="search"
                    name="keyword"
                    placeholder="Search by message or phone number..."
            >

            <button type="submit">
                Search
            </button>

        </div>

        <div class="date-group">

            <div class="field">

                <label>
                    From Date
                </label>

                <input
                        type="datetime-local"
                        name="fromDate"
                >

            </div>

            <div class="field">

                <label>
                    To Date
                </label>

                <input
                        type="datetime-local"
                        name="toDate"
                >

            </div>

        </div>

    </form>

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

            List<Message> messages =
                    (List<Message>)
                            request.getAttribute("messages");

            if (messages != null
                    && !messages.isEmpty()) {

                for (Message m : messages) {

        %>

        <tr>

            <td>
                <%= m.getMsgId() %>
            </td>

            <td>
                <%= m.getSenderNo() %>
            </td>

            <td>
                <%= m.getRecipientNo() %>
            </td>

            <td class="message-box">
                <%= m.getMsg() %>
            </td>

            <td>
                <%= m.getSentAt() %>
            </td>

            <td>

                <form
                        action="delete-sms"
                        method="post"
                        onsubmit="return confirm('Delete this message?');"
                >

                    <input
                            type="hidden"
                            name="msgId"
                            value="<%= m.getMsgId() %>"
                    >

                    <button class="delete-btn">

                        Delete

                    </button>

                </form>

            </td>

        </tr>

        <%
                }

            } else {
        %>

        <tr>

            <td colspan="6"
                class="empty">

                No messages found.

            </td>

        </tr>

        <%
            }
        %>

        </tbody>

    </table>

</div>

</body>
</html>
