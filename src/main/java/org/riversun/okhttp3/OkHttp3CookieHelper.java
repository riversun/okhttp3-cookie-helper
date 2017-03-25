/*  Cookie Handling Lib for OkHttp3
 *
 *  Copyright (c) 2017 Tom Misawa, riversun.org@gmail.com
 *  
 *  Permission is hereby granted, free of charge, to any person obtaining a
 *  copy of this software and associated documentation files (the "Software"),
 *  to deal in the Software without restriction, including without limitation
 *  the rights to use, copy, modify, merge, publish, distribute, sublicense,
 *  and/or sell copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following conditions:
 *  
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *  
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 *  DEALINGS IN THE SOFTWARE.
 *  
 */
package org.riversun.okhttp3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * 
 * Cookie Helper for OkHttp3 <br>
 * <br>
 * <p>
 * usage example:<br>
 * 
 * <pre>
 * <code>
 * String url = "https://example.com/webapi";
 * 
 * 		OkHttp3CookieHelper cookieHelper = new OkHttp3CookieHelper();
 * 		cookieHelper.setCookie(url, "cookie_name", "cookie_value");
 * 		
 * 		OkHttpClient client = new OkHttpClient.Builder()
 * 				.cookieJar(cookieHelper.cookieJar())
 * 				.build();
 * 
 * 		Request request = new Request.Builder()
 * 				.url(url)
 * 				.build();
 * </code>
 * </pre>
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 */
public class OkHttp3CookieHelper {

	private final Map<String, List<Cookie>> mServerCookieStore = new ConcurrentHashMap<String, List<Cookie>>();

	private Map<String, List<Cookie>> mClientCookieStore = new ConcurrentHashMap<String, List<Cookie>>();

	private final CookieJar mCookieJar = new CookieJar() {
		@Override
		public List<Cookie> loadForRequest(HttpUrl url) {

			List<Cookie> serverCookieList = mServerCookieStore.get(url.host());

			if (serverCookieList == null) {
				serverCookieList = new ArrayList<Cookie>();
			}

			final List<Cookie> clientCookieStore = mClientCookieStore.get(url.host());

			if (clientCookieStore != null) {
				serverCookieList.addAll(clientCookieStore);
			}

			return serverCookieList != null ? serverCookieList : new ArrayList<Cookie>();
		}

		@Override
		public void saveFromResponse(HttpUrl url, List<Cookie> unmodifiableCookieList) {
			// Why 'new ArrayList<Cookie>'?
			// Since 'unmodifiableCookieList' can not be changed, create a new
			// one
			mServerCookieStore.put(url.host(), new ArrayList<Cookie>(unmodifiableCookieList));

			// The persistence code should be described here if u want.
		}

	};

	/**
	 * Set cookie
	 * 
	 * @param url
	 * @param cookie
	 */
	public void setCookie(String url, Cookie cookie) {

		final String host = HttpUrl.parse(url).host();

		List<Cookie> cookieListForUrl = mClientCookieStore.get(host);
		if (cookieListForUrl == null) {
			cookieListForUrl = new ArrayList<Cookie>();
			mClientCookieStore.put(host, cookieListForUrl);
		}
		putCookie(cookieListForUrl, cookie);

	}

	/**
	 * Set cookie
	 * 
	 * @param url
	 * @param cookieName
	 * @param cookieValue
	 */
	public void setCookie(String url, String cookieName, String cookieValue) {
		final HttpUrl httpUrl = HttpUrl.parse(url);
		setCookie(url, Cookie.parse(httpUrl, cookieName + "=" + cookieValue));
	}

	/**
	 * Set cookie
	 * 
	 * @param httpUrl
	 * @param cookieName
	 * @param cookieValue
	 */
	public void setCookie(HttpUrl httpUrl, String cookieName, String cookieValue) {
		setCookie(httpUrl.host(), Cookie.parse(httpUrl, cookieName + "=" + cookieValue));
	}

	/**
	 * Returns CookieJar
	 * 
	 * @return
	 */
	public CookieJar cookieJar() {
		return mCookieJar;
	}

	private void putCookie(List<Cookie> storedCookieList, Cookie newCookie) {

		Cookie oldCookie = null;
		for (Cookie storedCookie : storedCookieList) {

			// create key for comparison
			final String oldCookieKey = storedCookie.name() + storedCookie.path();
			final String newCookieKey = newCookie.name() + newCookie.path();

			if (oldCookieKey.equals(newCookieKey)) {
				oldCookie = storedCookie;
				break;
			}
		}
		if (oldCookie != null) {
			storedCookieList.remove(oldCookie);
		}
		storedCookieList.add(newCookie);
	}

}
