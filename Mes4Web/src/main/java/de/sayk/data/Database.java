package de.sayk.data;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.ibatis.jdbc.ScriptRunner;

import de.sayk.DatabaseListener;
import de.sayk.Dir;
import de.sayk.logging.Logger;

public class Database {
	private static Logger log = Logger.getLogger(Database.class.getName());

	private static ArrayList<DatabaseListener> dbListeners = new ArrayList<DatabaseListener>();

	
	private static String url = null;
	private static String user = "";
	private static String password = "";
	private static String editorApp = "";

	private static void init() {

		Reader reader = null;

		try {
			reader = new FileReader(Dir.getHomePath() + "mes.properties");

			Properties prop = new Properties();
			prop.load(reader);

			url = prop.getProperty("url");
			user = prop.getProperty("user");
			password = prop.getProperty("password");
			editorApp = prop.getProperty("editor");

		} catch (IOException e) {
			log.error("problem reading properties.txt, please check installation", e);
			;
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
			}
		}

	}

	public static void addDataListener(DatabaseListener dl) {
		dbListeners.add(dl);
	}

	
	public static Connection getCon() {

		if (url == null)
			init();

		Connection temp = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			temp = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			log.error("problem load the jdbc driver", e);
			e.printStackTrace();
		} catch (SQLException e) {
			log.error("can't connect to database", e);
			e.printStackTrace();
		}
		return temp;

	}

	public static void editUserScript(String fileName) {

		if (url == null)
			init();

		try {
			Runtime runTime = Runtime.getRuntime();
			Process process = runTime.exec(editorApp + " \"" + fileName + "\"");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void setup(String fileName) {

		Connection con = getCon();

		try {

			// Initialize object for ScripRunner
			ScriptRunner sr = new ScriptRunner(con);

			// Give the input file to Reader
			Reader reader = new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8.name());

			// Execute script
			sr.runScript(reader);

			
			//sag allen Bescheid, die es wissen wollen 
			for (DatabaseListener databaseListener : dbListeners) {
				databaseListener.reset();
			}
			
		} catch (Exception ex) {
			log.error("problem executing script: " + fileName, ex);
			
			// Error Msg an User im Web 
			
			
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
					log.error("problem closing database connection", ex);
				}
			}
		}

	}

	public static void initDb() {

		Connection con = getCon();
		Statement stmt = null;
		String query = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = 'MES'";

		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			if (!rs.next()) {
				setup("sql/init.sql.backup");
			}
		} catch (SQLException e) {
			log.error("problem with query: " + query, e);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
					log.error("problem closing database connection", ex);
				}
			}

		}

	}

}
