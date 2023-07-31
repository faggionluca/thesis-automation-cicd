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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

/**
 * Classe base per gli Unit Test
 * Implemente metodi e annotation per ridurre la Code Duplication
 */
public class ModelFixtures {
  static String testDirectory = "src/test/resources/config/";
  static HashMap<String, String> configs = new HashMap<String,String>() {{}};
  static ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

  @BeforeAll
  static void setUpMapper() {
    mapper.registerModule(new Jdk8Module());
  }

  /**
   * Crea una HashMap di file presenti nella cartella /src/test/resources/config
   * indicizzati dal nome del file senza estensione
   * @throws IOException
   */
  @BeforeAll
  static void loadConfigs() throws IOException {
    try (Stream<Path> stream = Files.list(Paths.get(testDirectory))) {
      stream
          .filter(file -> !Files.isDirectory(file))
          .forEachOrdered(file -> configs.put(file.getFileName().toString(), file.toString()));
    }
  }

  /**
   * Metodo per caricare semplicemente un file dalla cartella /src/test/resources/config
   * @param name nome della configurazione da caricaricare (senza estensione)
   * @return il contenuto del file
   * @throws IOException
   */
  static String loadConfig(String name) throws IOException {
    String fileName = configs.get(name + ".yaml");
    File configFile = new File(fileName);
    return Files.readAllLines(configFile.toPath()).stream().collect(Collectors.joining(System.lineSeparator()));
  }

  /**
   * Utilizzabile per il debug, mettedo un breakpoint nel proprio IDE
   */
  @AfterAll
  static void afterAllDebug() {
    System.out.println("AfterAllDebug");
  }
}
