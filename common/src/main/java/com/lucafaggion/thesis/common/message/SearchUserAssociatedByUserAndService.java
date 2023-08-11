package com.lucafaggion.thesis.common.message;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchUserAssociatedByUserAndService {
    @NonNull
    private BigInteger id;
    @NonNull
    private String serviceName; 
}
