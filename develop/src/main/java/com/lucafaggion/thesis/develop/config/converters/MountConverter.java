package com.lucafaggion.thesis.develop.config.converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.model.Mount;

@Component
public class MountConverter implements Converter<Mount, String> {

  @Autowired
  ObjectMapper mapper;

  @Override
  public String convert(Mount source) {
    try {
      return mapper.writeValueAsString(source);
    } catch (JsonProcessingException e) {
      return null;
    }

  }

}
