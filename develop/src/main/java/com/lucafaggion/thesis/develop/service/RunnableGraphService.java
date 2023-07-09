package com.lucafaggion.thesis.develop.service;

import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.lucafaggion.thesis.develop.graph.RunnableGraphEdge;
import com.lucafaggion.thesis.develop.model.RunnerTaskConfig;

@Service
public class RunnableGraphService {

  @Autowired
  private ThreadPoolTaskExecutor threadPoolTaskExecutor;

  public Graph<ListenableFutureTask<Object>, RunnableGraphEdge> createAcyclicGraphFromConfig(String compiledConfig) throws JsonMappingException, JsonProcessingException {
    Graph<ListenableFutureTask<Object>, RunnableGraphEdge> future_g = new DirectedAcyclicGraph<>(
        RunnableGraphEdge.class);

    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    RunnerTaskConfig runnerTaskConfig = mapper.readValue(compiledConfig, RunnerTaskConfig.class);
    
    return future_g;
  }

}
