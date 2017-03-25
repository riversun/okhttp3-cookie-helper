# Overview
Cookie Helper for OkHttp3

It is licensed under [MIT](https://opensource.org/licenses/MIT).

- A library for letting okhttp3 handle cookies. It can be used easily.
- Keep cookies on memory, to do persistence yourself if you need.
- Run on Android and Pure Java environment.

# Example

```java
String url = "https://example.com/webapi";

OkHttp3CookieHelper cookieHelper = new OkHttp3CookieHelper();
    
//force set cookie from the client
cookieHelper.setCookie(url, "cookie_name", "cookie_value");

//set OkHttp3CookieHelper as cookieJar
OkHttpClient client = new OkHttpClient.Builder()
.cookieJar(cookieHelper.cookieJar())
.build();

Request request = new Request.Builder()
.url(url)
.build();
```
