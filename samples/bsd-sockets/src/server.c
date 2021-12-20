#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/socket.h>
#include <netinet/ip.h>
#include <arpa/inet.h>
#include "conn.h"

int main()
{
	int conn_socket, data_socket;
	struct sockaddr_in address;
	struct in_addr ip;

	char buffer[BUFFER_SIZE];
	int ret;

	inet_aton(IP_ADDR, &ip);
	address.sin_family = AF_INET;
	address.sin_port = htons(PORT_NUM);
	address.sin_addr = ip;

	conn_socket = socket(AF_INET, SOCK_STREAM, 0);
	ret = bind(conn_socket, (const struct sockaddr *) &address,
		   sizeof(struct sockaddr_in));
	if (ret == -1) {
		perror("bind");
		exit(EXIT_FAILURE);
	}
	ret = listen(conn_socket, 5);
	if (ret == -1) {
		perror("listen");
		exit(EXIT_FAILURE);
	}

	for (;;) {
		data_socket = accept(conn_socket, NULL, NULL);
		read(data_socket, buffer, BUFFER_SIZE);
		buffer[BUFFER_SIZE - 1] = '\0';
		puts(buffer);
		fflush(stdout); // Prevent stdout buffering

		if (strcmp(buffer, "HELLO") == 0) {
			strcpy(buffer, "HI\n");
			write(data_socket, buffer, BUFFER_SIZE);
		}

		close(data_socket);
		if (strcmp(buffer, "BYE") == 0) {
			break;
		}
	}

	close(conn_socket);
	return EXIT_SUCCESS;
}
