package com.lucafaggion.thesis.develop.util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.MountType;
import com.lucafaggion.thesis.develop.model.RunnerContext;
import com.lucafaggion.thesis.develop.service.ContextService;

public class DockerServiceUtils {
  // private Set<InspectContainerResponse> linkedContainers;

  private final static Logger logger = LoggerFactory.getLogger(DockerServiceUtils.class);
  /**
   * Extract the List of mounts from a container to Link them to another
   * @param container
   * @param declaredMounts the list to filter
   */
  public static List<com.github.dockerjava.api.model.Mount> mounts(InspectContainerResponse container,
      Map<String, String> declaredMounts) {

    List<com.github.dockerjava.api.model.Mount> res = container.getMounts().stream().filter((mount) -> declaredMounts.containsValue(mount.getDestination().getPath()))
        .map((mount) -> new com.github.dockerjava.api.model.Mount()
            .withSource(mount.getName()).withTarget(mount.getDestination().getPath())
            .withType(MountType.VOLUME))
        .collect(Collectors.toList());
    logger.debug("Extracted mounts: {}, from {}", res, container.getMounts());
    return res;
  }

  /**
   * Creates the CMD Array for cloning the repo using a HelperContainer (gitcredhelper)
   * @param context
   */
  public static String[] getCloneCMD(RunnerContext context) {
    return Stream
        .of(context.getVariable(ContextService.REPO_HOST), context.getVariable(ContextService.REPO_USER),
            context.getVariable(ContextService.REPO_TOKEN), context.getVariable(ContextService.REPO_URL))
        .toArray(String[]::new);
  }

}
