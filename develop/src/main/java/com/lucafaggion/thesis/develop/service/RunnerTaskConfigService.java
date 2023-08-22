package com.lucafaggion.thesis.develop.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucafaggion.thesis.develop.model.RunnerJob;
import com.lucafaggion.thesis.develop.model.RunnerTaskConfig;

@Service
public class RunnerTaskConfigService {
  
  @Autowired
  @Qualifier("yamlObjectMapper")
  private ObjectMapper mapper;

  @Autowired
  private SpringTemplateEngine templateEngine;

  public String compile(byte[] configTemplate, Context context) {
    return this.compile(new String(configTemplate), context);
  }

  public String compile(String configTemplate, Context context) {
    return this.templateEngine.process(configTemplate, context);
  }

  public String compile(File configPath, Context context) throws IOException {
    String template = Files.readString(configPath.toPath());
    return this.compile(template, context);
  }

  /**
   * Deserializza un oggetto RunnerTaskConfig 
   * @param config
   * @return un oggetto RunnerTaskConfig
   * @throws JsonMappingException
   * @throws JsonProcessingException
   */
  public RunnerTaskConfig from(byte[] config) throws JsonMappingException, JsonProcessingException {
    RunnerTaskConfig runnerTaskConfig = mapper.readValue(new String(config), RunnerTaskConfig.class);
    for (Entry<String, RunnerJob> jobEntry : runnerTaskConfig.getJobs().entrySet()) {
      jobEntry.getValue().setName(jobEntry.getKey()); // Set the name of the job
    }
    return runnerTaskConfig;
    // compileTemplate(config, contextService.getContext().toThymeleafContext())
  }

  public RunnerTaskConfig from(String config) throws JsonMappingException, JsonProcessingException {
    return this.from(config.getBytes());
  }

}
