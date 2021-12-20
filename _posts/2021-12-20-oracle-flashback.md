---
layout: post
title: Going back in time with Oracle Flashback
---

You ever wished you could take a snapshot of your current Oracle
development database? One that you could easily go back to in case of
trouble. What if you started working on a feature, and accidentally
mess up the business logic integrity in some way? Imagine the
countless hours just trying to revert the damage done only to finally
give up and end up recreating the database from scratch. You ever
wished this could be done in a straightforward way (less than 3
commands)? Me too!

Advancements in container technology have made it unbelievably easy to
spin up a development database in mere seconds. Gone are the days of
pre-packaged, resource-hogging, inflexible virtual machines; or
installing databases locally for a particular project, setting the
correct schema permissions, and dealing with platform-specific issues,
as you spend the rest of your much productive day praying the hard
drive never dies out; or ---worse--- having no alternative other than
to share a single database with your entire development team.

We now have access to hundreds of official, actively maintained Docker
images of our favorite databases, from Postgres to SQL Server. They're
all lighting fast and fully extensible, featuring sensible defaults
(e.g. correct file system structure, proper user/group permissions),
mappable ports, persistent volumes, among other great capabilities.

Containers help speed up the development process by providing
consistent, predictable & easily reproducible environments to work
with. It's in this same spirit that today I bring you yet another tool
you can add to your belt: Oracle's [Flashback Database][1].

# What is Flashback Database?

Flashback Database is a neat feature of Oracle Database that lets you
rewind your database to a defined point in time. Think of it as a
`ROLLBACK` statement, but without the need of a transaction, and
working at a much deeper level, as it deals with data files. What this
means is that any new rows added to any table (or any new table
added), sequences incremented, or modified stored procedures can be
sent back in time to a pristine state. It's flippin' cool.

# What are the use cases?

You may think this is a feature a DBA would me most concerned with,
but think again!

I use flashbacks to rollback undesired changes on my local database,
introduced by new features I'm working on. This is specially useful
when working with a relatively complex (100+ objects) database.

Perhaps I accidentally corrupted a specific row with invalid (i.e. not
according to the business logic) data; or I suddenly have to switch
branches and don't wanna be left with a bunch of test data, or
modified column datatypes that I can't ORM'd my classes to; or maybe
there's a painfully elaborate domain logic process I have to perform
within the application multiple times to get the correct data to work
with.

It's a game of do and re-do. I can safely and confidently move my way
around the database with a big *undo* button in my hand ready to be
pressed at any time I need. This sort of flexibility, I believe,
speeds up the development cycle considerably.

# Getting Started

As it is the case with most of my articles, this is a tutorial with a
hands-on approach. If you have [Docker][2] installed, you may follow
along with me. I'll be demonstrating how flashback database works
using this [sample project][3], which has all of the configuration and
scripts you will need.

I'll be working with release 19c, but this tutorial also applies to
version 12c. There's a caveat tho: **flashback database is only
available on Oracle Database Enterprise Edition**.

Oracle provides official Dockerfiles that facilitate the installation
and configuration of an Oracle Database. Head over to their [GitHub
repository][4], and follow the instructions to build a local oracle
database image (SingleInstance).

# Running the Sample Project

Once you have an Oracle Database image ready (in this case named
`oracle/database:19.3.0-ee`), you can execute the `init.sh`
script. This starts an Oracle Database container, automatically
configures flashback database (which is not enabled by default),
creates a pluggable database with a test user and a couple of tables,
then finally creates a restore point. Here's what we'll be working
with:

![Database Design](https://i.imgur.com/yFSmmlw.png)

You can connect to the database using the user `scott`, password
`tiger`. The hostname would be the IP address that has been assigned
to your container, which you can find out what it is via `docker
inspect oracle-db`. The port and service name correspond to 1521 and
`LOCALPDB` respectively.

{% highlight bash %}
$ docker inspect oracle-db | grep IPAddress
            "SecondaryIPAddresses": null,
            "IPAddress": "172.17.0.2",
                    "IPAddress": "172.17.0.2",
{% endhighlight %}

# Making Changes

You can query existing restore points with the following statement:
`SELECT * FROM V$RESTORE_POINT;`. If you run the previous command,
you'll find out the `BEFORE_FEATURE` restore point exists, which means
we can safely start doing some changes. The `feature.sql` script
includes the following:

{% highlight sql %}
INSERT INTO guitar (brand_id, model) VALUES (2, 'Les Paul');
CREATE TABLE guitar_catalog (
    id NUMBER GENERATED AS IDENTITY,
    guitar_id NUMBER,
    quantity NUMBER,
    CONSTRAINT guitar_catalog_fk
        FOREIGN KEY (guitar_id) REFERENCES guitar (id)
);
{% endhighlight %}

We may now drop tables, insert hundreds of new rows, create sequences,
you get the idea. Naturally, there's so much data Oracle can hold
about the new state of the database (10GB in this case, but it can be
configured), so I wouldn't recommend abusing this feature too
much. Remember, this is a development database after all.

# Performing the Flashback

To perform a flashback, we can connect directly to our database via
RMAN (Recovery Manager), using OS authentication. Start by executing a
shell in the running container:

{% highlight bash %}
$ docker exec -it oracle-db bash
$ export ORACLE_SID=ORCLCDB
$ export ORACLE_HOME=/opt/oracle/product/19c/dbhome_1
$ rman target /
{% endhighlight %}

At this point, it's only a matter of closing the pluggable database
(PDB), perform the actual flashback, and finally reopening the PDB
(with the `RESETLOGS` option):

{% highlight sql %}
ALTER PLUGGABLE DATABASE LOCALPDB CLOSE IMMEDIATE;
FLASHBACK PLUGGABLE DATABASE LOCALPDB TO RESTORE POINT BEFORE_FEATURE;
ALTER PLUGGABLE DATABASE LOCALPDB OPEN RESETLOGS;
{% endhighlight %}

And that's it! The flashback is now complete, and you can login with
`scott` again to check the current state of the database. Everything
should be how we initially started.

# Conclusion

This isn't by any means a guide to database flashbacks at all, far
from it. This is a demonstration of a useful feature you can apply to
your daily software development work. I obviously had to skim over so
many details, (such as undoing a flashback using `RECOVER`), but
hopefully you could get a general idea of what this is all about!

If you'd like to learn more, you can visit the [official
reference][1], which has a more in-depth explanation of what is going
on, plus some other great examples.

[1]: https://docs.oracle.com/en/database/oracle/oracle-database/19/rcmrf/FLASHBACK-DATABASE.html
[2]: https://www.docker.com/
[3]: https://github.com/daniel-aguilar/daniel-aguilar.github.io/tree/master/samples/oracle-flashback
[4]: https://github.com/oracle/docker-images
