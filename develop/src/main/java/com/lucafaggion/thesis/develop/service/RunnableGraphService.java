package com.lucafaggion.thesis.develop.service;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import org.bouncycastle.eac.EACCertificateRequestHolder;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.lucafaggion.thesis.develop.graph.RunnableGraphEdge;
import com.lucafaggion.thesis.develop.model.RunnerJob;
import com.lucafaggion.thesis.develop.model.RunnerTaskConfig;
import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.util.mxCellRenderer;

import java.awt.Color;
import java.awt.image.BufferedImage;

@Service
public class RunnableGraphService {

  @Autowired
  private ThreadPoolTaskExecutor threadPoolTaskExecutor;

  public Graph<RunnerJob, RunnableGraphEdge> createAcyclicGraphFromConfig(String compiledConfig)
      throws JsonMappingException, JsonProcessingException {
    Graph<RunnerJob, RunnableGraphEdge> future_g = new DirectedAcyclicGraph<>(
        RunnableGraphEdge.class);

    // Deserialize the YAML config file into a RunnerTaskConfig
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    mapper.registerModule(new Jdk8Module());
    RunnerTaskConfig runnerTaskConfig = mapper.readValue(compiledConfig, RunnerTaskConfig.class);

    // Construct the acyclic graph
    // Fist lets add all the vertex
    for (Entry<String, RunnerJob> jobEntry : runnerTaskConfig.getJobs().entrySet()) {
      jobEntry.getValue().setName(jobEntry.getKey()); // Set the name of the job
      future_g.addVertex(jobEntry.getValue());
    }

    for (RunnerJob job : future_g.vertexSet()) {
      for (String dependsOn : job.getDependsOn()) {
        RunnerJob dependJob = future_g.vertexSet()
            .stream()
            .filter(vertex -> dependsOn.equals(vertex.getName()))
            .findFirst()
            .orElse(null);
        if (dependJob != null) {
          future_g.addEdge(dependJob, job);
        }
      }
    }

    return future_g;
  }

  public <T, E> void saveGraphToImage(Graph<T, E> graph, String location) throws IOException {
    JGraphXAdapter<T, E> graphAdapter = new JGraphXAdapter<T, E>(graph);
    graphAdapter.getEdgeToCellMap().forEach((edge, cell) -> cell.setValue(null));
    mxIGraphLayout layout = new mxCompactTreeLayout(graphAdapter, false);
    layout.execute(graphAdapter.getDefaultParent());
    BufferedImage image = mxCellRenderer.createBufferedImage(graphAdapter, null, 2, new Color(0f, 0f, 0f, .5f), true,
        null);
    File imgFile = new File(location);
    ImageIO.write(image, "PNG", imgFile);
  }

}
