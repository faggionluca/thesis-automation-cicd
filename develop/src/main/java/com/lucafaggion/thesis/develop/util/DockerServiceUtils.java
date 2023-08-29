package com.lucafaggion.thesis.develop.util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.MountType;
import com.lucafaggion.thesis.develop.model.RunnerContext;
import com.lucafaggion.thesis.develop.service.ContextService;

public class DockerServiceUtils {
  // private Set<InspectContainerResponse> linkedContainers;

  public static List<com.github.dockerjava.api.model.Mount> mounts(InspectContainerResponse container,
      Map<String, String> declaredMounts) {

    // List<com.github.dockerjava.api.model.Mount> res = new ArrayList<>();
    List<com.github.dockerjava.api.model.Mount> res = container.getMounts().stream().filter((mount) -> declaredMounts.containsValue(mount.getDestination().getPath()))
        .map((mount) -> new com.github.dockerjava.api.model.Mount()
            .withSource(mount.getSource()).withTarget(mount.getDestination().getPath())
            .withType(MountType.VOLUME))
        .collect(Collectors.toList());

    // for (Mount mount : container.getMounts()) {
    // com.github.dockerjava.api.model.Mount current = new
    // com.github.dockerjava.api.model.Mount()
    // .withSource(mount.getSource()).withTarget(mount.getDestination().getPath())
    // .withType(MountType.VOLUME);
    // res.add(current);
    // }
    return res;
  }

  public static String[] getCloneCMD(RunnerContext context) {
    return Stream
        .of(context.getVariable(ContextService.REPO_HOST), context.getVariable(ContextService.REPO_USER),
            context.getVariable(ContextService.REPO_TOKEN), context.getVariable(ContextService.REPO_URL))
        .toArray(String[]::new);
  }

}
