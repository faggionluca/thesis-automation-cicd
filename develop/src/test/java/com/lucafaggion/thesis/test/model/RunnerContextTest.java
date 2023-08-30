package com.lucafaggion.thesis.test.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.DefaultBaseTypeLimitingValidator;
import com.lucafaggion.thesis.develop.model.RunnerContext;

public class RunnerContextTest extends RepoPushEventTest {

  protected ObjectMapper objectMapper;
  protected RunnerContext context;

  @BeforeEach
  void setUpContextService() {
    objectMapper = new ObjectMapper();
    objectMapper.activateDefaultTyping(new DefaultBaseTypeLimitingValidator(), ObjectMapper.DefaultTyping.EVERYTHING,
        As.PROPERTY);
    context = new RunnerContext();
  }

  @Test
  void serializeASimpleContextObject() throws JsonProcessingException {

    context.setVariable("test", "testing contex deserialization");

    String serializeContext = objectMapper.writeValueAsString(context);

    assertNotNull(serializeContext, "Serialization should be successfull");
  }

  @Test
  void serializeAComplexContextObject() throws JsonProcessingException {

    context.setVariable("test", "testing contex deserialization");
    context.setVariable("repo_push_event", repoPushEvent);

    String serializeContext = objectMapper.writeValueAsString(context);

    assertNotNull(serializeContext, "Serialization should be successfull");
  }

  @Test
  void copyContextObject() throws JsonMappingException, JsonProcessingException {
    context.setVariable("test", "testing contex deserialization");
    context.setVariable("repo_push_event", repoPushEvent);

    RunnerContext contextCopy = context.copy();

    assertFalse(context.equals(contextCopy), "The copied context should not be equal to the original");
  }
}
