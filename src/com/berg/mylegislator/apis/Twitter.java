package com.berg.mylegislator.apis;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONException;
import org.json.JSONObject;
import com.berg.mylegislator.Base64Coder;



public final class Twitter {
	static final int GET_LOGIN_INFORMATION = 1;
	public String user;
	public String password;

	
	public Twitter(String _user, String _password) {
		this.user= _user;
		this.password = _password;
	}

	private static final int timeOutMilliSecs = 10 * 1000;
	
	/**
	 * Set a header for basic authentication login.
	 *
	 * @param name
	 * @param password
	 * @param connection
	 */
	private void setBasicAuthentication(String name, String password,
			URLConnection connection) {
		assert name != null && password != null : "Need name and password for this method";
		String userPassword = name + ":" + password;
		String encoding = Base64Coder.encodeString(userPassword);
		connection.setRequestProperty("Authorization", "Basic " + encoding);
	}
	
	private String encode(Object x) {
		return URLEncoder.encode(String.valueOf(x));
	}
	
	/**
	 * Create: Befriends the user specified in the ID parameter as the
	 * authenticating user.
	 *
	 * @param username
	 *            Required. The ID or screen name of the user to befriend.
	 * @return The befriended user.
	 */
	public JSONObject befriend(String username) throws TwitterException {
		if (username == null) throw new NullPointerException();
		String page = fetchWebPage("http://twitter.com/friendships/create/"
				+ username + ".json", null, true);
		try {
			return new JSONObject(page);
		} catch (JSONException e) {
			throw new TwitterException(e);
		}
	}

	private String fetchWebPage(String uri, Map<String, String> vars,
			boolean authenticate) throws TwitterException {
		assert timeOutMilliSecs > 0;
		if (vars != null && vars.size() != 0) {
			uri += "?";
			for (Entry<String, String> e : vars.entrySet()) {
				if (e.getValue() == null)
					continue;
				uri += encode(e.getKey()) + "=" + encode(e.getValue()) + "&";
			}
		}
		// Setup a connection
		final URLConnection connection;
		final InputStream inStream;
		try {
			connection = new URL(uri).openConnection();
		} catch (IOException ex) {
			throw new TwitterException(ex);
		}
		// Authenticate
		if (authenticate) {
			setBasicAuthentication(user, password, connection);
		}
		// pretend to be IE6 on Windows XP SP2
		// http://en.wikipedia.org/wiki/User_agent#Internet_Explorer
		connection
				.setRequestProperty("User-Agent",
						"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727)");
		connection.setDoInput(true);
		connection.setReadTimeout(timeOutMilliSecs);
		// Open a connection
		try {
			inStream = connection.getInputStream();
		} catch (FileNotFoundException e) { // This happens with 404s only
			throw new TwitterException("404 Error: Page not found " + uri);
		} catch (IOException e) { // All other problems
			// This happens with failed connections and 404s
			Map<String, List<String>> headers = connection.getHeaderFields();
			throw new TwitterException("Errror re " + uri + ":\n"
					+ headers.get(null));
		}
		// Read in the web page
		String page = inStream.toString();
		// Done
		return page;
	}
	
	public class TwitterException extends Exception {

		  private static final long serialVersionUID = -7004865779218982263L;

		  /**
		   * Construct a new TwitterException
		   * 
		   * @param string the error message
		   */
		  public TwitterException(String string) {
		    super(string);
		  }

		  /**
		   * Construct a new TwitterException.
		   * 
		   * @param e the existing exception to wrap
		   */
		  public TwitterException(Exception e) {
		    super(e);
		  }

		}
}