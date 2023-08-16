package com.lucafaggion.thesis.develop.service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest.Headers;

import com.lucafaggion.thesis.common.config.AMQPCommonConfig;
import com.lucafaggion.thesis.common.message.SearchUserAssociatedByUserAndService;
import com.lucafaggion.thesis.common.model.UserAssociatedAccount;

public class APIInterceptor implements ClientHttpRequestInterceptor {

  private final static Logger logger = LoggerFactory.getLogger(APIInterceptor.class);
  public static final String AUTHORIZE_USER_HEADER = "AuthorizeUser";
  private final RabbitTemplate template;

  public APIInterceptor(RabbitTemplate rabbitTemplate) {
    this.template = rabbitTemplate;
  }

  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
      throws IOException {
    HttpHeaders headers = request.getHeaders();

    logger.debug("Intercepting ClientHttpRequest with Headers: {}", headers);
    // Aggiungiamo l'header accept se non ne abbiamo gia specificato uno
    if (headers.getAccept().isEmpty()) {
      request.getHeaders().setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }

    // Aggiungiamo il Bearer token se e' specificato l'header "AuthorizeUser"
    if (headers.containsKey(AUTHORIZE_USER_HEADER)) {
      List<String> authorizeUser = headers.get(AUTHORIZE_USER_HEADER);
      logger.debug("ClientHttpRequest has {} header with values: {}", AUTHORIZE_USER_HEADER, authorizeUser);
      if (authorizeUser.size() >= 2) {
        BigInteger user = new BigInteger(authorizeUser.get(0));
        String service = authorizeUser.get(1);
        SearchUserAssociatedByUserAndService search = SearchUserAssociatedByUserAndService.builder().id(user)
            .serviceName(service).build();
        Optional<UserAssociatedAccount> userAssociatedAccount = Optional
            .of((UserAssociatedAccount) template.convertSendAndReceive(AMQPCommonConfig.USER_EXCHANGE,
                AMQPCommonConfig.SEARCH_USER_ASSOCIATED_FROM_USER_ID_ROUTE_KEY,
                search));
        if (userAssociatedAccount.isPresent()) {
          logger.debug("Found Bearer token for {}", authorizeUser);
          headers.setBearerAuth(userAssociatedAccount.get().getToken());
        }
      }
    }
    ClientHttpResponse response = execution.execute(request, body);
    return response;
  }

}
