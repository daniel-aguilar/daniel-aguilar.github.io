'use strict';

var form = document.getElementById('contact-form');
var message = document.getElementById('message');

function checkLimit(e) {
  var length = this.value.length;
  var error = document.getElementById('error');
  var counter = error.getElementsByTagName('span').item(0);
  var button = form.getElementsByTagName('button').item(0);

  if (length > this.charLimit) {
    counter.innerHTML = length;
    error.style.display = 'block';
    button.disabled = true;
  } else {
    error.style.display = 'none';
    button.disabled = false;
  }
}

function beforeSubmit(e) {
  e.preventDefault();
  grecaptcha.execute();
}

function submit(token) {
  form.submit();
}

form.addEventListener('submit', beforeSubmit);

message.charLimit = 1000;
message.addEventListener('keyup', checkLimit);
