---
layout: post
title: How to Install Oracle JDK in Ubuntu
---

I've been a Windows user almost all my life, mainly because I didn't know
better, and I wasn't into computers either until later on. When I made the
switch to Ubuntu, one of the pet peeves I had was installing Java. Sure,
installing OpenJDK is very easy, since there's already a package for it, but
what if you need Oracle's?

There are a couple different ways of doing it, but so far this is my preferred
one. It involves 3 steps:

1. Download the binaries directly from Oracle's website
2. Place the binaries in the appropriate directory
3. Set `JAVA_HOME` and `PATH` accordingly

Ok, let's begin.

## Step 1: Downloading the Binaries

Head over to Oracle's website and download the JDK. Remember to [verify the
checksum][1]!

## Step 2: Choosing the Appropriate Directory

What directory is appropriate for a system-wide installation of Java? According
to [FHS][2] `opt/` fits just well. Go ahead and extract the contents of the
downloaded archive there.

## Step 3: Setting the Environment Variables

Where do we declare `JAVA_HOME` and modify our `PATH`? It can't be in
`~/.bashrc` or `~/.profile` as it's single user only and limited to a CLI shell.
What about a graphical shell?

I've found that the best location to set up environment variables is in
`/etc/profile.d`. According to the [Ubuntu Wiki][3]:

> Files with the .sh extension in the /etc/profile.d directory get executed
> whenever a bash login shell is entered (e.g. when logging in from the console
> or over ssh), as well as by the DisplayManager when the desktop session loads.

Which means the variables will be set in both CLI shells and graphical
environments, which is exactly what we want!

Here's what an `/etc/profile.d/java.sh` example might look like:

{% highlight bash %}
export JAVA_HOME=/opt/jdk1.8.0_161
PATH=$JAVA_HOME/bin:$PATH
{% endhighlight bash %}

That's it! Log out and back in to apply the changes. As usual, run `java
-version` to test if Java was installed correctly:

```
$ java -version
java version "1.8.0_161"
Java(TM) SE Runtime Environment (build 1.8.0_161-b12)
Java HotSpot(TM) 64-Bit Server VM (build 25.161-b12, mixed mode)
```

There you go, happy programming!

[1]: https://itsfoss.com/checksum-tools-guide-linux/
[2]: http://www.pathname.com/fhs/pub/fhs-2.3.html#OPTADDONAPPLICATIONSOFTWAREPACKAGES
[3]: https://help.ubuntu.com/community/EnvironmentVariables#A.2Fetc.2Fprofile.d.2F.2A.sh
