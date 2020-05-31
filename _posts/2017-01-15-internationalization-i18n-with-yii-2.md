---
title: Internationalization (i18n) with Yii 2
---

Yii 2 provides a very intuitive way to translate your web application,
and it makes it easy for both the developers and the translators, by
keeping the work that has to be done by both parties nicely separated.

This guide will focus on translating messages using PHP files.

## The Workflow

A common workflow when translating a Yii application will fit the
following:

1. Write the custom messages in the corresponding views/controllers.
   These messages are written in a 'source' language (English, by
default).

2. Run the `yii message` command that will catch those messages and
   prepare a translation document for each language, which consists of a
PHP file that returns an array of key-value pairs.

3. Edit the previously generated file, by writing a translation of each
   message (key) to the language of your choice (value).

This way, all the developer has to worry about is writing the default
messages once, focusing more on his application and functionality, and
tackle the translations later on.

## Getting Started

We need to add the `i18n` application component to our `config/web.php`
configuration file. Here, we'll define the different translations that
`i18n` will process. Here's an example:

{% highlight php %}
<?php
'i18n' => [
    'translations' => [
        'app*' => [
            'class' => 'yii\i18n\PhpMessageSource',
            'sourceLanguage' => 'en-US',
            'basePath' => '@app/messages',
            'fileMap' => [
                'app' => 'app.php',
                'app/error' => 'error.php',
                'app/other' => 'other.php',
            ],
        ],
    ],
],
{% endhighlight php %}

In the above code, the `app*` key represents, in layman's terms, a
'translation category'. Every message that falls within that category
will receive a certain configuration. The `*` character let's this
configuration catch all categories that start with `app`. This allows us
to define sub-categories, such as `app/error` or `app/other`, thus,
keeping things modular.

`class` is the PHP class that will handle the translations.
`yii\i18n\PhpMessageSource` lets us write them as PHP files.

`sourceLanguage` indicates what language the messages are written in. In
this case, it's `en-US`, the locale that corresponds to English and
United States.

`basePath` is the directory where all the translation documents are
located. As you can see, the alias `@app` keeps the path short.

Finally, `fileMap` associates each sub-category with a translation
document, avoiding long, monolithic files.

## Writing the Messages

In order to let Yii know which messages need to be translated, we'll
have to make use of the [`Yii::t()`](1) method:

{% highlight php %}
<?php
// 'Hello, World!'
echo Yii::t('app', 'Hello, World!');

// 'Something went wrong! (Error: Some error message)'
echo Yii::t('app/error',
    'Something went wrong! (Error: {error})',
    ['error' => $exception->getMessage()]
);

/*  When count returns 1:   '1 File uploaded successfully'
    Otherwise:              '# Files uploaded successfully' */

echo Yii::t('app/other',
    '{count} {count, plural, =1{File} other{Files}} uploaded successfully',
    ['count' => count(attachments)]
)
{% endhighlight %}

The previous examples show just how useful this method is. You can use
parameters to format variable information, and customize the messages as
you like.

## The Configuration File

The next step is generating a configuration file for the `yii message`
command. The `yii message` command is the one in charge of examining the
whole project in search for messages inside the `Yii:t()` method, and
prepare a translation document.

Use the `yii message/config` command to generate a new configuration
file, passing the necessary options, and a path where to store it.
Here's an example:

{% highlight shell %}
$ ./yii message/config --languages=es-CR --sourcePath=@app --messagePath=messages config/config.php
{% endhighlight %}

This will dynamically generate a `config.php` file inside the `config/`
directory. Here is how it looks:

{% highlight php %}
<?php
return [
    'sourcePath' => '@app',
    'messagePath' => 'messages',
    'languages' => [
        'es-CR',
    ],
    'translator' => 'Yii::t',
    'sort' => true,
    'overwrite' => true,
    'removeUnused' => true,
    'markUnused' => true,
    'except' => [
        '.svn',
        '.git',
        '.gitignore',
        '.gitkeep',
        '.hgignore',
        '.hgkeep',
        '/messages',
        '/BaseYii.php',
    ],
    'only' => [
        '*.php',
    ],
    'format' => 'php',
];

/* I have removed the database settings since I'm not using a database to store
translations */
{% endhighlight %}

Most of these settings are very self-explanatory, but let's focus on the
most relevant ones: `sourcePath`, `messagePath`, and `languages`:

1. `sourcePath`: Where Yii will start searching for messages. In my
   case, it's the root directory of the application.

2. `messagePath`: The folder where the language directories are stored.

3. `languages`: What languages the application will support. Each
   language gets its own dedicated directory, inside `messagePath`,
where all the translation files will be located.

## Generating the Translation Files

Once the configuration file is ready, it's time to generate a set of
translation files for each language.

Before doing that, however, it's crucial that you manually create the
directory you defined in `messagePath`, because the command we are about
to execute does not automatically create that folder:

{% highlight shell %}
$ mkdir messages
{% endhighlight %}

With that out of the way, let's execute the `yii message` command.  You
will need to specify the path where the configuration file is located.
Here's an example:

{% highlight shell %}
$ yii message config/config.php
{% endhighlight %}

Once the script has finished, you will end up with a directory hierarchy
similar to this:

```
messages/
└── es-CR
    ├── app
    │   ├── error.php
    │   └── other.php
    ├── app.php
    └── yii.php
```

Where the `app.php` corresponds to a general category where most of the
translations will be located, while the sub-directory `app` is where the
specific translations go (sub-categories).

Let's take a look of one of these files:

{% highlight php %}
<?php
return ['{count} {count, plural, =1{File} other{Files}} uploaded successfully' => '',];
{% endhighlight %}

This translation file corresponds to the sub-category app/other, which
only holds one message. As you can see, the source language is paired
with an empty value. This empty value it's the translation. I'm gonna go
ahead and write a Spanish translation for it. Also, please notice how
the string formatting feature I wrote about early in this guide is still
present here.

{% highlight php %}
<?php
return [
    '{count} {count, plural, =1{File} other{Files}} uploaded successfully' =>
    '{count} {count, plural, =1{Archivo subido} other{Archivos subidos}} con exito',
];
{% endhighlight %}

Now, the application translation is ready. We still need to define a
language in our application:

{% highlight php %}
<?php
Yii::$app->language = 'es-CR';
{% endhighlight %}

And just like that, we've successfully translated a Yii application to
Spanish.  Now, if we add/edit/delete messages to our code, all we need
to do is call the `yii message` command again so it can update the
translation files to reflect the changes. The configuration file can be
set to delete unused translations.

## The Result

Here are some screenshots displaying how a translated application looks
like:

### English

![example-english][2]

### Spanish

![example-spanish][3]

Do notice how Yii does have a Spanish translation for the `GridView`
component.  That is because Yii provides more than 30 different language
translations for it's own components. You can check the list of
available languages inside the `vendor/yiisoft/yii2/messages/`
directory.

## Conclusion

The Yii 2 Framework makes dealing with translations quite easy, and
provides ways to organize everything in an elegant way. It just takes a
few minutes to configure everything, and after that, it's just a matter
of executing a simple command, and get on with the translations.

Don't forget to check the [official Yii 2 guide on
internationalization][4], which expands even more on this matter. Also,
check out this other great [tutorial][5] on the same topic.

I hope that this guide has helped you understand how does i18n works in Yii 2.

[1]: http://www.yiiframework.com/doc-2.0/yii-baseyii.html#t%28%29-detail
[2]: https://i.imgur.com/DXqt3d6.png
[3]: https://i.imgur.com/jPA1te7.png
[4]: http://www.yiiframework.com/doc-2.0/guide-tutorial-i18n.html
[5]: https://code.tutsplus.com/tutorials/how-to-program-with-yii2-localization-with-i18n--cms-23140
