import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GetSQLData
 */
@WebServlet("/GetUsers")
public class GetUsers extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static String             url              = "jdbc:mysql://jxj.ddns.net:3306/T3SwDevProj";
	static String             user             = "remotejxj";
	static String             password         = "jxjremotep@ss";
	static Connection         connection       = null;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetUsers() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * Returns users in system
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			response.getWriter().append("JDBC failed");
			return;
		}
		
		try {
	         connection = DriverManager.getConnection(url, user, password);
	      } catch (SQLException e) {
	         response.getWriter().append("Connection Failed! Check output console");
	         e.printStackTrace();
	         return;
	      }
		
		try {
			String selectSQL = "SELECT * FROM Users";
	        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
	        //preparedStatement.setString(1, theUserName);
	        ResultSet rs = preparedStatement.executeQuery();
	        while (rs.next()) {
	           String id = rs.getString("id");
	           String last = rs.getString("last");
	           String first = rs.getString("first");
	           response.getWriter().append(id + "/split,");
	           response.getWriter().append(last + "/split,");
	           response.getWriter().append(first + "/split\n");
	        }
		}
		catch(SQLException e) {
			response.getWriter().append("SQL ERROR");
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * Receives new user data and places in database
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			response.getWriter().append("JDBC failed");
			return;
		}
		
		try {
	         connection = DriverManager.getConnection(url, user, password);
	      } catch (SQLException e) {
	         response.getWriter().append("Connection Failed! Check output console");
	         e.printStackTrace();
	         return;
	      }
		
		try {
			BufferedReader br = request.getReader();
			String[] postData = br.readLine().split("/split,");
			
			String last = postData[0];
			String first = postData[1];
			String insertSQL = "INSERT INTO Users(last, first) VALUES(\"" + last + "\", \"" + first + "\")";
			System.out.println(insertSQL);
	        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
	        //preparedStatement.setString(1, theUserName);
	        int rs = preparedStatement.executeUpdate();
	        if(rs == 0) {
	        	response.getWriter().append("INSERT FAILED");
	        }
	        else {
	        	response.getWriter().append("INSERT SUCCEDED");
	        }
		}
		catch(SQLException e) {
			response.getWriter().append("SQL ERROR");
			response.getWriter().append(e.getMessage());
		}
	}

}
