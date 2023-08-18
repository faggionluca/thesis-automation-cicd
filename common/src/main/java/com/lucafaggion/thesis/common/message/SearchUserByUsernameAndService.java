package com.lucafaggion.thesis.common.message;

import io.micrometer.common.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchUserByUsernameAndService {
  @NonNull
  private String username;
  @NonNull
  private String serviceName; 
}
