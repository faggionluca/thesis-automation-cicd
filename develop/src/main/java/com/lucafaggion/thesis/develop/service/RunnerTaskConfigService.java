package com.lucafaggion.thesis.develop.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucafaggion.thesis.develop.model.RunnerTaskConfig;

@Service
public class RunnerTaskConfigService {
  
  @Autowired
  @Qualifier("yamlObjectMapper")
  private ObjectMapper mapper;

  @Autowired
  private ContextService contextService;

  @Autowired
  private SpringTemplateEngine templateEngine;

  public String compileTemplate(byte[] configTemplate, Context context) {
    return this.compileTemplate(new String(configTemplate), context);
  }

  public String compileTemplate(String configTemplate, Context context) {
    return this.templateEngine.process(configTemplate, context);
  }

  public String compileTemplate(File configPath, Context context) throws IOException {
    String template = Files.readString(configPath.toPath());
    return this.compileTemplate(template, context);
  }

  public RunnerTaskConfig from(byte[] config) throws JsonMappingException, JsonProcessingException {
    return mapper.readValue(compileTemplate(config, contextService.getContext()), RunnerTaskConfig.class);
  }

}
