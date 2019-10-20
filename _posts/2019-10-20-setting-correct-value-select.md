---
title: Setting a Correct Value in a select Element
excerpt: ""
---

Setting the value of a `<select>` element it's quite easy:

```javascript
let selectEl = document.querySelector('select#favorite-food');
selectEl.value = 'pizza';
```

Yet there's a particular UX detail that we might have overlooked:
**what if the value is not an existing option?**

# The Problem

Consider an instance where we have to set the value of a `<select>`
programmatically. In my case, I had to preserve the user's previously
selected option right after updating the drop-down list with a new set
of options.

The problem is when the option is not listed (maybe it's no longer
available):

{% include snippets/select.html %}

The `<select>` is obviously left at an invalid state (its value is
`""`), which means existing validation mechanisms still work as
intended. However, we're left with an empty select, there's no
placeholder text to guide the user.

# The Solution

Fortunately the `HTMLSelectElement` interface has a
[`selectedIndex`][selectedIndex] property with a value of `-1` when no
option is selected, which is exactly our case. We can leverage this
property to perform a quick validation:

{% include snippets/select-fixed.html %}

*[UX]: User experience
[selectedIndex]: https://developer.mozilla.org/en-US/docs/Web/API/HTMLSelectElement/selectedIndex