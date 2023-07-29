package com.lucafaggion.thesis.develop.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

public class ModelFixtures {
  static String testDirectory = "src/test/resources/config/";
  static HashMap<String, String> configs = new HashMap<String,String>() {{}};
  static ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

  @BeforeAll
  static void setUpMapper() {
    mapper.registerModule(new Jdk8Module());
  }

  @BeforeAll
  static void loadConfigs() throws IOException {
    try (Stream<Path> stream = Files.list(Paths.get(testDirectory))) {
      stream
          .filter(file -> !Files.isDirectory(file))
          .forEachOrdered(file -> configs.put(file.getFileName().toString(), file.toString()));
    }
  }

  static String loadConfig(String name) throws IOException {
    String fileName = configs.get(name+".yaml");
    File configFile = new File(fileName);
    return Files.readAllLines(configFile.toPath()).stream().collect(Collectors.joining(System.lineSeparator()));
  }
}
