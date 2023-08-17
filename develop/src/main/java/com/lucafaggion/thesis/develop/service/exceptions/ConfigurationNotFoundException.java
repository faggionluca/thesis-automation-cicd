package com.lucafaggion.thesis.develop.service.exceptions;

public class ConfigurationNotFoundException extends RuntimeException {
  
  public ConfigurationNotFoundException(Throwable e) {
    super("Runner Configuration file not found, consider adding a config.yml in the repository root", e);
  }

}
