package com.lucafaggion.thesis.develop.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.lucafaggion.thesis.develop.repository.RepoEventRepository;

public class WebhookGitHubService {
  
  @Autowired
  RepoEventRepository repoEventRepository;

  void deserializePayload() {
    
  }

}
