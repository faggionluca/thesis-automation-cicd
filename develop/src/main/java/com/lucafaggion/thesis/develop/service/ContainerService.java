package com.lucafaggion.thesis.develop.service;

public interface ContainerService<C,H> {
  public C client();

  public H http();
}
