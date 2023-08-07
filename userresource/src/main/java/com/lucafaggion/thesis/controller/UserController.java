
package com.lucafaggion.thesis.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * UserController
 */
@RestController
public class UserController {

  private final static Logger logger = LoggerFactory.getLogger(UserController.class);

  @DeleteMapping("/user/delete")
  void deleteUser() {
    logger.debug("user/delete");
  }
}