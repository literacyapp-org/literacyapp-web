WARFILE=/var/lib/jenkins/workspace/webapp-2-precompile-jsps/target/webapp-SNAPSHOT.war
PROD_SERVER=eng.elimu.ai
echo "Copying latest WAR file to prod server '$PROD_SERVER'... ($WARFILE)"
echo "Time stamp: $(stat -c %y $WARFILE)"

WARFILE_PROD=/tmp/webapp-SNAPSHOT.war
scp $WARFILE root@$PROD_SERVER:$WARFILE_PROD
echo "Copy complete"

ssh root@$PROD_SERVER echo "JETTY_HOME: $JETTY_HOME"
ssh root@$PROD_SERVER echo "JAVA_OPTIONS: $JAVA_OPTIONS"
ssh root@$PROD_SERVER echo "Stopping prod server..."
ssh root@$PROD_SERVER $JETTY_HOME/bin/jetty.sh stop
ssh root@$PROD_SERVER echo "Deploying WAR file: $WARFILE_PROD $(stat -c %y $WARFILE)"
ssh root@$PROD_SERVER cp $WARFILE_PROD $JETTY_HOME/webapps/webapp.war
ssh root@$PROD_SERVER echo "Starting prod server..."
ssh root@$PROD_SERVER 'export GOOGLE_APPLICATION_CREDENTIALS="/path/to/google-cloud-service-account-key.json"; echo GOOGLE_APPLICATION_CREDENTIALS: \"${GOOGLE_APPLICATION_CREDENTIALS}\"; '"'${JETTY_HOME}'"'/bin/jetty.sh start'

echo "Deploy complete"
