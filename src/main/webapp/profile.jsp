<%-- 
    Document   : profile
    Created on : May 18, 2026, 12:40:27 PM
    Author     : mohamed
--%>

<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>

<html>

<head>

    <title>User Profile</title>

    <style>

        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
        }

        .container {
            width: 550px;
            margin: 40px auto;
            background-color: white;
            padding: 25px;
            border-radius: 10px;
            box-shadow: 0px 0px 10px gray;
        }

        h2 {
            text-align: center;
            margin-bottom: 25px;
        }

        h3 {
            margin-top: 25px;
            color: #007BFF;
            border-bottom: 1px solid #ddd;
            padding-bottom: 5px;
        }

        .field {
            margin-top: 15px;
        }

        label {
            font-weight: bold;
            display: block;
            margin-bottom: 5px;
        }

        input,
        textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 5px;
            box-sizing: border-box;
        }

        textarea {
            resize: vertical;
            height: 80px;
        }

        button {
            width: 100%;
            margin-top: 25px;
            padding: 12px;
            background-color: #007BFF;
            color: white;
            border: none;
            border-radius: 5px;
            font-size: 15px;
            cursor: pointer;
        }

        button:hover {
            background-color: #0056b3;
        }

    </style>

</head>

<body>

<div class="container">

    <h2>User Profile</h2>

    <form action="profile" method="post">

        <h3>Personal Information</h3>

        <div class="field">

            <label>Full Name</label>

            <input
                    type="text"
                    name="fullName"
                    value="${user.fullName}"
                    required
            >
        </div>

        <div class="field">

            <label>Email</label>

            <input
                    type="email"
                    name="email"
                    value="${user.email}"
                    required
            >
        </div>

        <div class="field">

            <label>Phone Number</label>

            <input
                    type="text"
                    name="msisdn"
                    value="${user.msisdn}"
            >
        </div>

        <div class="field">

            <label>Job Title</label>

            <input
                    type="text"
                    name="job"
                    value="${user.job}"
            >
        </div>

        <div class="field">

            <label>Birthday</label>

            <input
                    type="date"
                    name="birthday"
                    value="${user.birthday}"
            >
        </div>

        <div class="field">

            <label>Address</label>

            <textarea
                    name="physicalAddress"
            >${user.physicalAddress}</textarea>
        </div>

        <h3>Twilio Settings</h3>

        <div class="field">

            <label>Account SID</label>

            <input
                    type="text"
                    name="accountSid"
                    value="${user.twilioAccountSid}"
            >
        </div>

        <div class="field">

            <label>Auth Token</label>

            <input
                    type="password"
                    name="authToken"
                    placeholder="Enter new token"
            >
        </div>

        <div class="field">

            <label>Sender Number</label>

            <input
                    type="text"
                    name="senderId"
                    value="${user.twilioSenderId}"
            >
        </div>

        <button type="submit">
            Update Profile
        </button>

    </form>

</div>

</body>

</html>