# Use official Tomcat base image
FROM tomcat:10.1-jdk17

# Remove default webapps (optional)
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy WAR (admin or user WAR will be copied depending on the build context)
ARG WAR_FILE
COPY target/${WAR_FILE} /usr/local/tomcat/webapps/ROOT.war

# Expose Tomcat port
EXPOSE 8080

CMD ["catalina.sh", "run"]

