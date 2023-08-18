package com.lucafaggion.thesis.service.interfaces;

import com.lucafaggion.thesis.common.model.UserAssociatedAccount;

public interface UserAssociatedAccountService {

  public boolean forService(String serviceName);

  public UserAssociatedAccount refreshTokenFor(UserAssociatedAccount account);

}