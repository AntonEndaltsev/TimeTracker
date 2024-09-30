package TimeTracker.TimeTracker;

import TimeTracker.TimeTracker.DTO.TaskDTO;
import TimeTracker.TimeTracker.DTO.UserDTO;
import TimeTracker.TimeTracker.models.User;
import TimeTracker.TimeTracker.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class TimeTrackerApplicationTest {
	@LocalServerPort
	private Integer port;

	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine");

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}


	//@Autowired
	//private MockMvc mvc;

	@Autowired
	private UserService userService;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		//todoRepository.deleteAll();
		RestAssured.baseURI = "http://localhost:" + port;
	}

	@Test
	void testAddUser() throws Exception {

//		Response response = given()
//				.header("Content-type", "application/json")
//				.and()
//				//.body(requestBody)
//				.when()
//				//.get("/v1/people")
//				//.param("owner", goodIdPerson)
//				.get("/user?name=Test2")
//				.then()
//				//.statusCode(200)
//				.extract().response();
//
//		Assertions.assertEquals(200, response.statusCode());

		UserDTO testUserDTO = given()
				.header("Content-type", "application/json")
				.and()
				.when()
						.get("/user?name=Test2")
								.then().extract().body().as(UserDTO.class);

		assertEquals("Test2", testUserDTO.getName());

	}

	@Test
	void testChangeUser() throws Exception {

		UserDTO testUser2DTO = given()
				.header("Content-type", "application/json")
				.and()
				.when()
				.get("/userchange?oldname=Test2&newname=Test3")
				.then().extract().body().as(UserDTO.class);

		assertEquals("Test3", testUser2DTO.getName());

	}

	@Test
	void testStartTask() throws Exception {

		TaskDTO testTaskDTO = given()
				.header("Content-type", "application/json")
				.and()
				.when()
				.get("/starttask?name=Task1&user=Test2")
				.then().extract().body().as(TaskDTO.class);

		assertEquals("Task1", testTaskDTO.getName());

	}

	@Test
	void testStopTask() throws Exception {

		Response response1 = given()
				.header("Content-type", "application/json")
				.and()
				.when()
				.get("/user?name=Test4")
				.then()
				.extract().response();

		Response response2 = given()
				.header("Content-type", "application/json")
				.and()
				.when()
				.get("/starttask?name=Task2&user=Test4")
				.then()
				.extract().response();
		Assertions.assertEquals(200, response2.statusCode());

		TaskDTO testTaskDTO = given()
				.header("Content-type", "application/json")
				.and()
				.when()
				.get("/stoptask?name=Task2")
				.then().extract().body().as(TaskDTO.class);

		assertEquals("Task2", testTaskDTO.getName());

	}


}


