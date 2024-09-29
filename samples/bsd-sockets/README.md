# BSD Sockets

1. Create a network

    ```
    docker network create sockets-net --subnet 172.18.0.0/16
    ```

2. Build the image

    ```
    docker build -t sockets:latest .
    ```

3. Run a container

    ```
    docker run --network sockets-net --ip 172.18.0.10 -d sockets:latest
    ```

4. Compile the client

    ```bash
    $ gcc client.c -o client
    ```

5. Interact with the server

    ```bash
    $ ./client HEY
    $ ./client HELLO
    HI
    $ ./client BYE
    ```

You may view the server's output via `docker logs`.
