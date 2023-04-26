package com.kickers;

import com.kickers.dao.ConnectionDB;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableFeignClients
public class ReviewsApplication {

	public static void main(String[] args) throws SQLException {
		Connection connection = ConnectionDB.getConnection();
		Statement statement = connection.createStatement();

		PreparedStatement statement1 = connection.prepareStatement("select API_NEW as a from corp where corpname ='АЛ'");
		System.out.println(statement1);

		ResultSet set = statement.executeQuery("select API_NEW as a from corp where corpname ='АЛ'");
		set.next();


		SpringApplication.run(ReviewsApplication.class, args);
	}
	private static String getToken(String corpName) throws SQLException{
		Connection connection = ConnectionDB.getConnection();
			assert connection != null;
			PreparedStatement statement = connection.prepareStatement("select API_NEW as a from corp where corpname =" + "\'" + corpName + "\'");
			ResultSet result = statement.executeQuery();
			result.next();
			return result.getString("a");
	}

}
