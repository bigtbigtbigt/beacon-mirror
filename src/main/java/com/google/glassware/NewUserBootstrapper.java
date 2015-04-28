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
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.mirror.model.Command;
import com.google.api.services.mirror.model.Contact;
import com.google.api.services.mirror.model.NotificationConfig;
import com.google.api.services.mirror.model.Subscription;
import com.google.api.services.mirror.model.TimelineItem;
import com.google.common.collect.Lists;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.mirror.model.MenuItem;
import com.google.api.services.mirror.model.MenuValue;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

/**
 * Utility functions used when users first authenticate with this service
 *
 * @author Jenny Murphy - http://google.com/+JennyMurphy
 */
public class NewUserBootstrapper {
  private static final Logger LOG = Logger.getLogger(NewUserBootstrapper.class.getSimpleName());

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
   * Bootstrap a new user. Do all of the typical actions for a new user:
   * <ul>
   * <li>Creating a timeline subscription</li>
   * <li>Inserting a contact</li>
   * <li>Sending the user a welcome message</li>
   * </ul>
   */
  public static void bootstrapNewUser(HttpServletRequest req, String userId) throws IOException {
    Credential credential = AuthUtil.newAuthorizationCodeFlow().loadCredential(userId);

    // Create contact
    Contact starterProjectContact = new Contact();
    starterProjectContact.setId(MainServlet.CONTACT_ID);
    starterProjectContact.setDisplayName(MainServlet.CONTACT_NAME);
    starterProjectContact.setImageUrls(Lists.newArrayList(WebUtil.buildUrl(req,
        "/static/images/beacon-logo-black-vignette.png")));

/*
    starterProjectContact.setAcceptCommands(Lists.newArrayList(
        new Command().setType("TAKE_A_NOTE")));
*/

    Contact insertedContact = MirrorClient.insertContact(credential, starterProjectContact);
    LOG.info("Bootstrapper inserted contact " + insertedContact.getId() + " for user " + userId);

    try {
      // Subscribe to timeline updates
      Subscription subscription =
          MirrorClient.insertSubscription(credential, WebUtil.buildUrl(req, "/notify"), userId,
              "timeline");
      LOG.info("Bootstrapper inserted subscription " + subscription
          .getId() + " for user " + userId);
    } catch (GoogleJsonResponseException e) {
      LOG.warning("Failed to create timeline subscription. Might be running on "
          + "localhost. Details:" + e.getDetails().toPrettyString());
    }

    // Send welcome timeline item
/*
    TimelineItem timelineItem = new TimelineItem();
    String currentTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
    timelineItem.setText("Welcome, foo, to the Beacon Pub Crawl for Glass (" + currentTime + ")");
    timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));
    TimelineItem insertedItem = MirrorClient.insertTimelineItem(credential, timelineItem);
*/

      TimelineItem timelineItem = new TimelineItem();
      String welcomeHtml = generateWelcomeHtml(req);
      timelineItem.setHtml(welcomeHtml);

      List<MenuItem> menuItemList = new ArrayList<MenuItem>();
      // Built in actions
      //menuItemList.add(new MenuItem().setAction("REPLY"));
      //menuItemList.add(new MenuItem().setAction("READ_ALOUD"));
      menuItemList.add(new MenuItem().setAction("TOGGLE_PINNED"));

      // Add custom actions
/*
      List<MenuValue> menuValues = new ArrayList<MenuValue>();
      menuValues.add(new MenuValue().setIconUrl(WebUtil.buildUrl(req, "/static/images/vignette-icon.png"))
          .setDisplayName("Get Vignette Pack"));
      menuItemList.add(new MenuItem().setValues(menuValues).setId("insertVignetteBundle").setAction("CUSTOM"));
*/

      // And another custom action
/*
      List<MenuValue> menuValues2 = new ArrayList<MenuValue>();
      menuValues2.add(new MenuValue().setIconUrl(WebUtil.buildUrl(req, "/static/images/beacon-icon.png"))
          .setDisplayName("Say the password"));
      menuItemList.add(new MenuItem().setValues(menuValues2).setId("passwordCard").setAction("CUSTOM"));
*/

      menuItemList.add(new MenuItem().setAction("DELETE"));

      timelineItem.setMenuItems(menuItemList);
      timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));

      TimelineItem insertedItem = MirrorClient.insertTimelineItem(credential, timelineItem);

    LOG.info("Bootstrapper inserted welcome message " + insertedItem.getId() + " for user "
        + userId);
  }
}
