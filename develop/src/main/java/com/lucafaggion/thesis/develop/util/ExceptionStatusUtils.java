package com.lucafaggion.thesis.develop.util;

import java.util.Date;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.lucafaggion.thesis.develop.model.CurrentStatus;
import com.lucafaggion.thesis.develop.model.enums.Status;

/*
 * Classe di Aiuto per generare istanze di CurrentStatus
 */
public class ExceptionStatusUtils {

  public static CurrentStatus fromThrowable(Throwable e) {
    CurrentStatus status = CurrentStatus.builder()
        .status(Status.ERROR)
        .errorClass(e.getClass().getName())
        .errorMessage(e.getMessage())
        .rootCause(ExceptionUtils.getRootCause(e).getMessage())
        .rootCauseClass(ExceptionUtils.getRootCause(e).getClass().getName())
        .updated(new Date())
        .build();
    return status;
  }

  public static CurrentStatus defaultStatus() {
    CurrentStatus status = CurrentStatus.builder()
        .status(Status.CREATED)
        .updated(new Date())
        .build();
    return status;
  }
}
