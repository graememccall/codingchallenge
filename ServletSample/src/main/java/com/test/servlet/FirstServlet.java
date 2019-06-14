package com.test.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.audit4j.core.AuditManager;
import org.audit4j.core.annotation.Audit;
import org.audit4j.core.dto.AuditEvent;
import org.audit4j.core.dto.EventBuilder;
import org.audit4j.core.dto.Field;

/**
 * Servlet implementation class FirstServlet
 */
/**
 * @author preetham
 *
 */
public class FirstServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FirstServlet() {
        System.out.println("FirstServlet Constructor called!");
        
        
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		System.out.println("FirstServlet \"Init\" method called");
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		System.out.println("FirstServlet \"Destroy\" method called");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("FirstServlet \"Service\" method(inherited) called");
		System.out.println("FirstServlet \"DoGet\" method called");
		
		
		
		AuditManager manager = (AuditManager) AuditManager.getInstance();  
        


		// call the audit manager
        manager.audit(new AuditEvent("mccallg", "myMethod", new Field("myParam1Name", "myParam1"),
                new Field("myParam2Name", "myParam2")));
        
        
        // or use an event builder
        EventBuilder eventBuilder = new EventBuilder()
        		.addActor("mccallg")
        		.addAction("eventBuilder")
        		.addField("myParam1Name", "myParam1")
        		.addField("myParam2Name", "myParam2");
        
        manager.audit(eventBuilder.build());
        
        
        
        
        
        
		storeInSessionAndRespond(request, response);
		

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Audit
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("FirstServlet \"Service\" method(inherited) called");
        System.out.println("FirstServlet \"DoPost\" method called");
        
        storeInSessionAndRespond(request, response);

	}
	
	private void storeInSessionAndRespond(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		String uname = request.getParameter("uname");
		String emailId = request.getParameter("email");
		System.out.println("Username from jsp page is "+ uname + " and email id is "+ emailId);
		//Create a session
		HttpSession session = request.getSession(true);
		if(session!=null)
		{
			//store the attributes
			session.setAttribute("uname", uname);
			session.setAttribute("emailId", emailId);
			System.out.println("Username and email id is stored in the session");
		}

		out.write("<html><body><h4>Check console to understand the flow</h4></body></html>");
		out.write("<html><body><h2>Username and email id is stored in the session, go back and click on \"TestSession\" to test the session</h2></body></html>");
		out.write("<html><body><p>&copy 2016 Preetham</p></body></html>");
	}



}
