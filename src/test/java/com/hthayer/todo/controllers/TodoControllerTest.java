package com.hthayer.todo.controllers;

import static com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder.newMongoDbRule;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

import com.hthayer.todo.model.Todo;
import com.hthayer.todo.repository.TodoRepository;
import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles( "test" )
public class TodoControllerTest {

	@Rule
	public MongoDbRule mongoDbRule = newMongoDbRule().defaultSpringMongoDb("todo-testDb");

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private TodoRepository todoRepository;

	@Autowired
	private MockMvc mvc;
	
	private final ObjectMapper mapper = new ObjectMapper();

	@Test
	@UsingDataSet(locations = "/testData/todoList.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
	public void testCountAllTodos() throws Exception {
		mvc.perform(get("/")
					   	.accept(MediaType.APPLICATION_JSON)
					)
					.andExpect(jsonPath("$", hasSize(2)));
	}
	
	@Test
	@UsingDataSet(locations = "/testData/todoList.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
	public void testGetOneTodo() throws Exception {
		mvc.perform(get("/2")
						.accept(MediaType.APPLICATION_JSON)
					)
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.name", is("Todo test item 2")))
					.andExpect(jsonPath("$.whatToDo", is("Test a second todo item")))
					.andExpect(jsonPath("$.completed", is(false)));
	}
	
	@Test
	@UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
	public void testGetOneTodoFail() throws Exception {
		mvc.perform(get("/3")
						.accept(MediaType.APPLICATION_JSON)
					)
					.andExpect(status().isNotFound());
	}

	@Test
	@UsingDataSet(locations = "/testData/todoList.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
	public void testdeleteATodo() throws Exception {
		final String id = "1";
		mvc.perform(delete("/" + id)
						.accept(MediaType.APPLICATION_JSON)
					)
				   	.andExpect(status().isOk());
		
		//Test controller to make sure it is throwing a 404 exception
		mvc.perform(get("/" + id)
						.accept(MediaType.APPLICATION_JSON)
					)
					.andExpect(status().isNotFound());

		mvc.perform(get("/")
						.accept(MediaType.APPLICATION_JSON)
					)
					.andExpect(jsonPath("$", hasSize(1)));
	}

	@Test
	@UsingDataSet(locations = "/testData/todoList.json", loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
	public void testUpdateOneTodo( ) throws Exception {
		final Todo updateTodo = new Todo( "1", "updated todo", "We updated the todo item", true );
		
		
		
		mvc.perform(put("/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content( mapper.writeValueAsString(updateTodo))
						.accept(MediaType.APPLICATION_JSON)
					)
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.name", is(updateTodo.getName())))
					.andExpect(jsonPath("$.whatToDo", is(updateTodo.getWhatToDo())))
					.andExpect(jsonPath("$.completed", is(updateTodo.getCompleted())));
		
		final Todo updatedTodo = todoRepository.findOne(updateTodo.getId());
		assertEquals( updateTodo.getWhatToDo(), updatedTodo.getWhatToDo()	);
		assertEquals( updateTodo.getName(), updatedTodo.getName( ) );
		assertEquals( updateTodo.getCompleted(), updatedTodo.getCompleted() );
		
		mvc.perform(get("/" + updateTodo.getId())
						.accept(MediaType.APPLICATION_JSON)
					)
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.name", is(updateTodo.getName())))
					.andExpect(jsonPath("$.whatToDo", is(updateTodo.getWhatToDo())))
					.andExpect(jsonPath("$.completed", is(updateTodo.getCompleted())));

	}
	
	@Test
	@UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
	public void testUpdateOneTodoFail( ) throws Exception {
		final Todo updateTodo = new Todo( "6", "updated todo", "We updated the todo item", true );
				
		mvc.perform(put("/" + updateTodo.getId())
						.contentType(MediaType.APPLICATION_JSON)
						.content( mapper.writeValueAsString(updateTodo))
						.accept(MediaType.APPLICATION_JSON)
					)
					.andExpect(status().is4xxClientError());
	}
	
	@Test
	@UsingDataSet(loadStrategy = LoadStrategyEnum.DELETE_ALL)
	public void testCreateNewTodo( ) throws Exception {
		final Todo newTodo = new Todo( "5", "New Todo", "This is a new Todo we just created", true);
				
		mvc.perform(post("/")
						.contentType(MediaType.APPLICATION_JSON)
						.content( mapper.writeValueAsString(newTodo))
						.accept(MediaType.APPLICATION_JSON)
					)
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.name", is(newTodo.getName())))
					.andExpect(jsonPath("$.whatToDo", is(newTodo.getWhatToDo())))
					.andExpect(jsonPath("$.completed", is(newTodo.getCompleted())));
		
		mvc.perform(get("/" + newTodo.getId())
						.accept(MediaType.APPLICATION_JSON)
					)
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.name", is(newTodo.getName())))
					.andExpect(jsonPath("$.whatToDo", is(newTodo.getWhatToDo())))
					.andExpect(jsonPath("$.completed", is(newTodo.getCompleted())));

	}
}