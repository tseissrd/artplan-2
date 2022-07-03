/*
 */
package com.example.test.app2.routers.error;

/**
 *
 * @author Sovereign
 */
public class AnimalServiceException
extends ServiceException {
  
  public static enum Type
  implements ServiceExceptionType {
    COULD_NOT_FIND_ANIMAL_FOR_ID,
    NAME_USED,
    ANIMAL_NOT_OWNED;

    @Override
    public String getCodeGroup() {
      return "ASE";
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
  
  public AnimalServiceException(Type type) {
    super(type);
  }
  
  public AnimalServiceException(Type type, String details) {
    super(type, details);
  }
  
}
