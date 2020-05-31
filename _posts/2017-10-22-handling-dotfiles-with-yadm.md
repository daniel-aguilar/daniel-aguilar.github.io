---
title: Handling dotfiles with YADM
---

Nothing makes me feel more at `$HOME` than seeing my dotfiles in place.

Most people keep them in sync via a git repository, usually GitHub, which is
great because it encourages sharing. I like to look through those repositories
to seek inspiration (I'm also just curious).

I used to keep them in sync using [syncthing](https://syncthing.net/), but my
current setup was limited to LAN only. I needed something that could work over
the internet.

I must admit I was a bit skeptic about using a git repository because it meant
creating a script to symlink the files, figuring out a way to handle different
environments like macOS and Ubuntu, and manually transfer SSH keys.

But then I discovered [YADM: Yet Another Dotfiles
Manager](https://thelocehiliosan.github.io/yadm/), and I was blown away. It
featured exactly everything I needed in a dotfiles manager:

- Git under the hood
- No symlinking required, as your `$HOME` is the working directory
- File encryption (via GnuPG)
- Alternate files based on the OS

With YADM, you only add the files you need synced, and because it's a glorified
git wrapper, all the git commands stay the same.

Perhaps my favorite feature is encryption, and it's super easy to use. Simply
create a `~/.yadm/encrypt` file, and write some glob patterns matching the files
you want encrypted so they're not stored as plain text. For instance:

```
.config/secrets
.ssh/*
!.ssh/authorized_keys
!.ssh/known_hosts*
```

Then, encrypt the files with: `yadm encrypt`. This will create a bundle called
`~/.yadm/files.gpg`, which you can now add to the repo. If you'd like to decrypt
the files, `yadm decrypt` does the job.

I highly recommend this tool, I think it's an elegant way to handle dotfiles.
