package com.kickers;

import com.kickers.dao.ConnectionDB;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@SpringBootApplication
@EnableFeignClients
public class ReviewsApplication {

	public static void main(String[] args) throws SQLException {
		Connection connection = ConnectionDB.getConnection();
        assert connection != null;
        Statement statement = connection.createStatement();

		ResultSet resultSet = statement.executeQuery("select * from answer_params_auto");
		System.out.println(resultSet.next());
		System.out.println(resultSet.getString("date"));

		LocalDate date = resultSet.getDate("date").toLocalDate();

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		String formattedDate = date.format(dateFormatter);

		System.out.println(formattedDate);

		SpringApplication.run(ReviewsApplication.class, args);
	}
}
