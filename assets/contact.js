'use strict';

const form = document.getElementById('contact-form');
const message = document.getElementById('message');

function checkLimit() {
  const length = this.value.length;
  const error = document.getElementById('error');
  const counter = error.getElementsByTagName('span').item(0);
  const button = form.getElementsByTagName('button').item(0);

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
