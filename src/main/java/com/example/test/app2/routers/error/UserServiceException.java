/*
 */
package com.example.test.app2.routers.error;

/**
 *
 * @author tseissrd
 */
public class UserServiceException
extends ServiceException {
  
  public static enum Type
  implements ServiceExceptionType {
    USER_EXISTS,
    AUTHENTICATION_FAILURE;

    @Override
    public String getCodeGroup() {
      return "USE";
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
  
  public UserServiceException(Type type) {
    super(type);
  }
  
  public UserServiceException(Type type, String details) {
    super(type, details);
  }
  
}
