---
layout: post
title: Introduction to BSD Sockets
---

The operating system truly makes developers' jobs much more easier: we don't
have to worry about the boring details, and can focus on what our programs do
instead. Keeping with the same philosophy of abstracting the details, and thanks
to the people at UC Berkeley, communicating over a network isn't that much
different as writing some data to a file.

The example I'll be working on will be the classic client-server model, using
IPv4. The server will be listening for messages (printing them out as they come)
and react accordingly, while the client sends messages and outputs responses
from the server. The server runs in a Docker container in its own network. You
can find the example [here][1].

The example is written in C. Most modern programming languages provide support
for (the now called low-level) sockets, but what better way to learn other than
using the original implementation?

## The Basics

The original implementation of the TCP/IP stack for Unix-like systems appeared
in 4.2BSD, it was later adopted as a POSIX standard. As I mentioned, it was
written in C, and introduced the concept of the socket---an endpoint for
communication between processes.

There are many *domains* of sockets (also known as *families*), but the 2 most
popular you'll encounter are *UNIX domains* and *Internet protocols*. While I'll
be discussing the Internet protocols family of sockets in this article, UNIX
domain sockets are not that different. They're used for efficient local
communication (on the same machine). The Docker daemon, for instance, uses this
type of socket.

Sockets also have *types*. I'll be working with stream-type sockets
(`SOCK_STREAM`), corresponding to a TCP socket.

### The Telephone Analogy

A well-known way of explaining sockets is using the telephone analogy. In order
to receive a call, you must first install a phone, have a phone number, hear it
ring, and finally take the call. Generally, you do not need to know the caller's
number.

To make a phone call, the process is somewhat the same in that you still need a
phone, but instead of passively listen for incoming calls, you dial a number
instead, and wait for someone to answer.

Sockets work like telephones. On the server side, you create a `socket()`,
`bind()` a local address to it, `listen()` for connections, and finally
`accept()` one. On the client side, you create a `socket()` and `connect()` to
an address.

## Working with Addresses

While we can work with dot-decimal notation IP addresses (e.g. 192.168.1.110),
ultimately they have to be converted to binary form (network byte order). Both
`bind()` and `connect()` have a `sockaddr` argument, a struct that holds data
about an address. You will see something like this in the client and server
code:

{% highlight c %}
struct sockaddr_in address;
struct in_addr ip;

inet_aton("192.168.1.110", &ip);
address.sin_addr = ip;
address.sin_port = htons(8080);
address.sin_family = AF_INET;
{% endhighlight %}

We can use the `inet_aton()` and `htons()` utilities to convert such
values. Finally, we need to specify what kind of address we're talking
about. The address family for IPv4 Internet protocols is `AF_INET`.

## Server Code

This server will implement a simple protocol consisting of two commands: `HELLO`
and `BYE`. If the client sends a `HELLO` message, the server then replies with
"HI", while a `BYE` message instructs the server to shut down. All messages
received must be sent to stdout.

{% highlight c linenos %}
// server.c stub
char buffer[12];
int data_socket;
int conn_socket = socket(AF_INET, SOCK_STREAM, 0);
bind(conn_socket, (const struct sockaddr *) &address,
     sizeof(struct sockaddr_in));
listen(conn_socket, 5);

for (;;) {
	data_socket = accept(conn_socket, NULL, NULL);
	read(data_socket, buffer, 12);
	buffer[11] = '\0';
	puts(buffer);

	if (strcmp(buffer, "HELLO") == 0) {
		strcpy(buffer, "HI\n");
		write(data_socket, buffer, 12);
	}

	close(data_socket);
	if (strcmp(buffer, "BYE") == 0) {
		break;
	}
}

close(conn_socket);
{% endhighlight %}

Assuming `address` has been correctly assigned, line 4 creates an IPv4
*listening* socket. I say listening because once we accept a connection, a new
socket is created, so as to support multiple concurrent connections. Line 5
binds the previous socket to an address, while line 7 configures the socket to
listen for up to 5 pending connections in a queue.

The main loop consists of accepting a new connection, reading 12 bytes from the
socket, implement a HELLO protocol, and then close the data socket. If a `BYE`
command is received, the listening socket is closed too.

If you've worked with file I/O in C before, you'll notice the process is
essentially the same. `data_socket` is a file descriptor and so the standard
`read()` and `write()` operations work as they would with any other file.

## Client Code

{% highlight c %}
// client.c stub
char response[12];

int fd = socket(AF_INET, SOCK_STREAM, 0);
connect(fd, (const struct sockaddr *) &address,
        sizeof(struct sockaddr_in));

write(fd, "HELLO", 5);
read(fd, response, 12);
printf(response);
close(fd);
{% endhighlight %}

The client can be significantly smaller as it only has one task. Again, here I'm
assuming `address` its been properly assigned. In this particular instance I am
expected to received a "HI" response from the server.

## Conclusion

I hope this explanation was clear. I tried to keep the code concise by removing
includes, error handling noise and other off topic details, but I encourage you
to download the [full example][1] and try it yourself.

[1]: https://github.com/daniel-aguilar/daniel-aguilar.github.io/tree/blog/samples/bsd_sockets
