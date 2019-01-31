import java.io.Console;
import java.net.InetSocketAddress;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionIdManager;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.server.session.HouseKeeper;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Starts a jetty http server with three servlets and session management to
 * count the number of times a user visits the /action page.
 * 
 * @author Joseph Eichenhofer
 */
public class TargetServer {

	// number of seconds before session expires
	private static final int SESSION_TIMEOUT = 300;
	// number of seconds between checking for expired sessions
	private static final int SCAVENGE_INTERVAL = 20;

	// for printing logged-in requests
	Console term;
	// server object for configuring and starting handlers
	Server server;

	/**
	 * Constructor. Configures the server context with session management and three
	 * handlers (one for login, one for button press, and one for page view). Does
	 * not start the server (must call startServer).
	 * 
	 * Instantiate with handle to terminal for output. Console object is
	 * synchronized, so the multithreaded printing/reading will not be a problem.
	 * 
	 * @param terminalHandle
	 *            instantiated object for the output terminal
	 * @throws Exception
	 *             if unable to configure server (caused by setIntervalSec on
	 *             housekeeper)
	 */
	public TargetServer(Console terminalHandle) throws Exception {
		term = terminalHandle;

		// instantiate servlet on port 8080
		server = new Server(new InetSocketAddress("0.0.0.0", 8080));

		// setup context with session management
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		// set session timeout
		SessionHandler sessions = context.getSessionHandler();
		sessions.setMaxInactiveInterval(SESSION_TIMEOUT);
		// configure scavenger to detect expired sessions
		SessionIdManager idManager = new DefaultSessionIdManager(server);
		HouseKeeper scavenger = new HouseKeeper();
		scavenger.setIntervalSec(SCAVENGE_INTERVAL);
		idManager.setSessionHouseKeeper(scavenger);
		sessions.setSessionIdManager(idManager);
		// finish initialization of handler
		context.setContextPath("/");
		server.setHandler(context);

		// add the three servlets
		context.addServlet(MaliciousAd.class, "/Ad");
		context.addServlet(WelcomePage.class, "/");
	}

	/**
	 * Start the server as configured by constructor. Cleanly handles as many errors
	 * as possible with debug info.
	 * 
	 * @return true if successful or false if errors are detected
	 */
	public boolean startServer() {
		// try to start the server
		try {
			term.printf("Starting web server...\n");
			server.start();
		} catch (Exception e) {
			System.err.println("Unable to start server.");
			e.printStackTrace();
			server.destroy();
			return false;
		}

		// wait for input to exit
		term.printf("Open a web browser and access 'localhost:8080' to view the web interface.\n");
		do {
			term.printf("Type 'quit' and press enter to stop the server.\n");
		} while (!term.readLine().equals("quit"));

		// try to stop the server
		try {
			server.stop();
		} catch (Exception e) {
			System.err.println("Unable to stop server. Forcing Quit.");
			e.printStackTrace();
			server.destroy();
			return false;
		}

		// try to join the server threads after stopping
		try {
			server.join();
		} catch (InterruptedException e) {
			System.err.print("Unable to join server. Forcing Quit.");
			server.destroy();
			return false;
		}

		server.destroy();
		term.printf("Server exited.\n");
		return true;
	}

	/**
	 * Start the server with various servlets registered.
	 * 
	 * @param args
	 *            n/a
	 */
	public static void main(String[] args) {
		// get handle to console for input
		Console term = System.console();
		if (term == null) {
			System.err.println("Unable to get console. Are you running from an IDE?");
			System.exit(-1);
		}

		boolean success = false;

		// try server startup
		try {
			TargetServer s = new TargetServer(term);
			success = s.startServer();
		} catch (Exception e) {
			success = false;
			term.printf("fatal error in server startup\n");
			e.printStackTrace();
		}

		if (!success) {
			// exit with error if bad return code
			System.exit(-1);
		}
	}

}
