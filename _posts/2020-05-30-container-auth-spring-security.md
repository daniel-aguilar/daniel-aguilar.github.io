---
layout: post
title: Container Authentication with Spring Security
---

Spring Security is a framework full of solid features, most of them working
nearly out of the box. Yet it's still flexible enough to adapt to your
particular requirements. In this article I'll demonstrate how to integrate your
Spring application authentication with a servlet container, such as WebSphere
Application Server (WAS) or Tomcat, using Spring Security.

A little bit of background before we get started. I was assigned a new project
at work (Java web application running in a WAS), with one of the requirements
being the ability to authenticate existing users in a directory service,
specifically an LDAP server. While doing my research at the time, I found myself
either digging through old forum posts, inspecting inherited and unmaintained
code, or reading a whole spec in search for a particular detail. So I thought
I'd write an article about it, with completeness being the main goal.

I'll be working with OpenJDK 8, Spring Boot 2.3.0, Spring Security 5.3.2 and the
Jetty Maven plugin, which spins up a servlet container ideal for development. A
working sample of the following code can be found [here][1].

## Form Based Authentication

The first step is setting up the security configuration over at our *deployment
descriptor* (i.e. `web.xml`). FORM based login just so happens to be our ideal
authentication mechanism, users are presented with a login page where they enter
their credentials. Under the hood, the action of the form is `j_security_check`,
where we'll be POSTing a `j_username` and `j_password`. The server then returns
a cookie `JSESSIONID` used to validate any other requests that require
authentication (and *authorization*, or access on the basis of roles). Here's
how it's done:

{% highlight xml %}
<!-- web.xml excerpt -->
<login-config>
  <auth-method>FORM</auth-method>
  <realm-name>realm</realm-name>
  <form-login-config>
    <form-login-page>/login</form-login-page>
    <form-error-page>/login?error</form-error-page>
  </form-login-config>
</login-config>
{% endhighlight %}

The `realm-name` indicates the *LDAP realm name*. Users are stored there, and we
really don't care about the details. The realm name should be provided by the
application server administrators.

An additional `form-login-config` is required, specifying the login and error
page URLs, where the user will be automatically redirected when unauthenticated,
or when the credentials don't match, respectively.

Over at our login page, we would need an HTML form. The [servlet spec][2] offers
an example of how that might look like:

{% highlight html %}
<form method="POST" action="j_security_check">
  <input type="text" name="j_username">
  <input type="password" name="j_password" autocomplete="off">
</form>
{% endhighlight %}

### Defining the Roles

One last piece of configuration we can specify in the deployment descriptor is
the `security-role` element. We use it to define---well---security roles (with
an optional description for each one):

{% highlight xml %}
<!-- web.xml excerpt -->
<security-role>
  <description>Application Administrator</description>
  <role-name>ROLE_ADMIN</role-name>
</security-role>
{% endhighlight %}

Do notice I added the `ROLE_` prefix to the role name. If you've ever worked
with Spring Security before, you know that's a naming convention.

## Spring Security Configuration

That's it for the `web.xml`, Spring Security takes care of the rest. For this
step I prefer the Java configuration over XML. It consists of overriding the
`configure` method:

{% highlight java %}
protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests(requests -> requests
        .antMatchers("/admin/**").hasRole("ADMIN")
        .anyRequest().permitAll()
    );
    http.jee(jee -> jee.mappableAuthorities("ROLE_ADMIN"));
}
{% endhighlight  %}

Here I'm securing the `/admin/**` route so only the `ADMIN` role can access it
(the `ROLE_` prefix is added automatically), while the rest is allowed to
anyone. immediately after that is where we tell Spring Security to integrate
with the Java EE container authentication (the [docs][3] refer to this scenario
as *pre-authentication*), importing the previously defined roles via the
`mappableAuthorities` method.

Spring Security is highly customizable, the documentation has all the classes
that can be extended to modify the default behaviour, such as loading certain
user information from a database (email address, full name, etc.) using a custom
`UserDetailsService`.

## The Results

Let's do a couple of test requests to verify our authentication system is
working.  Jetty allows to configure a security realm in a properties file, which
is handy for local development, you can learn more [here][4]. I have the
following controller methods:

{% highlight java %}
@GetMapping("/hello")
public String hello() {
    return "Hello";
}

@GetMapping("/admin/hello")
public String helloAdmin() {
    return "Hello, Admin";
}
{% endhighlight %}

{% highlight bash %}
$ curl localhost:8080/app/hello/
Hello

$ curl localhost:8080/app/admin/hello/ | python -m json.tool
{
    "timestamp": "2020-05-30T06:33:41.250+00:00",
    "status": 403,
    "error": "Forbidden",
    "message": "",
    "path": "/app/admin/hello"
}

$ curl -c - -d "j_username=admin&j_password=pass" \
localhost:8080/app/j_security_check/
# Output shortened for brevity
JSESSIONID node0gl4hg0zrigmg3v4ywapwo959.node0

$ curl -b "JSESSIONID=node0gl4hg0zrigmg3v4ywapwo959.node0" \
localhost:8080/app/admin/hello/
Hello, Admin
# Subsequent requests may 403'd as the cookie value has changed
{% endhighlight %}

Spring Security has its own JSON-formatted error messages, which are convenient
if you're working with JavaScript.

## Conclusion

You can see just how little configuration Spring Security actually requires, but
how much functionality you're getting back! Plus, container authentication
delegates the security to the application server, making it relatively easier to
implement.

[1]: https://github.com/daniel-aguilar/daniel-aguilar.github.io/tree/blog/samples/jee-auth
[2]: https://javaee.github.io/servlet-spec/downloads/servlet-3.1/Final/servlet-3_1-final.pdf
[3]: https://docs.spring.io/spring-security/site/docs/5.3.2.RELEASE/reference/html5/#servlet-preauth
[4]: https://wiki.eclipse.org/Jetty/Tutorial/Realms