package com.hthayer.todo.controllers;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hthayer.todo.exceptions.NotFoundException;
import com.hthayer.todo.model.Todo;
import com.hthayer.todo.repository.TodoRepository;

@RestController
@RequestMapping(value = "/")
public class TodoController {

	private final TodoRepository todoRepository;

	@Autowired
	public TodoController(TodoRepository todoRepository) {
		this.todoRepository = todoRepository;
	}

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Todo> getAllTodo() {
		return todoRepository.findAll();
	}

	@RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Todo createTodo(@RequestBody final Todo todo) {
		return todoRepository.save(todo);
	}

	@RequestMapping(value = "{requestId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Todo getTodo(@PathVariable final String requestId) throws NotFoundException {
		final Todo todo = todoRepository.findOne(requestId);
		if (todo == null) {
			throw new NotFoundException("Todo with id:" + requestId + " was not found");
		}
		return todo;
	}

	@RequestMapping(value = "{requestId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Todo updateTodo(@PathVariable final String requestId, @RequestBody Todo todo) throws NotFoundException {
		final Todo oldTodo = todoRepository.findOne(requestId);
		if (oldTodo == null) {
			throw new NotFoundException("Todo with id:" + requestId + " was not found");
		}
		BeanUtils.copyProperties(todo, oldTodo);
		return todoRepository.save(oldTodo);
	}

	@RequestMapping(value = "{requestId}", method = RequestMethod.DELETE)
	public void delete(@PathVariable final String requestId) {
		todoRepository.delete(requestId);
	}

}
