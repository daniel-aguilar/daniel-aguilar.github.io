---
title: cherry-pick Considered Harmful
---

or: How I Learned to Stop cherry-picking and Love the merge

Funny headlines aside, this post is gonna be a small rant as to why I find
cherry-picking particularly annoying when working with [Gitflow][1].

Gitflow is a good idea! I'm just not a huge fan of its ~~horrifying~~ humorous
results in the log, as opposed to a much linear history:

![Humorous Log](https://i.imgur.com/Gh4ELWi.png)

Now, if you couple that with the bad habit of cherry-picking hotfix or bug fix
commits, things start to get a lot messier. So why exactly is cherry-pick a bad
idea? Because **it further obfuscates your commit history**.

Consider the following example: I just committed a change on `hotfix/issue658`,
and I need to apply it to both `master` and `dev`:

![Example 1](https://i.imgur.com/XqmuO1H.png)

Suppose I cherry-pick the resulting commit to both of those branches:

![Example 2](https://i.imgur.com/z16kVHc.png)

It might look OK at first glance, but watch what happens when I try to update
`master` to a new version:

![Example 3](https://i.imgur.com/fQfvMGk.png)

We got duplicated commits! Git treats them as separate units because they each
have different IDs (that's what cherry-pick does, it creates a new commit each
time), thus, my `hotfix` branch it's not merged into any of my main branches.
This commit history does not accurately reflect my original intent.

Contrast that to what a correct merge strategy of the previous example would
look like:

![Example 4](https://i.imgur.com/tf7Eozu.png)

I regard maintaining a reasonably sound commit history as important as writing
good code, given how git can also be used as an auditing tool, believe me, it
can save you a lot of trouble in the long run. Do yourself a favor an try to
avoid cherry-picking as much as you can!

[1]: https://nvie.com/posts/a-successful-git-branching-model/
