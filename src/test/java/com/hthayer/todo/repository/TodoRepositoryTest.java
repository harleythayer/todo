package com.hthayer.todo.repository;

import static com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder.newMongoDbRule;
import static org.assertj.core.api.Java6Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.lordofthejars.nosqlunit.annotation.UsingDataSet;
import com.lordofthejars.nosqlunit.core.LoadStrategyEnum;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;


// Need to start this maybe?

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class TodoRepositoryTest {

    @Rule
    public MongoDbRule mongoDbRule = newMongoDbRule().defaultSpringMongoDb("todo-testDb");

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TodoRepository todoRepository;

    @Test
    @UsingDataSet(locations = {"/testData/todoList.json"}, loadStrategy = LoadStrategyEnum.CLEAN_INSERT)
    public void TestGetAllCount() {

        long total = this.todoRepository.findAll().size();

        assertThat(total).isEqualTo(2);
    }
}