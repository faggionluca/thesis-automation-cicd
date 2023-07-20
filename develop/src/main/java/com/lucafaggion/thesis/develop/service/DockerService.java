package com.lucafaggion.thesis.develop.service;

import java.time.Duration;

import org.springframework.stereotype.Service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;

@Service
public class DockerService implements ContainerService<DockerClient, DockerHttpClient> {

  private DockerClientConfig config;
  private DockerHttpClient httpClient;

  public DockerService() {
    this.config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
    this.httpClient = new ApacheDockerHttpClient.Builder()
        .dockerHost(config.getDockerHost())
        .sslConfig(config.getSSLConfig())
        .maxConnections(100)
        .connectionTimeout(Duration.ofSeconds(5))
        .responseTimeout(Duration.ofSeconds(5))
        .build();
  }

  @Override
  public DockerClient client() {
    DockerClient client = DockerClientImpl.getInstance(this.config, this.httpClient);
    return client;
  }

  @Override
  public DockerHttpClient http() {
    return this.httpClient;
  }
}
