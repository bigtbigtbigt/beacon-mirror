Google Mirror API app for Beacon Crawl
======================================

BeaconCrawl (http://beaconcrawl.com/) is an interactive bar crawl event supporting bars in lower New
York that were hit by 2012's Superstorm Sandy.

This app is for Google Glass explorers participating in the beacon crawl. It allows them to take
photos and share them using BeaconCrawl themed overlays.

Notifications may also be sent to Google Glass participants by administrators of the app.

This code is based off of Google's Mirror API QuickStart for Java

Please see here for more information:
https://developers.google.com/glass/quickstart/java

## Running the server

Apache Maven and Tomcat 7 are recommended for running this Java application (as opposed to Jetty, as
Google recommends in their quickstart). This is, in part, because of the HTTPS/SSL requirement of 
the application.

* https://maven.apache.org/
* https://tomcat.apache.org/

To deploy, be sure to run the following from the same directory as the pom.xml file (On Ubuntu, 
usually /web/beacon-mirror/)

````
sudo mvn compiler:compile
sudo mvn war:war | tee /dev/tty
sudo cp /web/beacon-mirror/target/beacon-crawl-0.1-SNAPSHOT.war /var/lib/tomcat7/webapps/ROOT.war
````

(I chained these together in .bash_profile with the command "deploy" as follows)

    alias deploy="cd /web/beacon-mirror/ ; sudo mvn compiler:compile ; sudo mvn war:war | tee /dev/tty ; sudo cp /web/beacon-mirror/target/beacon-crawl-0.1-SNAPSHOT.war /var/lib/tomcat7/webapps/ROOT.war"

## Key files

### /src/main/java/com/google/glassware/MainServlet.java

Based off the QuickStart, this file checks to see if the user is new, welcomes them if appropriate,
sets them up with a session, and handles all the POSTS coming from admin.jsp and index.jsp, 
translating them into commands for the Mirror API

### /src/main/webapp/admin.jsp

Based off the Quickstart, this is the administrative control panel for the app. It enables an admin
to, for example, send a Glass notification to all the active users.

### /src/main/webapp/index.jsp

This page has some of the features of the admin.jsp, but more generally, it provides a way for
the user to install the APK for the full BeaconCrawl app.

### /src/main/java/com/google/glassware/NewUserBootstrapper.java

Based off the QuickStart, this file initializes a new user and sends appropriate cards and vignettes
to their Glass timeline, including an option to pin the app, so it's always easy to find.

### /src/main/webapp/static/beaconscan.apk

The APK for the full BeaconCrawl app. This app, written by the Skylight1 team, let users know
whether they were in range of the Gimbal(TM) beacons.

More information: https://github.com/skylight1/beaconscan

Part of the purpose of the Mirror API app was to make it easier for people to install the full
BeaconCrawl app, since there was no app store for Google Glass at the time.

### /src/main/webapp/static/images/

Photo overlays for the vignettes are stored here

## License
Code for this project is licensed under [APL 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
and content is licensed under the
[Creative Commons Attribution 3.0 License](http://creativecommons.org/licenses/by/3.0/).