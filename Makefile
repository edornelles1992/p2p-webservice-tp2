all:
	@javac Arquivo.java
	@javac DataStore.java
	@javac Peer.java
	@javac ResponseDTO.java
	@javac -cp .:jars/json-20190722.jar:jars/servlet-api.jar:jars/gson-2.8.2.jar Utils.java
	@javac -cp .:jars/json-20190722.jar:jars/servlet-api.jar:jars/gson-2.8.2.jar Servlet.java
	@javac -cp .:jars/json-20190722.jar:jars/servlet-api.jar:jars/gson-2.8.2.jar RestApiClient.java
	
app_install:
	@mkdir jetty/webapps/Servlet
	@mkdir jetty/webapps/Servlet/WEB-INF
	@mkdir jetty/webapps/Servlet/WEB-INF/classes
	@mkdir jetty/webapps/Servlet/WEB-INF/lib
	@cp web.xml jetty/webapps/Servlet/WEB-INF
	@cp *.class jetty/webapps/Servlet/WEB-INF/classes
	@cp ./jars/*.jar jetty/webapps/Servlet/WEB-INF/lib
	
rest_webservice:
	@cd jetty && java -jar start.jar && cd ..
		
clean:
	@rm -rf *.class *~ jetty/webapps/Servlet
	
