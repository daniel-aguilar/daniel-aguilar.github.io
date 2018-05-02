---
title: "Using vimrc Files per Directory"
---

How many times have you wished you could have a different vimrc
configuration per directory? I know I have, many times! Specially when
you work on different projects which might require different settings.
For instance, I like to enable spell checking when working on my school
notes, but I don't wanna `set spell spelllang=es,en` every time I open
a file for editing.

Introducing [Localvimrc][1] which comes to solve that exact problem!
Simply keep a `.lvimrc` (as per defaults) in the directory you want to
apply the settings in and voilà!

Each time you open a file in which a `.lvimrc` file is present (from the
root directory to the current directory), vim will prompt you asking if
you wanna apply the settings.

This is a nice way to keep your global vimrc clean and generic, while
fulfilling individual requirements at the same time!

[1]: https://github.com/embear/vim-localvimrc
