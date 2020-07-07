#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/socket.h>
#include <netinet/ip.h>
#include <arpa/inet.h>
#include "conn.h"

int main(int argc, char *argv[])
{
	struct sockaddr_in address;
	struct in_addr ip;
	int fd;

	char response[BUFFER_SIZE];
	int ret;

	if (argc < 2) {
		fprintf(stderr, "argc: Expected one argument\n");
		exit(EXIT_FAILURE);
	}

	inet_aton(IP_ADDR, &ip);
	address.sin_family = AF_INET;
	address.sin_port = htons(PORT_NUM);
	address.sin_addr = ip;

	fd = socket(AF_INET, SOCK_STREAM, 0);
	ret = connect(fd, (const struct sockaddr *) &address,
		      sizeof(struct sockaddr_in));
	if (ret == -1) {
		perror("connect");
		exit(EXIT_FAILURE);
	}

	write(fd, argv[1], strlen(argv[1]) + 1);
	read(fd, response, BUFFER_SIZE);
	printf(response);
	close(fd);
	return EXIT_SUCCESS;
}
