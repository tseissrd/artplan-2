/*
 */
package com.example.test.app2.routers.error;

import java.util.Map;

/**
 *
 * @author tseissrd
 */
public class ServiceException
extends Exception {
  
  public static interface ServiceExceptionType {
    public String getCodeGroup();
    public int getCode();
    public String getMessage();
  }
  
  public static enum Type
  implements ServiceExceptionType {
    REQUIRED_PARAMETERS_MISSING,
    USER_UNAUTHORIZED;
    
    @Override
    public String getCodeGroup() {
      return "SVCE";
    }
    
    @Override
    public int getCode() {
      return ordinal() + 1;
    }

    @Override
    public String getMessage() {
      return name();
    }
  }
  
  private final String code;
  private final String exceptionString;
  
  public ServiceException(Type type) {
    this((ServiceExceptionType)type);
  }
  
  public ServiceException(Type type, String details) {
    this((ServiceExceptionType)type, details);
  }
  
  protected ServiceException(ServiceExceptionType type) {
    this(type.getCodeGroup(), type.getCode(), type.getMessage());
  }
  
  protected ServiceException(ServiceExceptionType type, String details) {
    this(
      type.getCodeGroup(),
      type.getCode(),
      type.getMessage(),
      details
    );
  }
  
  protected static String completeCode(String codeGroup, int code) {
    return String.format("%s-%d", codeGroup, code);
  }
  
  protected static String constructExceptionString(String message) {
    return message;
  }
  
  protected static String constructExceptionString(String message, String details) {
    return String.format("%s (%s)", message, details);
  }
  
  protected static String constructMessage(
    String codeGroup,
    int code,
    String message
  ) {
    return String.format(
      "%s-%d: %s",
      codeGroup,
      code,
      message
    );
  }
  
  protected static String constructMessage(
    String codeGroup,
    int code,
    String message,
    String details
  ) {
    return String.format(
      "%s-%d: %s (%s)",
      codeGroup,
      code,
      message,
      details
    );
  }

  public ServiceException(
    String codeGroup,
    int code,
    String message
  ) {
    super(
      constructMessage(
        codeGroup,
        code,
        message
      )
    );
    
    this.code = completeCode(codeGroup, code);
    this.exceptionString = constructExceptionString(message);
  }
  
  public ServiceException(
    String codeGroup,
    int code,
    String message,
    String details
  ) {
    super(
      constructMessage(
        codeGroup,
        code,
        message,
        details
      )
    );
    
    this.code = completeCode(codeGroup, code);
    this.exceptionString = constructExceptionString(message, details);
  }
  
  public String getCode() {
    return this.code;
  }
  
  public String getExceptionString() {
    return this.exceptionString;
  }
  
  public Map toBody() {
    return Map.of(
      "status", "error",
      "code", getCode(),
      "message", getExceptionString()
    );
  }
  
}
