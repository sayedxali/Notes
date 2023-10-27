# Spring WebSockets

Official
Documentation : [Spring framework - WebSockets](https://docs.spring.io/spring-framework/reference/web/websocket.html)

Official Documentation -
Reactive : [Spring framework - WebSockets : Reactive Stacks](https://docs.spring.io/spring-framework/reference/web/webflux-websocket.html#webflux-websocket-server)

<!-- TOC -->
* [Spring WebSockets](#spring-websockets)
  * [Introduction to WebSocket](#introduction-to-websocket)
  * [HTTP Versus WebSocket](#http-versus-websocket)
  * [When to Use WebSockets](#when-to-use-websockets)
  * [Advantages of WebSockets](#advantages-of-websockets)
  * [Let's Build a Simple Chat-application](#lets-build-a-simple-chat-application)
<!-- TOC -->

## Introduction to WebSocket

The WebSocket protocol, RFC 6455, provides a standardized way to establish a full-duplex, two-way communication channel
between client and server over a single TCP connection. It is a different TCP protocol from HTTP but is designed to work
over HTTP, using ports 80 and 443 and allowing re-use of existing firewall rules.

A WebSocket interaction begins with an HTTP request that uses the HTTP Upgrade header to upgrade or, in this case, to
switch to the WebSocket protocol. The following example shows such an interaction:

```yaml
GET /spring-websocket-portfolio/portfolio HTTP/1.1
Host: localhost:8080
Upgrade: websocket # The Upgrade header.
Connection: Upgrade # Using the Upgrade connection.
Sec-WebSocket-Key: Uc9l9TMkWGbHFD2qnFHltg==
Sec-WebSocket-Protocol: v10.stomp, v11.stomp
Sec-WebSocket-Version: 13
Origin: http://localhost:8080
```

Instead of the usual 200 status code, a server with WebSocket support returns output similar to the following:

```yaml
HTTP/1.1 101 Switching Protocols # Protocol switch
Upgrade: websocket
Connection: Upgrade
Sec-WebSocket-Accept: 1qVdfYHU9hPOl4JYYNXF623Gzn0=
Sec-WebSocket-Protocol: v10.stomp
```

After a successful handshake, the TCP socket underlying the HTTP upgrade request remains open for both the client and
the server to continue to send and receive messages.

A complete introduction of how WebSockets work is beyond the scope of this document. See RFC 6455, the WebSocket chapter
of HTML5, or any of the many introductions and tutorials on the Web.

Note that, if a WebSocket server is running behind a web server (e.g. nginx), you likely need to configure it to pass
WebSocket upgrade requests on to the WebSocket server. Likewise, if the application runs in a cloud environment, check
the instructions of the cloud provider related to WebSocket support.

## HTTP Versus WebSocket

Even though WebSocket is designed to be HTTP-compatible and starts with an HTTP request, it is important to understand
that the two protocols lead to very different architectures and application programming models.

In HTTP and REST, an application is modeled as many URLs. To interact with the application, clients access those URLs,
request-response style. Servers route requests to the appropriate handler based on the HTTP URL, method, and headers.

By contrast, in WebSockets, there is usually only one URL for the initial connect. Subsequently, all application
messages flow on that same TCP connection. This points to an entirely different asynchronous, event-driven, messaging
architecture.

WebSocket is also a low-level transport protocol, which, unlike HTTP, does not prescribe any semantics to the content of
messages. That means that there is no way to route or process a message unless the client and the server agree on
message semantics.

WebSocket clients and servers can negotiate the use of a higher-level, messaging protocol (for example, STOMP), through
the Sec-WebSocket-Protocol header on the HTTP handshake request. In the absence of that, they need to come up with their
own conventions.

## When to Use WebSockets

WebSockets can make a web page be dynamic and interactive. However, in many cases, a combination of AJAX and HTTP
streaming or long polling can provide a simple and effective solution.

For example, news, mail, and social feeds need to update dynamically, but it may be perfectly okay to do so every few
minutes. Collaboration, games, and financial apps, on the other hand, need to be much closer to real-time.

Latency alone is not a deciding factor. If the volume of messages is relatively low (for example, monitoring network
failures) HTTP streaming or polling can provide an effective solution. It is the combination of low latency, high
frequency, and high volume that make the best case for the use of WebSocket.

Keep in mind also that over the Internet, restrictive proxies that are outside of your control may preclude WebSocket
interactions, either because they are not configured to pass on the Upgrade header or because they close long-lived
connections that appear idle. This means that the use of WebSocket for internal applications within the firewall is a
more straightforward decision than it is for public facing applications.

Some use-cases :

- Chat Applications
- Real-Time Gaming
- Collaborative Editing
- Live Updates
- IoT (Internet of Things)

## Advantages of WebSockets

- Real time Data Transfer
- Bi-Directional
- Efficient
- Persistent Connection

## Let's Build a Simple Chat-application

This chat application is just a demonstration of websockets, it is not production ready. You
can use this as reference and build something else out of it; like push notification!

In this example we have both message to everyone and message to specific user which can be seen
only when the user has logged in.

The code source : [chat-application-demo](./code_sources/src)

> 💡 Note : this is in spring-boot 2.7.x version. It'll be different in spring-boot 3.x since the
> security configuration is different. Other than that, everything is same.

> 💡 We're using `SockJS` and `StompJS` CDNs for the javascript to connect to the websocket.

> 💡 Also check the resources folder since the html is also important! 