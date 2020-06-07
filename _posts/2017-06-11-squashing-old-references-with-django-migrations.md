---
layout: post
title: Squashing Old References with Django Migrations
---

The other day I found a small typo in a function I was referencing in
the `upload_to` field option in one of my models. I fixed the nasty
typo, only to discover I had broken the migrations, as it was also being
referenced in one of them.

According to the [docs][1], we can make use of *squashing* to
remove these old references. Here's how I managed to solve my OCD
inspired issue.

# Making the Changes

Freely apply all the changes that are required, but keep the old references:

{% highlight python %}
from django.db import models


# Old function
def my_supr_function(instance, filename):
    # Move the implementation to the new function
    pass


# Renamed function
def my_super_function(instance, filename):
    # Some advanced code here
    return 2 + 2


class MyModel(models.Model):
    # Update the 'upload_to' reference here
    image = models.ImageField(upload_to=my_super_function)
{% endhighlight %}

# Making the Migrations

Tell Django you want to create a new migration based on these changes:

{% highlight bash %}
$ ./manage.py makemigrations --name rename-function
{% endhighlight %}

# Squashing

We now have a brand new migration which no longer references
`my_supr_function` (the function with the typo). We can know squash our
migrations from the point where it was first referenced, up to this
newly created migration.

{% highlight bash %}
$ ./manage.py squashmigrations my_app 0005 0009
{% endhighlight %}

# Applying migrations

We can't go around life having unimplemented functions whose names have
typos, our end goal here is to get rid of them, but unfortunately there
is one more step required before we can do that.

Go ahead and commit your changes, and apply the migrations on any
production server you have, plus your development machine of course.

{% highlight bash %}
$ ./manage.py migrate
{% endhighlight %}

# Cleaning Up

Once migrations are up to date, it is safe to remove not only the typo
function, but all the squashed migrations as well. Peace at last.

[1]: https://docs.djangoproject.com/en/1.11/topics/migrations/#historical-models
