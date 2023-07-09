package com.lucafaggion.thesis.develop.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class RunnerTaskConfigService {
  
  @Autowired
  private SpringTemplateEngine templateEngine;

  public String compileTemplate(String configTemplate, Context context) {
    return this.templateEngine.process(configTemplate, context);
  }

  public String compileTemplate(File configPath, Context context) throws IOException {
    String template = Files.readString(configPath.toPath());
    return this.compileTemplate(template, context);
  }

}
