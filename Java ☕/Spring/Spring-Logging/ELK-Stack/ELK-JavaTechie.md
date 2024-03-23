# ELK Stack in Microservice for Centralized Logging



![image-20240322124243883](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240322124243883.png)



![image-20240322124256126](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240322124256126.png)



![image-20240322124336851](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240322124336851.png)



## Run ELK

Go into the downloaded directory/bin folder :

- elasticsearch: `elasticsearch.bat`

  <blockquote alt = 'purple'>elasticsearch accessing the https://localhost:9200 error: <br><br>1. Start the Elastic Stack with security enabled automatically: (https://www.elastic.co/guide/en/elasticsearch/reference/current/configuring-stack-security.html)<br><br>2. Reset Elasticsearch password: (https://www.elastic.co/guide/en/elasticsearch/reference/current/reset-password.html)

  

- kibana: first go the the config folder and configure the <kbd>kibana.yml</kbd> file with `elasticsearch.hosts: ["http://localhost:9200"]`. Then go to bin folder and `kibana.bat`

   <blockquote alt = 'purple'>kibana UI error:<br>
       go to the elastic bin folder and run: <kbd>elasticsearch-create-enrollment-token.bat --scope kibana</kbd></blockquote>

  

- logstash: first create a `logstash.conf` file in the bin directory of logstash:

  ```yaml
  input {
  
  	file {
  		path => "D:/Spring-boot-Tutorial/source-code/ELK-Stack/ELK-JavaTechie/logs/elk-stack-logs.log"
  		start_position => "beginning"
  	}
  	
  }
  
  output {
  	
  	stdout {
  		codec => rubydebug 
  	}
  	elasticsearch {
  		hosts => ["https://localhost:9200"]
  		user => "elastic"
  		password => "wufZxjUMfypuiF7Htrjd"
  		ssl => true
  		ssl_certificate_authorities => ["C:/Softwares/elasticsearch-8.12.2-windows-x86_64_2/config/certs/http_ca.crt"]
  		index => "demo_index"   #--- Optional: this will create an index for logs
  	}
  	
  }
  ```

  <blockquote alt = 'purple'>Note 💡:<br>Since elastic is secured in TLS/SSL connection, we need the last 4 lines. Otherwise we'll get error saying unable to connect to elastic.<br><br>Reference: https://stackoverflow.com/questions/75394302/logstash-unable-to-reach-elastic-server-unable-to-find-valid-certification-path

  After creating the `logstash.conf` file, run the logstash using: <kbd>.\logstash -f full\path\to\your\logstash.conf</kbd>.

  Go to kibana console:

  ![image-20240322151532578](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240322151532578.png)

  ![image-20240322151544925](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240322151544925.png)

  ![image-20240322151550094](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240322151550094.png)

  And done!

  Now you can view your logs in kibana console discover panel:

  ![image-20240322151939641](C:\Users\aliro\AppData\Roaming\Typora\typora-user-images\image-20240322151939641.png)

  