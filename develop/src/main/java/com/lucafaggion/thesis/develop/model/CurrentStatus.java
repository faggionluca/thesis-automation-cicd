package com.lucafaggion.thesis.develop.model;

import java.util.Date;

import com.lucafaggion.thesis.develop.model.enums.Status;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Getter
@Table(name = "status")
public class CurrentStatus {
  
  @Enumerated(EnumType.STRING)
  private Status status;
  private Date updated;

  private String errorMessage;
  private String errorClass;
  private String rootCause;
  private String rootCauseClass;
}
