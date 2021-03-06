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
 * Servlet implementation class GetTasks
 */
@WebServlet("/GetTasks")
public class GetTasks extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static String             url              = "jdbc:mysql://jxj.ddns.net:3306/T3SwDevProj";
	static String             user             = "remotejxj";
	static String             password         = "jxjremotep@ss";
	static Connection         connection       = null;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetTasks() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * Return tasks for user
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
			String selectSQL = "SELECT * FROM Tasks WHERE user_id=" + request.getParameter("userId")
			+ " ORDER BY task_priorityid";
			System.out.println(selectSQL);
	        PreparedStatement preparedStatement = connection.prepareStatement(selectSQL);
	        ResultSet rs = preparedStatement.executeQuery();
	        while (rs.next()) {
	           String id = rs.getString("id");
	           String title = rs.getString("task_title");
	           String description = rs.getString("task_description");
	           String duedate = rs.getString("task_duedate");
	           String stateid = rs.getString("task_stateid");
	           String category = rs.getString("task_category");
	           String priority = rs.getString("task_priorityid");
	           response.getWriter().append(id + "/split,");
	           response.getWriter().append(title + "/split,");
	           response.getWriter().append(description + "/split,");
	           response.getWriter().append(duedate + "/split,");
	           response.getWriter().append(stateid + "/split,");
	           response.getWriter().append(category + "/split,");
	           response.getWriter().append(priority + "/split\n");
	        }
		}
		catch(SQLException e) {
			response.getWriter().append("SQL ERROR");
			response.getWriter().append(e.getMessage());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * Based on data sent by client, deletes a task, updates an existing tasks, or creates a new task in the database.
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
			StringBuilder linesCombined = new StringBuilder();
			String line;
			while((line = br.readLine()) != null) {
				linesCombined.append(line + "\\n");
			}
			linesCombined.setLength(linesCombined.length() - 2);
			String[] postData = linesCombined.toString().split("/split,", -1);
			String updateSQL;
			if(postData.length == 8 && postData[5].equals("")) {
				postData[5] = "NULL";
			}
			if(postData.length == 8 && postData[7].equals("")) {
				postData[7] = "NULL";
			}
			if(postData[0].equals("DELETE")){
				updateSQL = "DELETE FROM Tasks WHERE id = " + postData[1];
			}
			else if(postData[0].equals("0")) {
				updateSQL = "INSERT INTO Tasks(user_id, "
						+ "task_title, " +
					    "task_description," + 
						"task_duedate," + 
						"task_stateid," + 
						"task_category," + 
						"task_priorityid) VALUES(" +
						postData[1] + ",\"" +
						postData[2] + "\",\"" +
						postData[3] + "\",\"" +
						postData[4] + "\"," +
						postData[5] + ",\"" +
						postData[6] + "\"," +
						postData[7] + ")";
			}
			else {
				updateSQL = "UPDATE Tasks SET "
						+ "task_title = \"" + postData[2] +
					    "\",task_description = \"" + postData[3] + 
						"\",task_duedate = \"" + postData[4] + 
						"\",task_stateid = " + postData[5] + 
						",task_category = \"" + postData[6] + 
						"\",task_priorityid = " + postData[7] +
						" WHERE id=" + postData[0];
			}

			System.out.println(updateSQL);
	        PreparedStatement preparedStatement = connection.prepareStatement(updateSQL);
	        int rs = preparedStatement.executeUpdate();
	        if(rs == 0) {
	        	response.getWriter().append("UPDATE FAILED");
	        }
	        else {
	        	response.getWriter().append("UPDATE SUCCEDED");
	        }
		}
		catch(SQLException e) {
			response.getWriter().append("SQL ERROR");
			response.getWriter().append(e.getMessage());
		}
	}

}
