package org.elwiki.internal;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.mail.AuthenticationFailedException;
import javax.mail.SendFailedException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.wiki.api.core.ContextEnum;
import org.apache.wiki.api.core.ContextUtil;
import org.apache.wiki.api.core.Engine;
import org.apache.wiki.api.core.Session;
import org.apache.wiki.api.core.WikiContext;
import org.apache.wiki.api.exceptions.NoSuchPrincipalException;
import org.apache.wiki.auth.AccountRegistry;
import org.apache.wiki.auth.AuthenticationManager;
import org.apache.wiki.auth.UserProfile;
import org.apache.wiki.url0.URLConstructor;
import org.apache.wiki.util.TextUtil;
import org.apache.wiki.util0.MailUtil;
import org.eclipse.jdt.annotation.NonNull;

public class LostpasswordCmdCode extends CmdCode {

	private static final Logger log = Logger.getLogger(RenameCmdCode.class);
	
    String message = null;
	
	@Override
	public void applyPrologue(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception {
		super.applyPrologue(httpRequest, httpResponse);
		// Get wiki context and check for authorization
		WikiContext wikiContext = ContextUtil.findContext(httpRequest);
		Engine wiki = wikiContext.getEngine();

	    ResourceBundle rb = null; //:FVK: Preferences.getBundle( wikiContext, "CoreResources" );

	    Session wikiSession = wikiContext.getWikiSession();
	    String action = httpRequest.getParameter( "action" );

	    boolean done = false;

	    if( action != null && action.equals( "resetPassword" ) ) {
	        if( resetPassword( wiki, httpRequest, rb ) ) {
	            done = true;
	            wikiSession.addMessage( "resetpwok", rb.getString( "lostpwd.emailed" ) );
	            //:FVK: httpRpageContext.setAttribute( "passwordreset", "done" );
	        } else {
	            // Error
	            wikiSession.addMessage( "resetpw", message );
	        }
	    }
		
	}

    public boolean resetPassword( Engine wiki, HttpServletRequest request, ResourceBundle rb ) {
        // Reset pw for account name
        String name = request.getParameter( "name" );
        @NonNull AccountRegistry accountRegistry = this.getEngine().getManager(AccountRegistry.class);
        boolean success = false;

        try {
            UserProfile profile = null;
            /*
             // This is disabled because it would otherwise be possible to DOS JSPWiki instances
             // by requesting new passwords for all users.  See https://issues.apache.org/jira/browse/JSPWIKI-78
             try {
                 profile = userDatabase.find(name);
             } catch (NoSuchPrincipalException e) {
             // Try email as well
             }
            */
            if( profile == null ) {
                profile = accountRegistry.findByEmail( name );
            }

            String applicationName = wiki.getWikiConfiguration().getApplicationName();
            String email = profile.getEmail();
            String randomPassword = TextUtil.generateRandomPassword();

            // Try sending email first, as that is more likely to fail.

            URLConstructor urlConstructor = getEngine().getManager(URLConstructor.class); 
            Object[] args = { profile.getLoginName(), randomPassword, request.getScheme() + "://"+ request.getServerName() + ":" + request.getServerPort() +
            		urlConstructor.makeURL( ContextEnum.PAGE_NONE.getRequestContext(), "Login.jsp", "" ), applicationName };

            String mailMessage = MessageFormat.format( rb.getString( "lostpwd.newpassword.email" ), args );

            Object[] args2 = { applicationName };
            MailUtil.sendMessage( wiki.getWikiConfiguration(), 
            		              email, 
            		              MessageFormat.format( rb.getString( "lostpwd.newpassword.subject" ), args2 ),
                                  mailMessage );

            log.info( "User " + email + " requested and received a new password." );

            // Mail succeeded.  Now reset the password.
            // If this fails, we're kind of screwed, because we already emailed.
            profile.setPassword( randomPassword );
            accountRegistry.save( profile );
            success = true;
        } catch( NoSuchPrincipalException e ) {
            Object[] args = { name };
            message = MessageFormat.format( rb.getString( "lostpwd.nouser" ), args );
            log.info( "Tried to reset password for non-existent user '" + name + "'" );
        } catch( SendFailedException e ) {
            message = rb.getString( "lostpwd.nomail" );
            log.error( "Tried to reset password and got SendFailedException: " + e );
        } catch( AuthenticationFailedException e ) {
            message = rb.getString( "lostpwd.nomail" );
            log.error( "Tried to reset password and got AuthenticationFailedException: " + e );
        } catch( Exception e ) {
            message = rb.getString( "lostpwd.nomail" );
            log.error( "Tried to reset password and got another exception: " + e );
        }
        return success;
    }
    
}
