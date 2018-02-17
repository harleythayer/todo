package com.hthayer.todo.controllers;

import static com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder.newMongoDbRule;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.hthayer.todo.model.Todo;
import com.hthayer.todo.repository.TodoRepository;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TodoControllerTest {

	@Rule
	public MongoDbRule mongoDbRule = newMongoDbRule().defaultSpringMongoDb("todo-testDb");

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private TodoRepository todoRepository;

	@Autowired
	private MockMvc mvc;

	@Test
	@UsingDataSet(locations = { "/testData/todoList.json" }, loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
	public void testCountAllTodos() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", hasSize(2)));
	}

	@Test
	@UsingDataSet(locations = { "/testData/todoList.json" }, loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
	public void testdeleteEachTodo() throws Exception {
		mvc.perform(MockMvcRequestBuilders.delete("/1")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		mvc.perform(MockMvcRequestBuilders.get("/")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", hasSize(1)));
	}

	@Test
	@UsingDataSet(locations = { "/testData/todoList.json" }, loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
	public void testGetOneTodo() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/2")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is("Todo test item 2")))
				.andExpect(jsonPath("$.whatToDo", is("Test a second todo item")))
				.andExpect(jsonPath("$.completed", is(false)));
	}

	@Test
	@UsingDataSet(locations = { "/testData/todoList.json" }, loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
	public void testGetOneTodoFail() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/3")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}
	
	@Test
	@UsingDataSet(locations = {"/testData/todoList.json" }, loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
	public void testUpdateOneTodo( ) throws Exception {
		Todo updateTodo = new Todo( );
		updateTodo.setId("1");
		updateTodo.setName("updated todo");
		updateTodo.setWhatToDo("We updated the todo item");
		updateTodo.setCompleted( true );
		
		ObjectMapper mapper = new ObjectMapper();
		
		mvc.perform(MockMvcRequestBuilders.put("/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content( mapper.writeValueAsString(updateTodo))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is("updated todo")))
				.andExpect(jsonPath("$.whatToDo", is("We updated the todo item")))
				.andExpect(jsonPath("$.completed", is(true)));
		
		mvc.perform(MockMvcRequestBuilders.get("/1")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is("updated todo")))
				.andExpect(jsonPath("$.whatToDo", is("We updated the todo item")))
				.andExpect(jsonPath("$.completed", is(true)));

	}
	
	@Test
	public void testCreateNewTodo( ) throws Exception {
		Todo newTodo = new Todo( );
		newTodo.setId("1");
		newTodo.setName("New Todo");
		newTodo.setWhatToDo("This is a new Todo");
		newTodo.setCompleted( true );
		
		ObjectMapper mapper = new ObjectMapper();
		
		mvc.perform(MockMvcRequestBuilders.post("/")
				.contentType(MediaType.APPLICATION_JSON)
				.content( mapper.writeValueAsString(newTodo))
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is("New Todo")))
				.andExpect(jsonPath("$.whatToDo", is("This is a new Todo")))
				.andExpect(jsonPath("$.completed", is(true)));
		
		mvc.perform(MockMvcRequestBuilders.get("/1")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is("New Todo")))
				.andExpect(jsonPath("$.whatToDo", is("This is a new Todo")))
				.andExpect(jsonPath("$.completed", is(true)));

	}
}