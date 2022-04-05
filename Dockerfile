FROM tomcat:9.0.56-jdk8-openjdk 
#8.5.50-jdk8-openjdk

ARG WAR_FILE 
ARG CONTEXT 

COPY ${WAR_FILE} /usr/local/tomcat/webapps/${CONTEXT}.war