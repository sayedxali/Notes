Important classes: 

[CustomBearerTokenAccessDeniedHandler](./Task2-SecurityAuditing/src/main/java/com/seyed/ali/task2securityauditing/config/security/jwt/CustomBearerTokenAccessDeniedHandler.java): For logging the activity of user when not authentication is provided in the request.

[AuditLogger](./Task2-SecurityAuditing/src/main/java/com/seyed/ali/task2securityauditing/config/audit/AuditLogger.java): This class listens to all the events, and from this class we can save the logs and create the logs.

<blockquote alt = 'purple'> Note 💡:<br>The `AuditLogger` class is one way of logging. We could have logged from the `EntryPoint` and `Handler` classes.</blockquote> 


