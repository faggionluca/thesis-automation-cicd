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
/*
 * Cerca un utente usando l'username (del servizio)
 * e il nome del servizio
 */
public class SearchUserAssociatedByUsernameAndService {
    @NonNull
    private String username;
    @NonNull
    private String serviceName; 
}
