package com.lucafaggion.thesis.develop.model;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

import lombok.Getter;
import lombok.NonNull;

/**
 * A Context used for holding data during the execution of
 * the RunnerAction
 */
@Getter
public class RunnerContext implements IContext {

  private Map<String, Object> variables;
  private Locale locale;

  public RunnerContext() {
    this(null, null);
  }

  public RunnerContext(final Locale locale, final Map<String, Object> variables) {
    super();
    this.locale = (locale == null ? Locale.getDefault() : locale);
    this.variables = (variables == null ? new LinkedHashMap<String, Object>(10)
        : new LinkedHashMap<String, Object>(variables));

  }

  @Override
  public final boolean containsVariable(final String name) {
    return this.variables.containsKey(name);
  }

  @Override
  @JsonIgnore
  public final Set<String> getVariableNames() {
    return this.variables.keySet();
  }

  @Override
  @JsonIgnore
  public final Object getVariable(final String name) {
    return this.variables.get(name);
  }

  @JsonIgnore
  public final <T> T getVariableAs(final String name) {
    return (T)this.variables.get(name);
  }

  /**
   * <p>
   * Sets the locale to be used.
   * </p>
   *
   * @param locale the locale.
   */
  public void setLocale(@NonNull final Locale locale) {
    this.locale = locale;
  }

  /**
   * <p>
   * Sets a new variable into the context.
   * </p>
   *
   * @param name  the name of the variable.
   * @param value the value of the variable.
   */
  public void setVariable(final String name, final Object value) {
    this.variables.put(name, value);
  }

  /**
   * <p>
   * Sets several variables at a time into the context.
   * </p>
   *
   * @param variables the variables to be set.
   */
  public void setVariables(final Map<String, Object> variables) {
    if (variables == null) {
      return;
    }
    this.variables.putAll(variables);
  }

  /**
   * <p>
   * Removes a variable from the context.
   * </p>
   *
   * @param name the name of the variable to be removed.
   */
  public void removeVariable(final String name) {
    this.variables.remove(name);
  }

  /**
   * <p>
   * Removes all the variables from the context.
   * </p>
   */
  public void clearVariables() {
    this.variables.clear();
  }

  public RunnerContext copy() throws JsonMappingException, JsonProcessingException {
    // configura ObjectMapper per serializzazione e deserializzazione
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(Include.NON_NULL);
    objectMapper.activateDefaultTyping(new LaissezFaireSubTypeValidator(), ObjectMapper.DefaultTyping.EVERYTHING,
        As.PROPERTY);
    return objectMapper.readValue(objectMapper.writeValueAsString(this), RunnerContext.class);
  }

  public Context toThymeleafContext() {
    return new Context(this.locale, this.variables);
  }

  @Override
  public String toString() {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      return "";
    }
  }

  public String toTypedString() {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.setSerializationInclusion(Include.NON_NULL);
      objectMapper.activateDefaultTyping(new LaissezFaireSubTypeValidator(), ObjectMapper.DefaultTyping.EVERYTHING,
          As.PROPERTY);
      return objectMapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      return "";
    }
  }

}
