package xc.mst.harvester;

/**
 *  Copyright 2002 - 2006 DLESE Program Center/University Corporation for Atmospheric Research (UCAR), P.O.
 *  Box 3000, Boulder, CO 80307, support@dlese.org.<p>
 *
 *  This file is part of the DLESE Tools Project.<p>
 *
 *  The DLESE Tools Project is free software; you can redistribute it and/or modify it under the terms of the
 *  GNU General Public License as published by the Free Software Foundation; either version 2 of the License,
 *  or (at your option) any later version.<p>
 *
 *  The DLESE Tools Project is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  General Public License for more details.<p>
 *
 *  You should have received a copy of the GNU General Public License along with The DLESE System; if not,
 *  write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Observable;
import java.util.Observer;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;

/**
 *  A {@link java.net.URLConnection} wrapper that allows a connection timeout to be set. This is useful for
 *  applications that need to retrieve content from a URL without hanging if the remote server does not
 *  respond within a given period of time. Throws a {@link URLConnectionTimedOutException} if the connection
 *  is not made within the allotted time or a {@link java.io.IOException} if the connection fails for some
 *  other reason, such as an HTTP type 500 or 403 error. Note that this class is compatible with Java 1.3 and
 *  1.4 (Java 1.5 has native support for setting a timeout in URLConnection).<p>
 *
 *  The static methods {@link #importURL} and {@link #getInputStream} are provided for convenience. <p>
 *
 *  Example that uses the static getInputStream method:<p>
 *
 *  <code>
 *  import org.dlese.dpc.util.TimedURLConnection;<br>
 *  import org.dlese.dpc.util.URLConnectionTimedOutException;<br>
 *  import org.dom4j.Document;<br>
 *  import org.dom4j.DocumentException;<br>
 *  import org.dom4j.io.SAXReader;<br>
 *  // Plus other imports... <p>
 *
 *  try {<br>
 *  <blockquote> <p>
 *
 *  // Get an input stream for the remote content (throws exeception if timeout occurs):<br>
 *  InputStream istm = TimedURLConnection.getInputStream("http://example.org/remoteData.xml", 2000); <br>
 *  <p>
 *
 *  // Process the InputStream as desired. In this example, the InputStream is used to create a dom4j XML DOM:
 *  <br>
 *  ... <br>
 *  try{<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SAXReader reader = new SAXReader();<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Document document = reader.read(istm);<br>
 *  } catch ( DocumentException e ) {<br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Handle the Exception as desired... <br>
 *  }<p>
 *
 *  // Now the DOM is ready for use... <br>
 *  ... <br>
 *  </blockquote> } catch (URLConnectionTimedOutException exc) { <br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// The URLConnection timed out...<br>
 *  } catch (IOException ioe) { <br>
 *  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// The URLConnection threw an IOException while attempting to connect...<br>
 *  }<br>
 *  </code>
 *
 * @author     John Weatherley
 * @version    $Id: TimedURLConnection.java,v 1.9 2006/01/07 00:28:20 jweather Exp $
 * @see        java.net.URLConnection
 */
public class TimedURLConnection implements Observer {

	/**
	 * A reference to the logger which writes to the HarvestIn log file
	 */
	static Logger log = Logger.getLogger("harvestIn");

	private URLConnection urlConnection = null;
	private int timeOut = 180000;
	private boolean connected = false;
	private IOException connectIOException = null;


	/**
	 *  Constructor for the TimedUrlConnection object
	 *
	 * @param  urlConnection  A URL connection.
	 * @param  timeOut        Time to wait for a connection, in milliseconds.
	 */
	public TimedURLConnection(URLConnection urlConnection, int timeOut) {
		this.urlConnection = urlConnection;
		this.timeOut = timeOut;
	}


	/**
	 *  Imports the content of a given URL into a String using the default character encoding, timing out if the
	 *  remote server does not respond within the given number of milliseconds. A timeout set to 0 disables the
	 *  timeout (e.g. timeout of infinity). If the connection is http, then redirects are followed. Uses gzip
	 *  encoding if the server supports it. Throws an IOException if an http connection returns something other
	 *  than status 200.
	 *
	 * @param  url                                 The URL to import
	 * @param  timeOutPeriod                       Milliseconds to wait before timing out
	 * @return                                     A String containing the contents of the URL
	 * @exception  IOException                     If IO Error
	 * @exception  URLConnectionTimedOutException  If timeout occurs before the server responds
	 */
	public static String importURL(String url, int timeOutPeriod)
		 throws IOException, URLConnectionTimedOutException {
		return importURL(url, null, timeOutPeriod);
	}


	/**
	 *  Imports the content of a given URL into a String using the given character encoding, timing out if the
	 *  remote server does not respond within the given number of milliseconds. A timeout set to 0 disables the
	 *  timeout (e.g. timeout of infinity). If the connection is http, then redirects are followed. Uses gzip
	 *  encoding if the server supports it. Throws an IOException if an http connection returns something other
	 *  than status 200.
	 *
	 * @param  url                                 The URL to import
	 * @param  timeOutPeriod                       Milliseconds to wait before timing out
	 * @param  characterEncoding                   The character encoding to use, for example 'UTF-8'
	 * @return                                     A String containing the contents of the URL
	 * @exception  IOException                     If IO Error
	 * @exception  URLConnectionTimedOutException  If timeout occurs before the server responds
	 */
	public static String importURL(String url, String characterEncoding, int timeOutPeriod)
		 throws IOException, URLConnectionTimedOutException {

		InputStream istm = getInputStream(url, timeOutPeriod);

		InputStreamReader inr;
		if (characterEncoding == null)
			inr = new InputStreamReader(istm);
		else
			inr = new InputStreamReader(istm, characterEncoding);
		BufferedReader in = new BufferedReader(inr);
		int c;
		StringBuffer content = new StringBuffer();
		while ((c = in.read()) != -1)
			content.append((char) c);

		istm.close();
		in.close();
		inr.close();

		return content.toString();
	}


	/**
	 *  Gets an InputStream for the given URL, timing out if the remote server does not respond within the given
	 *  number of milliseconds. A timeout set to 0 disables the timeout (e.g. timeout of infinity). Supports gzip
	 *  compression (returns a GZIPInputStream if the server supports it). If the connection is http, then
	 *  redirects are followed. Throws an IOException if an http connection returns something other than status
	 *  200.
	 *
	 * @param  url                                 The URL to import
	 * @param  timeOutPeriod                       Milliseconds to wait before timing out
	 * @return                                     An InputStream for the URL
	 * @exception  IOException                     If IO Error
	 * @exception  URLConnectionTimedOutException  If timeout occurs before the server responds
	 */
	public static InputStream getInputStream(String url, int timeOutPeriod)
		 throws IOException, URLConnectionTimedOutException {

		URL u = new URL(url);

		URLConnection conn = u.openConnection();

		boolean isHttp = false;
		if (conn instanceof HttpURLConnection)
			isHttp = true;

		// Setup the connection:
		conn.setRequestProperty("Connection", "close");
		// Indicate we want gzip if supported:
		conn.setRequestProperty("Accept-Encoding",	"gzip,deflate");//;q=1.0, identity;q=0.5, *;q=0");

		// Follow HTTP redirects:
		if (isHttp)
			HttpURLConnection.setFollowRedirects(true);

		// Connect with the URL
		if (timeOutPeriod <= 0)
			conn.connect();
		else {
			// The following 'hack' works in java 1.4.x and earlier:
			TimedURLConnection timedConnection = new TimedURLConnection(conn, timeOutPeriod);
			timedConnection.connect(); // Throws exception if timeout reached before connecting

			// The following works in Java 1.5 (and is preferrable):
			/*
			try{
				conn.setConnectTimeout(timeOutPeriod);
				conn.connect();
			} catch (SocketTimeoutException ste) {
				throw new URLConnectionTimedOutException(ste.getMessage());
			}
			*/
		}

		// Check the HTTP response code:
		if (isHttp) {
			int respcode = ((HttpURLConnection) conn).getResponseCode();
			if (respcode < 200 || respcode > 299)
				throw new IOException("Invalid HTTP response code: " + respcode);
		}

		// Since we requested qzip output above, check to see if the content was returned
		// in gzip format and use a GZIPInputStream if it was
		InputStream istm = conn.getInputStream();
		String encd = conn.getContentEncoding();
		if (encd != null && encd.equalsIgnoreCase("gzip"))
			istm = new GZIPInputStream(istm);

		return istm;
	}


	/**
	 *  Connects to the {@link java.net.URLConnection} by calling it's {@link java.net.URLConnection#connect()}
	 *  method and then verifies that the InputStream is ready by calling the {@link
	 *  java.net.URLConnection#getInputStream()} method. Throws URLConnectionTimedOutException if these method
	 *  calls do not succeeed within the specified timeout period. Throws IOException if these method calls fail
	 *  for any reason, such as an HTTP type 500 or 403, for example.
	 *
	 * @exception  URLConnectionTimedOutException  Thrown if the connection is not established within the
	 *      specified timeout period.
	 * @exception  IOException                     Thrown if the attempt to connect failed, such as an HTTP 500,
	 *      for example.
	 */
	public void connect()
		 throws URLConnectionTimedOutException, IOException {

		ObservableURLConnection ouc = new
			ObservableURLConnection(urlConnection);

		ouc.addObserver(this);

		Thread ouct = new Thread(ouc);

		ouct.start();

		try {
			// Wait for the ObservableURLConnection thread to complete successfully, otherwise timeout.
			ouct.join(timeOut);
		} catch (InterruptedException i) {}

		// If the connect() threw an IOException, throw one here.
		if (connectIOException != null)
			throw connectIOException;

		// If not connected, timeout must have occured.
		if (!connected) {
			String msg;
			if (timeOut >= 1000)
				msg = "Waited " + (timeOut / 1000) + " seconds. Connection timed out.";
			else
				msg = "Waited " + timeOut + " milliseconds. Connection timed out.";

			log.error(msg);

			throw new URLConnectionTimedOutException(msg);
		}
	}



	/**
	 *  Called by ObservableURLConnection iff the URLConnection succeeds or an IOException is thrown while
	 *  attempting to connect. This method is called by the notifyObservers() method.
	 *
	 * @param  o    The observable object.
	 * @param  arg  An IOException or null if none occured.
	 */
	public void update(Observable o, Object arg) {
		if (arg != null)
			connectIOException = (IOException) arg;
		connected = true;
	}



	/**
	 *  Observes a URLConnection for successful connection.
	 *
	 * @author     John Weatherley
	 * @version    $Id: TimedURLConnection.java,v 1.9 2006/01/07 00:28:20 jweather Exp $
	 */
	public class ObservableURLConnection extends Observable implements
		Runnable {

		private URLConnection urlConnection;


		/**
		 *  Constructor for the ObservableURLConnection object
		 *
		 * @param  urlConnection  The URLConnection to connect with.
		 */
		public ObservableURLConnection(URLConnection urlConnection) {
			this.urlConnection = urlConnection;
		}


		/**
		 *  Attempts to connect to the {@link java.net.URLConnection} by calling it's {@link
		 *  java.net.URLConnection#connect()} and {@link java.net.URLConnection#getInputStream()} methods and
		 *  notifies the {@link TimedURLConnection} if both method calls succeed.
		 */
		public void run() {
			try {
				// connect() will throw IOException if HTTP 500, etc. occurred
				urlConnection.connect();
				// Make sure the InputStream is available as well...
				urlConnection.getInputStream();

				setChanged();
				notifyObservers();
			} catch (IOException e) {
				setChanged();
				notifyObservers(e);
			}
		}
	}
}

