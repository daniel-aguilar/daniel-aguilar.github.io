---
layout: post
title: "Dockerfile: CMD vs. ENTRYPOINT"
---

Docker provides a couple of instructions to specify what a container
does when you want to `docker run` it: `CMD` and `ENTRYPOINT`.

These two provide a somewhat similar functionality but are actually
quite different fundamentally. According to the [Dockerfile
reference][1], `CMD`'s entire purpose is to provide **defaults** for an
executing container, while `ENTRYPOINT`'s is being concerned with the
**configuration** of said executing container.

That's their main purpose. However, depending on the combination of this
two instructions, we can achieve different use cases.

## CMD

Let's take a look at [Debian Stretch Slim's Dockerfile][2]:

```
FROM scratch
ADD rootfs.tar.xz /
CMD ["bash"]
```

Notice how there's no `ENTRYPOINT`. When only `CMD` is present, the
default behaviour will be to execute whatever is specified by `CMD`,
bash in this case.

So, when I `docker run debian:stretch-slim`, the container will run bash
and exit.

Wouldn't `ENTRYPOINT` achieve the same thing? Why do we need `CMD` for
then? Well, `CMD` has a special property, something I like to call *soft
exec*, which is basically the ability to override the command. Take
a look:

```
$ docker run debian:stretch-slim echo Hello!
Hello!
```

The container executed `echo Hello!` instead of the default command.

## ENTRYPOINT

Let's contrast the previous behaviour with an `ENTRYPOINT` instruction.
Using the following Dockerfile:

```
FROM debian:stretch-slim
ENTRYPOINT ["echo" "Hello,"]
```

I'm gonna build a `entrypoint-test` image and run it:

```
$ docker run entrypoint-test
Hello,
```

Okay, so far things look samey. But watch this!

```
$ docker run entrypoint-test World!
Hello, World!
```

I couldn't override the command this time! Instead, the container used
it as `echo`'s input. This is what I call the *hard exec*: `ENTRYPOINT`
will always be executed, regardless.

## ENTRYPOINT + CMD

As I mentioned, `CMD` provides default arguments to `ENTRYPOINT`.
Consider the following Dockerfile:

```
FROM debian:stretch-slim
CMD ["World!"]
ENTRYPOINT ["echo", "Hello,"]
```

Having built it as `entrypoint-cmd-test`, let's run it:

```
$ docker run entrypoint-cmd-test
Hello, World!
```

`CMD` provided the default *World!* argument to echo. What if we pass an
argument of our own?

```
$ docker run entrypoint-cmd-test Daniel!
Hello, Daniel!
```

We have successfully overridden the default parameters!

## Conclusion

Both `CMD` & `ENTRYPOINT` bring their own stuff to the table, but at the
end of the day, like most things, it's up to you to decide which use
case fits your needs! I hope this has helped clear up the confusion!

[1]: https://docs.docker.com/engine/reference/builder/
[2]: https://github.com/debuerreotype/docker-debian-artifacts/blob/42bec5bc2f5a76ceeb125bc4e66d6f70a95e933f/stretch/slim/Dockerfile
