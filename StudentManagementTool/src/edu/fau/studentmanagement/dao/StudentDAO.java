package edu.fau.studentmanagement.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.fau.studentmanagement.model.Student;

/**
 * AbstractDAO.java This DAO class provides CRUD database operations for the
 * table users in the database.
 */
public class StudentDAO {
	private String jdbcURL = "jdbc:mysql://localhost:3306/testdb?useSSL=false";
	private String jdbcUsername = "root";
	private String jdbcPassword = "#Prince1970#";

	private static final String INSERT_STUDENTS_SQL = "INSERT INTO students" + "  (name, email, address) VALUES "
			+ " (?, ?, ?);";

	private static final String SELECT_STUDENT_BY_ID = "select id,name,email,address from students where id =?";
	private static final String SELECT_ALL_STUDENTS = "select * from students";
	private static final String DELETE_STUDENTS_SQL = "delete from students where id = ?;";
	private static final String UPDATE_STUDENTS_SQL = "update students set name = ?,email= ?, address =? where id = ?;";

	public StudentDAO() {
	}

	protected Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connection;
	}

	public void insertStudent(Student student) throws SQLException {
		System.out.println(INSERT_STUDENTS_SQL);
		// try-with-resource statement will auto close the connection.
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(INSERT_STUDENTS_SQL)) {
			preparedStatement.setString(1, student.getName());
			preparedStatement.setString(2, student.getEmail());
			preparedStatement.setString(3, student.getAddress());
			System.out.println(preparedStatement);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			printSQLException(e);
		}
	}

	public Student selectStudent(int id) {
		Student student = null;
		// Step 1: Establishing a Connection
		try (Connection connection = getConnection();
				// Step 2:Create a statement using connection object
				PreparedStatement preparedStatement = connection.prepareStatement(SELECT_STUDENT_BY_ID);) {
			preparedStatement.setInt(1, id);
			System.out.println(preparedStatement);
			// Step 3: Execute the query or update query
			ResultSet rs = preparedStatement.executeQuery();

			// Step 4: Process the ResultSet object.
			while (rs.next()) {
				String name = rs.getString("name");
				String email = rs.getString("email");
				String address = rs.getString("address");
				student = new Student(id, name, email, address);
			}
		} catch (SQLException e) {
			printSQLException(e);
		}
		return student;
	}

	public List<Student> selectAllStudents() {

		// using try-with-resources to avoid closing resources (boiler plate code)
		List<Student> students = new ArrayList<>();
		// Step 1: Establishing a Connection
		try (Connection connection = getConnection();

				// Step 2:Create a statement using connection object
			PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_STUDENTS);) {
			System.out.println(preparedStatement);
			// Step 3: Execute the query or update query
			ResultSet rs = preparedStatement.executeQuery();

			// Step 4: Process the ResultSet object.
			while (rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				String email = rs.getString("email");
				String address = rs.getString("address");
				students.add(new Student(id, name, email, address));
			}
		} catch (SQLException e) {
			printSQLException(e);
		}
		return students;
	}

	public boolean deleteStudent(int id) throws SQLException {
		boolean rowDeleted;
		try (Connection connection = getConnection();
				PreparedStatement statement = connection.prepareStatement(DELETE_STUDENTS_SQL);) {
			statement.setInt(1, id);
			rowDeleted = statement.executeUpdate() > 0;
		}
		return rowDeleted;
	}

	public boolean updateStudent(Student student) throws SQLException {
		boolean rowUpdated;
		try (Connection connection = getConnection();
				PreparedStatement statement = connection.prepareStatement(UPDATE_STUDENTS_SQL);) {
			statement.setString(1, student.getName());
			statement.setString(2, student.getEmail());
			statement.setString(3, student.getAddress());
			statement.setInt(4, student.getId());

			rowUpdated = statement.executeUpdate() > 0;
		}
		return rowUpdated;
	}

	private void printSQLException(SQLException ex) {
		for (Throwable e : ex) {
			if (e instanceof SQLException) {
				e.printStackTrace(System.err);
				System.err.println("SQLState: " + ((SQLException) e).getSQLState());
				System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
				System.err.println("Message: " + e.getMessage());
				Throwable t = ex.getCause();
				while (t != null) {
					System.out.println("Cause: " + t);
					t = t.getCause();
				}
			}
		}
	}

}
