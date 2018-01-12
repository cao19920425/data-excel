package com.caotc.excel4j.parse.error;

public class Error<T> {
  private final T cause;
  private final String message;

  public Error(T cause, String message) {
    super();
    this.cause = cause;
    this.message = message;
  }

  public T getCause() {
    return cause;
  }

  public String getMessage() {
    return message;
  }

}
