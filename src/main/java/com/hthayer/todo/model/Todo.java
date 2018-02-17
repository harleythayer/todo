package com.hthayer.todo.model;

import org.springframework.data.annotation.Id;

public class Todo {

    @Id
    private String id;

    private String name;
    private String whatToDo;
    private boolean completed;

    public String getId( ) {
    	return id;
    }
    
    public void setId( String id ) {
    	this.id = id;
    }
    
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the whatToDo
	 */
	public String getWhatToDo() {
		return whatToDo;
	}

	/**
	 * @param whatToDo the whatToDo to set
	 */
	public void setWhatToDo(String whatToDo) {
		this.whatToDo = whatToDo;
	}

	/**
	 * @return the completed
	 */
	public boolean getCompleted() {
		return completed;
	}

	/**
	 * @param completed the completed to set
	 */
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

}