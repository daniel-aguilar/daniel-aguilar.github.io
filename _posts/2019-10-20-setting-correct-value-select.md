---
title: Setting a Correct Value in a select Element
excerpt: ""
---

<script>
  function choosePizza() {
    let selectEl = document.getElementById('sel-1');
    selectEl.value = 'pizza';
  }

  function choosePizzaFixed() {
    let selectEl = document.getElementById('sel-2');
    selectEl.value = 'pizza';

    if (selectEl.selectedIndex === -1) {
      selectEl.value = '';
    }
  }
</script>

Setting the value of a `<select>` element it's quite easy:

{% highlight javascript %}
let selectEl = document.querySelector('select#favorite-food');
selectEl.value = 'pizza';
{% endhighlight %}

Yet there's a particular UX detail that we might have overlooked:
**what if the value is not an existing option?**

# The Problem

Consider an instance where we have to set the value of a `<select>`
programmatically. In my case, I had to preserve the user's previously
selected option right after updating the drop-down list with a new set
of options.

The problem is when the option is not listed (maybe it's no longer
available):

<div class="snippet" markdown="0">
  <select id="sel-1">
    <option value="" disabled selected>What's your favorite food?</option>
    <option value="chocolate">Chocolate</option>
    <option value="ice-cream">Ice Cream</option>
    <option value="fries">French Fries</option>
  </select>

  <button type="button" onclick="choosePizza()">Choose Pizza</button>
</div>

The `<select>` is obviously left at an invalid state (its value is
`""`), which means existing validation mechanisms still work as
intended. However, we're left with an empty select, there's no
placeholder text to guide the user.

# The Solution

Fortunately the `HTMLSelectElement` interface has a
[`selectedIndex`][1] property with a value of `-1` when no
option is selected, which is exactly our case. We can leverage this
property to perform a quick validation:

<div class="snippet" markdown="0">
  <select id="sel-2">
    <option value="" disabled selected>What's your favorite food?</option>
    <option value="chocolate">Chocolate</option>
    <option value="ice-cream">Ice Cream</option>
    <option value="fries">French Fries</option>
  </select>

  <button type="button" onclick="choosePizzaFixed()">Choose Pizza</button>
</div>

*[UX]: User experience
[1]: https://developer.mozilla.org/en-US/docs/Web/API/HTMLSelectElement/selectedIndex