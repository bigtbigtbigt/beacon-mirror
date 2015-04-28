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

 sudo mvn compiler:compile
 sudo mvn war:war | tee /dev/tty
 sudo cp /web/beacon-mirror/target/beacon-crawl-0.1-SNAPSHOT.war /var/lib/tomcat7/webapps/ROOT.war

(I chained these together in .bash_profile with the command "deploy" as follows)

 alias deploy="cd /web/beacon-mirror/ ; sudo mvn compiler:compile ; sudo mvn war:war | tee /dev/tty ; sudo cp /web/beacon-mirror/target/beacon-crawl-0.1-SNAPSHOT.war /var/lib/tomcat7/webapps/ROOT.war"

## Key files

### beacon-mirror-glassware/MainServlet.java

### admin.jsp

### index.jsp

### NewUserBootstrapper.java

### beaconscan.apk

The APK for the full Beacon Crawl app. This app, written by the Skylight1 team, let users know 
whether they were in range of the Gimbal(TM) beacons.

### /src/main/java/com/google/glassware/AuthServlet.java

### /src/main/java/com/google/glassware/ReauthFilter.java

### /src/main/java/com/google/glassware/AuthFilter.java

### /src/main/webapp/WEB-INF/web.xml

### static/images

Photo overlays are stored here

## License
Code for this project is licensed under [APL 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
and content is licensed under the
[Creative Commons Attribution 3.0 License](http://creativecommons.org/licenses/by/3.0/).