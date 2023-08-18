package com.lucafaggion.thesis.common.message;

import com.lucafaggion.thesis.common.model.UserAssociatedAccount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenForUserAssociatedOnService {
  @NonNull
  private UserAssociatedAccount userAssociatedAccount;
  @NonNull
  private String serviceName;
}
