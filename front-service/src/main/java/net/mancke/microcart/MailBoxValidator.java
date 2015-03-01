package net.mancke.microcart;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.naming.*;
import javax.naming.directory.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Basically taken and modified from http://www.rgagnon.com/javadetails/java-0452.html
// Unknown license
public class MailBoxValidator {

	private static final Logger logger = LoggerFactory.getLogger(MailBoxValidator.class);

	private String fromDomain;
	private String fromEmail;

	public MailBoxValidator(String fromDomain, String fromEmail) {
		this.fromDomain = fromDomain;
		this.fromEmail = fromEmail;
	}

	public static void main(String args[]) {
		String testData[] = {
				"blisdcasdcasdc@googlemail.com",				
				"info@bilderbuch-stoff.de",
				"xxx@bilderbuch-stoff.de", // invalid
				"s.muncke@tarent.de", //invalid
				"fail.me@nowhere.spam" // Invalid domain name
		};

		MailBoxValidator validator = new MailBoxValidator(args[0], args[1]);
		for (int ctr = 0; ctr < testData.length; ctr++) {
			System.out.println(testData[ctr] + " is valid? "
					+ validator.isAddressValid(testData[ctr]));
		}
		return;
	}

	private ArrayList getMX(String hostName) throws NamingException {
		// Perform a DNS lookup for MX records in the domain
		Hashtable env = new Hashtable();
		env.put("java.naming.factory.initial",
				"com.sun.jndi.dns.DnsContextFactory");
		DirContext ictx = new InitialDirContext(env);
		Attributes attrs = ictx.getAttributes(hostName, new String[] { "MX" });
		Attribute attr = attrs.get("MX");

		// if we don't have an MX record, try the machine itself
		if ((attr == null) || (attr.size() == 0)) {
			attrs = ictx.getAttributes(hostName, new String[] { "A" });
			attr = attrs.get("A");
			if (attr == null)
				throw new NamingException("No match for name '" + hostName
						+ "'");
		}
		// Huzzah! we have machines to try. Return them as an array list
		// NOTE: We SHOULD take the preference into account to be absolutely
		// correct. This is left as an exercise for anyone who cares.
		ArrayList res = new ArrayList();
		NamingEnumeration en = attr.getAll();

		while (en.hasMore()) {
			String mailhost;
			String x = (String) en.next();
			String f[] = x.split(" ");
			// THE fix *************
			if (f.length == 1)
				mailhost = f[0];
			else if (f[1].endsWith("."))
				mailhost = f[1].substring(0, (f[1].length() - 1));
			else
				mailhost = f[1];
			// THE fix *************
			res.add(mailhost);
		}
		return res;
	}

	public boolean isAddressValid( String address ) {
		
		// Find the separator for the domain name
		int pos = address.indexOf( '@' );

	     // If the address does not contain an '@', it's not valid
	     if ( pos == -1 ) return false;
	
	     // Isolate the domain/machine name and get a list of mail exchangers
	     String domain = address.substring( ++pos );
	     ArrayList mxList = null;
	     try {
	        mxList = getMX( domain );
	     }
	     catch (CommunicationException ce) {
	    	 logger.info("[mail validation] got dns problems email="+address, ce);
	    	 return true;
	     } catch (NamingException ex) {
	    	 logger.info("[mail validation] got host naming exception for email="+address +" - "+ ex);
	        return false;
	     }

	     // if we do not find an mx, we beleve the adress anyway
	     if ( mxList.size() == 0 ) return true;
	     
	     // modification, SMa: mx only use the first mx
	     int mx = 0;
	     try {
	         int res;
	         //
	         Socket skt = new Socket( (String) mxList.get( mx ), 25 );
	         BufferedReader rdr = new BufferedReader
	            ( new InputStreamReader( skt.getInputStream() ) );
	         BufferedWriter wtr = new BufferedWriter
	            ( new OutputStreamWriter( skt.getOutputStream() ) );
	
	         res = hear( rdr );
	         if ( res != 220 ) throw new Exception( "Invalid header" );
	         say( wtr, "EHLO "+ this.fromDomain );
	
	         res = hear( rdr );
	         if ( res != 250 ) throw new Exception( "Not ESMTP" );
	
	         // validate the sender address              
	         say( wtr, "MAIL FROM: <"+this.fromEmail+">" );
	         res = hear( rdr );
	         if ( res != 250 ) throw new Exception( "Sender rejected" );
	
	         say( wtr, "RCPT TO: <" + address + ">" );
	         res = hear( rdr );
	
	         // be polite
	         say( wtr, "RSET" ); hear( rdr );
	         say( wtr, "QUIT" ); hear( rdr );
	
	         rdr.close();
	         wtr.close();
	         skt.close();

	         if ( res == 550 ) {
		    	 logger.info("[mail validation] got response SMTP 550 for email="+address);
	        	 return false;
	         }

	     } catch (Exception e) {
	    	 logger.info("[mail validation] remote mail validation error accept anyway email="+address +" - "+ e.getMessage());
	     }
	     return true;
    }

	private int hear(BufferedReader in) throws IOException {
		String line = null;
		int res = 0;

		while ((line = in.readLine()) != null) {
			String pfx = line.substring(0, 3);
			try {
				res = Integer.parseInt(pfx);
			} catch (Exception ex) {
				res = -1;
			}
			if (line.charAt(3) != '-')
				break;
		}

		return res;
	}

	private void say(BufferedWriter wr, String text) throws IOException {
		wr.write(text + "\r\n");
		wr.flush();

		return;
	}

}