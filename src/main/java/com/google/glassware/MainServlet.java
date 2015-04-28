/*
 * Copyright (C) 2013 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.glassware;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.mirror.model.Command;
import com.google.api.services.mirror.model.Contact;
import com.google.api.services.mirror.model.MenuItem;
import com.google.api.services.mirror.model.MenuValue;
import com.google.api.services.mirror.model.NotificationConfig;
import com.google.api.services.mirror.model.TimelineItem;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles POST requests from index.jsp and admin.jsp
 *
 * @author Jenny Murphy - http://google.com/+JennyMurphy
 */
public class MainServlet extends HttpServlet {

  /**
   * Private class to process batch request results.
   * <p/>
   * For more information, see
   * https://code.google.com/p/google-api-java-client/wiki/Batch.
   */
  private final class BatchCallback extends JsonBatchCallback<TimelineItem> {
    private int success = 0;
    private int failure = 0;

    @Override
    public void onSuccess(TimelineItem item, HttpHeaders headers) throws IOException {
      ++success;
    }

    @Override
    public void onFailure(GoogleJsonError error, HttpHeaders headers) throws IOException {
      ++failure;
      LOG.info("Failed to insert item: " + error.getMessage());
    }
  }

  private static final Logger LOG = Logger.getLogger(MainServlet.class.getSimpleName());
  public static final String CONTACT_ID = "com.google.glassware.contact.beacon-crawl";
  public static final String CONTACT_NAME = "beaconcrawl";

  private static final String PAGINATED_HTML =
      "<article class='auto-paginate'>"
      + "<h2 class='blue text-large'>Did you know...?</h2>"
      + "<p>Cats are <em class='yellow'>solar-powered.</em> The time they spend napping in "
      + "direct sunlight is necessary to regenerate their internal batteries. Cats that do not "
      + "receive sufficient charge may exhibit the following symptoms: lethargy, "
      + "irritability, and disdainful glares. Cats will reactivate on their own automatically "
      + "after a complete charge cycle; it is recommended that they be left undisturbed during "
      + "this process to maximize your enjoyment of your cat.</p><br/><p>"
      + "For more cat maintenance tips, tap to view the website!</p>"
      + "</article>";

  public static String generateVignetteHtml(HttpServletRequest req) throws IOException {

    String vignetteHtml = "<article class='photo cover-only'>"
      + "<img src='" + WebUtil.buildUrl(req,"/static/images/beacon-background.jpg") + "' width='100%' height='100%'>"
      + "<div class='overlay-gradient-tall-dark'/>"
      + "<section>"
      + "<p class='text-auto-size'>beaconcrawl Vignette Pack</p>"
      + "</section>"
      + "</article>";

    return vignetteHtml;
  }

  public static String generateWelcomeHtml(HttpServletRequest req) throws IOException {

    String welcomeHtml =
      "<article class='photo cover-only'>"
      + "<img src='" + WebUtil.buildUrl(req,"/static/images/beacon-background.jpg") + "' width='100%' height='100%'>"
      + "<div class='overlay-gradient-tall-dark'/>"
      + "<section>"
      + "<p class='text-auto-size'>beaconcrawl for Glass</p>"
      + "</section>"
      + "</article>"
      + "<article class=''>"
      + "<img src='" + WebUtil.buildUrl(req,"/static/images/beacon-background.jpg") + "' width='100%' height='100%'>"
      + "<div class='overlay-full'/>"
      + "<section>"
      + "<p class='text-large'>Welcome to beaconcrawl!</p>"
      + "<p class='text-medium'>Use this app to get exclusive vignette images and share photos.</p>"
      + "</section>"
      + "</article>"
      + "<article class=''>"
      + "<img src='" + WebUtil.buildUrl(req,"/static/images/beacon-background.jpg") + "' width='100%' height='100%'>"
      + "<div class='overlay-full'/>"
      + "<section>"
      + "<p class='text-large'>To take photo vignettes</p>"
      + "<p class='text-medium'>1) Visit the website and click 'Get Vignette Pack'</p>"
      + "</section>"
      + "</article>"
      + "<article class=''>"
      + "<img src='" + WebUtil.buildUrl(req,"/static/images/beacon-background.jpg") + "' width='100%' height='100%'>"
      + "<div class='overlay-full'/>"
      + "<section>"
      + "<p class='text-large'>To take photo vignettes</p>"
      + "<p class='text-medium'>2) Select an image so that it’s on the screen</p>"
      + "</section>"
      + "</article>"
      + "<article class=''>"
      + "<img src='" + WebUtil.buildUrl(req,"/static/images/beacon-background.jpg") + "' width='100%' height='100%'>"
      + "<div class='overlay-full'/>"
      + "<section>"
      + "<p class='text-large'>To take photo vignettes</p>"
      + "<p class='text-medium'>3) Using the shutter button, take a picture</p>"
      + "</section>"
      + "</article>"
      + "<article class=''>"
      + "<img src='" + WebUtil.buildUrl(req,"/static/images/beacon-background.jpg") + "' width='100%' height='100%'>"
      + "<div class='overlay-full'/>"
      + "<section>"
      + "<p class='text-large'>To take photo vignettes</p>"
      + "<p class='text-medium'>4) Say “OK Glass, Make Vignette”</p>"
      + "<p class='text-medium'>Nice work! Now share it with your friends.</p>"
      + "</section>"
      + "</article>"
      + "<article class='photo'>"
      + "<img src='" + WebUtil.buildUrl(req,"/static/images/beacon-background.jpg") + "' width='100%' height='100%'>"
      + "<div class='overlay-full'/>"
      + "<section>"
      + "<p class='text-x-small text-minor'>Presented by Skylight1 with Aurnhammer</p>"
      + "</section>"
      + "</article>";

    return welcomeHtml;
 }


  /**
   * Do stuff when buttons on index.jsp and admin.jsp are clicked
   */
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {

    String userId = AuthUtil.getUserId(req);
    Credential credential = AuthUtil.newAuthorizationCodeFlow().loadCredential(userId);
    String message = "";

    if (req.getParameter("operation").equals("insertSubscription")) {

      // subscribe (only works deployed to production)
      try {
        MirrorClient.insertSubscription(credential, WebUtil.buildUrl(req, "/notify"), userId,
            req.getParameter("collection"));
        message = "Application is now subscribed to updates.";
      } catch (GoogleJsonResponseException e) {
        LOG.warning("Could not subscribe " + WebUtil.buildUrl(req, "/notify") + " because "
            + e.getDetails().toPrettyString());
        message = "Failed to subscribe. Check your log for details";
      }

    } else if (req.getParameter("operation").equals("deleteSubscription")) {

      // subscribe (only works deployed to production)
      MirrorClient.deleteSubscription(credential, req.getParameter("subscriptionId"));

      message = "Application has been unsubscribed.";

    } else if (req.getParameter("operation").equals("insertItem")) {
      LOG.fine("Inserting Timeline Item");
      TimelineItem timelineItem = new TimelineItem();

      if (req.getParameter("message") != null) {
        String currentTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
        timelineItem.setText(req.getParameter("message") + currentTime );
      }

      // Triggers an audible tone when the timeline item is received
      timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));

      if (req.getParameter("imageUrl") != null) {
        // Attach an image, if we have one
        URL url = new URL(req.getParameter("imageUrl"));
        String contentType = req.getParameter("contentType");
        MirrorClient.insertTimelineItem(credential, timelineItem, contentType, url.openStream());
      } else {
        MirrorClient.insertTimelineItem(credential, timelineItem);
      }

      message = "A timeline item has been inserted.";

    } else if (req.getParameter("operation").equals("insertWelcomeBundle")) {
      LOG.fine("Inserting Welcome Bundle");
      TimelineItem timelineItem = new TimelineItem();
      String welcomeHtml = generateWelcomeHtml(req);
      timelineItem.setHtml(welcomeHtml);

      List<MenuItem> menuItemList = new ArrayList<MenuItem>();
      // Built in actions
      //menuItemList.add(new MenuItem().setAction("REPLY"));
      //menuItemList.add(new MenuItem().setAction("READ_ALOUD"));
      menuItemList.add(new MenuItem().setAction("TOGGLE_PINNED"));

      // And custom actions
      List<MenuValue> menuValues = new ArrayList<MenuValue>();
      menuValues.add(new MenuValue().setIconUrl(WebUtil.buildUrl(req, "/static/images/vignette-icon.png"))
          .setDisplayName("Get Vignette Pack"));
      menuItemList.add(new MenuItem().setValues(menuValues).setId("insertVignetteBundle").setAction("CUSTOM"));
      // And another custom action
      List<MenuValue> menuValues2 = new ArrayList<MenuValue>();
      menuValues2.add(new MenuValue().setIconUrl(WebUtil.buildUrl(req, "/static/images/beacon-icon.png"))
          .setDisplayName("Say the password"));
      menuItemList.add(new MenuItem().setValues(menuValues2).setId("passwordCard").setAction("CUSTOM"));

      menuItemList.add(new MenuItem().setAction("DELETE"));

      timelineItem.setMenuItems(menuItemList);
      timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));

      MirrorClient.insertTimelineItem(credential, timelineItem);

      message = "A welcome timeline item has been inserted.";

    } else if (req.getParameter("operation").equals("insertVignetteBundle")) {
      LOG.fine("Inserting Vignette Bundle");
      List<TimelineItem> timeLineList = new ArrayList<TimelineItem>();

      TimelineItem timelineItem = new TimelineItem();
      String vignetteHtml = generateVignetteHtml(req);
      timelineItem.setHtml(vignetteHtml);
      timelineItem.setBundleId("vignetteBundle");
      timelineItem.setIsBundleCover(true);
      // Menu items
      List<MenuItem> menuItemList = new ArrayList<MenuItem>();
      menuItemList.add(new MenuItem().setAction("TOGGLE_PINNED"));
      menuItemList.add(new MenuItem().setAction("DELETE"));
      timelineItem.setMenuItems(menuItemList);

      timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));

      timeLineList.add(timelineItem);

      // Insert main card
      MirrorClient.insertTimelineItem(credential, timelineItem);

      // Insert other cards
      TimelineItem vignetteTimelineItem1 = new TimelineItem();
      //TimelineItem vignetteTimelineItem2 = new TimelineItem();
      TimelineItem vignetteTimelineItem3 = new TimelineItem();
      vignetteTimelineItem1.setId("logoBlack");
      //vignetteTimelineItem2.setId("logo");
      vignetteTimelineItem3.setId("logoDance");
      vignetteTimelineItem1.setBundleId("vignetteBundle");
      //vignetteTimelineItem2.setBundleId("vignetteBundle");
      vignetteTimelineItem3.setBundleId("vignetteBundle");
      vignetteTimelineItem1.setText(".");
      vignetteTimelineItem1.setHtml("<article class='photo'><img src='" + WebUtil.buildUrl(req, "/static/images/beacon-logo-black-vignette.png") + "' width='100%' height='100%'><section></section></article>");
      //vignetteTimelineItem2.setHtml("<article class='photo'><img src='" + WebUtil.buildUrl(req, "/static/images/beacon-logo-vignette.png") + "' width='100%' height='100%'><section></section></article>");
      vignetteTimelineItem3.setHtml("<article class='photo'><img src='" + WebUtil.buildUrl(req, "/static/images/beacon-logo-dance-vignette.png") + "' width='100%' height='100%'><section></section></article>");
      timeLineList.add(vignetteTimelineItem1);
      //timeLineList.add(vignetteTimelineItem2);
      timeLineList.add(vignetteTimelineItem3);
      vignetteTimelineItem1.setMenuItems(menuItemList);
      //vignetteTimelineItem2.setMenuItems(menuItemList);
      vignetteTimelineItem3.setMenuItems(menuItemList);
      MirrorClient.insertTimelineItem(credential, vignetteTimelineItem3);
      //MirrorClient.insertTimelineItem(credential, vignetteTimelineItem2);
      MirrorClient.insertTimelineItem(credential, vignetteTimelineItem1);

      message = "A vignette timeline item has been inserted.";

    } else if (req.getParameter("operation").equals("insertPaginatedItem")) {
      LOG.fine("Inserting Timeline Item");
      TimelineItem timelineItem = new TimelineItem();
      timelineItem.setHtml(PAGINATED_HTML);

      List<MenuItem> menuItemList = new ArrayList<MenuItem>();
      menuItemList.add(new MenuItem().setAction("OPEN_URI").setPayload(
          "https://www.google.com/search?q=cat+maintenance+tips"));
      timelineItem.setMenuItems(menuItemList);

      // Triggers an audible tone when the timeline item is received
      timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));

      MirrorClient.insertTimelineItem(credential, timelineItem);

      message = "A timeline item has been inserted.";

    } else if (req.getParameter("operation").equals("insertItemWithAction")) {
      LOG.fine("Inserting Timeline Item");
      TimelineItem timelineItem = new TimelineItem();
      timelineItem.setText("Tell me what you had for lunch :)");

      List<MenuItem> menuItemList = new ArrayList<MenuItem>();
      // Built in actions
      menuItemList.add(new MenuItem().setAction("REPLY"));
      menuItemList.add(new MenuItem().setAction("READ_ALOUD"));

      // And custom actions
      List<MenuValue> menuValues = new ArrayList<MenuValue>();
      menuValues.add(new MenuValue().setIconUrl(WebUtil.buildUrl(req, "/static/images/drill.png"))
          .setDisplayName("Drill In"));
      menuItemList.add(new MenuItem().setValues(menuValues).setId("drill").setAction("CUSTOM"));

      timelineItem.setMenuItems(menuItemList);
      timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));

      MirrorClient.insertTimelineItem(credential, timelineItem);

      message = "A timeline item with actions has been inserted.";

    } else if (req.getParameter("operation").equals("insertContact")) {
      if (req.getParameter("iconUrl") == null || req.getParameter("name") == null) {
        message = "Must specify iconUrl and name to insert contact";
      } else {
        // Insert a contact
        LOG.fine("Inserting contact Item");
        Contact contact = new Contact();
        contact.setId(req.getParameter("id"));
        contact.setDisplayName(req.getParameter("name"));
        contact.setImageUrls(Lists.newArrayList(req.getParameter("iconUrl")));
        contact.setAcceptCommands(Lists.newArrayList(new Command().setType("TAKE_A_NOTE")));
        MirrorClient.insertContact(credential, contact);

        message = "Inserted contact: " + req.getParameter("name");
      }

    } else if (req.getParameter("operation").equals("deleteContact")) {

      // Insert a contact
      LOG.fine("Deleting contact Item");
      MirrorClient.deleteContact(credential, req.getParameter("id"));

      message = "Contact has been deleted.";

    } else if (req.getParameter("operation").equals("insertItemAllUsers")) {
      if (req.getServerName().contains("glass-java-starter-demo.appspot.com")) {
        message = "This function is disabled on the demo instance.";
      }

      // Insert a contact
      List<String> users = AuthUtil.getAllUserIds();
      LOG.info("found " + users.size() + " users");
      if (users.size() > 10) {
        // We wouldn't want you to run out of quota on your first day!
        message =
            "Total user count is " + users.size() + ". Aborting broadcast " + "to save your quota.";
      } else {
        TimelineItem allUsersItem = new TimelineItem();
        allUsersItem.setText("Hello Beacon Crawlers! This is a broadcast message from Tony!");

        BatchRequest batch = MirrorClient.getMirror(null).batch();
        BatchCallback callback = new BatchCallback();

        // TODO: add a picture of a cat
        for (String user : users) {
          Credential userCredential = AuthUtil.getCredential(user);
          MirrorClient.getMirror(userCredential).timeline().insert(allUsersItem)
              .queue(batch, callback);
        }

        batch.execute();
        message =
            "Successfully sent cards to " + callback.success + " users (" + callback.failure
                + " failed).";
      }


    } else if (req.getParameter("operation").equals("deleteTimelineItem")) {

      // Delete a timeline item
      LOG.fine("Deleting Timeline Item");
      MirrorClient.deleteTimelineItem(credential, req.getParameter("itemId"));

      message = "Timeline Item has been deleted.";

    } else {
      String operation = req.getParameter("operation");
      LOG.warning("Unknown operation specified " + operation);
      message = "I don't know how to do that";
    }
    WebUtil.setFlash(req, message);
    // Redirect user to some webpage within the /webapp/ directory (i.e. index.jsp)
    if (req.getHeader("referer").contains("admin.jsp")) {
      res.sendRedirect(WebUtil.buildUrl(req, "/admin.jsp"));
    }
    else {
      res.sendRedirect(WebUtil.buildUrl(req, "/"));
    }

  }
}
