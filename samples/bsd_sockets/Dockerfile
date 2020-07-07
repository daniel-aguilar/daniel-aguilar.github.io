FROM alpine
WORKDIR /usr/local/src
COPY src .
RUN set -ex \
	&& apk add build-base \
	&& gcc server.c -o /usr/local/bin/server
ENTRYPOINT ["server"]
