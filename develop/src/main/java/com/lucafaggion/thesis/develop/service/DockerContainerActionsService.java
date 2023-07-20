package com.lucafaggion.thesis.develop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PingCmd;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.transport.DockerHttpClient;
import com.lucafaggion.thesis.develop.model.RunnerAction;
import com.lucafaggion.thesis.develop.model.RunnerJob;

@Service
public class DockerContainerActionsService implements ContainerActionsService {

  @Autowired
  ContainerService<DockerClient, DockerHttpClient> docker;

  @Override
  public String runActionInContainer(RunnerAction action) {
    // TODO Auto-generated method stub
    System.out.println(this.docker.client().getClass().toString());
    DockerClient client = docker.client();
    List<Image> images = client.listImagesCmd().exec();
    System.out.println(images.toString());
    return images.toString();
  }

}
