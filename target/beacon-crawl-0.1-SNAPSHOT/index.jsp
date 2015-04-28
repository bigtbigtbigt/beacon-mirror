<!--
Copyright (C) 2013 Google Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->
<%@ page import="com.google.api.client.auth.oauth2.Credential" %>
<%@ page import="com.google.api.services.mirror.model.Contact" %>
<%@ page import="com.google.glassware.MirrorClient" %>
<%@ page import="com.google.glassware.WebUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.api.services.mirror.model.TimelineItem" %>
<%@ page import="com.google.api.services.mirror.model.Subscription" %>
<%@ page import="com.google.api.services.mirror.model.Attachment" %>
<%@ page import="com.google.glassware.MainServlet" %>
<%@ page import="org.apache.commons.lang3.StringEscapeUtils" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!doctype html>
<%
  String userId = com.google.glassware.AuthUtil.getUserId(request);
  String appBaseUrl = WebUtil.buildUrl(request, "/");

  Credential credential = com.google.glassware.AuthUtil.getCredential(userId);

  Contact contact = MirrorClient.getContact(credential, MainServlet.CONTACT_ID);

  List<TimelineItem> timelineItems = MirrorClient.listItems(credential, 3L).getItems();


  List<Subscription> subscriptions = MirrorClient.listSubscriptions(credential).getItems();
  boolean timelineSubscriptionExists = false;
  boolean locationSubscriptionExists = false;


  if (subscriptions != null) {
    for (Subscription subscription : subscriptions) {
      if (subscription.getId().equals("timeline")) {
        timelineSubscriptionExists = true;
      }
      if (subscription.getId().equals("locations")) {
        locationSubscriptionExists = true;
      }
    }
  }

%>
<html>
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>beaconcrawl for Glass</title>
  <link href="/static/bootstrap/css/bootstrap.min.css" rel="stylesheet"
        media="screen">
  <link href="/static/bootstrap/css/bootstrap-responsive.min.css"
        rel="stylesheet" media="screen">
  <link href="/static/main.css" rel="stylesheet" media="screen">
</head>
<body>
<div class="navbar navbar-inverse navbar-fixed-top">
  <div class="navbar-inner">
    <div class="container">
      <a class="brand" href="#">beaconcrawl for Glass</a>
    </div>
  </div>
</div>

<div class="container">

  <% String flash = WebUtil.getClearFlash(request);
    if (flash != null) { %>
  <div class="alert alert-info"><%= StringEscapeUtils.escapeHtml4(flash) %></div>
  <% } %>

  <h1>Welcome to beaconcrawl!</h1>
  <div class="row">

    <div style="clear:both;"></div>
  </div>

  <hr/>

  <div class="row">
    <div class="span4">
      <h2>Download the APK</h2>
      <p><a href="<%= WebUtil.buildUrl(request, "/static/beaconscan.apk") %>">beaconscan.apk</a></p>
      <p><strong>Mac Instructions</strong></p>
<ol>
<li>Save beaconscan.apk to the same directory as <strong>adb</strong></li>
<li>Plug your Glass into USB</li>
<li>Open terminal and cd to the directory where adb is saved (i.e. cd android-sdks/platform-tools/)</li>
<li>Install by running the following command <pre>./adb install beaconscan.apk</pre></li>
<li>Turn up your volume</li>
<li>Say "OK Glass, Start a beaconcrawl!"</li>
</ol>
<p>Have fun!</p> 
    </div>

    <div class="span4">
      <h2>Timeline</h2>

      <form action="<%= WebUtil.buildUrl(request, "/main") %>" method="post">
        <input type="hidden" name="operation" value="insertWelcomeBundle">
        <button class="btn btn-block" type="submit">
          Get the "Welcome" bundle again</button>
      </form>

      <form action="<%= WebUtil.buildUrl(request, "/main") %>" method="post">
        <input type="hidden" name="operation" value="insertVignetteBundle">
        <button class="btn btn-block" type="submit">
          Get the "Vignette" pack</button>
      </form>

    </div>

    <div class="span4">
      <h2>Contacts</h2>

      <% if (contact == null) { %>
      <form action="<%= WebUtil.buildUrl(request, "/main") %>" method="post">
        <input type="hidden" name="operation" value="insertContact">
        <input type="hidden" name="iconUrl" value="<%= appBaseUrl +
               "static/images/chipotle-tube-640x360.jpg" %>">
        <input type="hidden" name="id"
               value="<%= MainServlet.CONTACT_ID %>">
        <input type="hidden" name="name"
               value="<%= MainServlet.CONTACT_NAME %>">
        <button class="btn btn-block btn-success" type="submit">
          Insert beaconcrawl Contact
        </button>
      </form>
      <% } else { %>
      <form action="<%= WebUtil.buildUrl(request, "/main") %>" method="post">
        <input type="hidden" name="operation" value="deleteContact">
        <input type="hidden" name="id" value="<%= MainServlet.CONTACT_ID %>">
        <button class="btn btn-block btn-danger" type="submit">
          Delete beaconcrawl Contact
        </button>
      </form>
      <% } %>

    </div>

    <div class="span4">
      <h2>Admin</h2>

      <form action="admin.jsp" method="get">
        <button class="btn btn-block" type="submit">
          Admin interface (beta)
        </button>
      </form>
    </div>

  </div>
</div>

<script
    src="//ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
<script src="/static/bootstrap/js/bootstrap.min.js"></script>
</body>
</html>
