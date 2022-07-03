package com.example.test.app2.persistence.model;

/**
 *
 * @author Anechka
 */
public class DuplicateEntryCreationAttemptException
extends RuntimeException {
  public DuplicateEntryCreationAttemptException() {
    super();
  }
  public DuplicateEntryCreationAttemptException(String message) {
    super(message);
  }
}
