'use strict';

const message = document.getElementById('message');

function checkLimit() {
  const length = this.value.length;
  const error = document.getElementById('error');
  const counter = error.getElementsByTagName('span').item(0);
  const button = document.querySelector('form button');

  if (length > this.charLimit) {
    counter.innerHTML = length;
    error.style.display = 'block';
    button.disabled = true;
  } else {
    error.style.display = 'none';
    button.disabled = false;
  }
}

function onSubmit(token) {
  document.getElementById('contact-form').submit();
}

message.charLimit = 1000;
message.addEventListener('keyup', checkLimit);
