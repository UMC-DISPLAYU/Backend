package com.example.demo.domain.display.domain.type;

public enum DisplayType {
  GRADUATION,
  ASSIGNMENTS,
  DEPARTMENTS,
  SMALL_GROUP,
  INTER_GROUP,
  OTHERS;

  public boolean supportsDepartment() {
    return this == GRADUATION || this == ASSIGNMENTS || this == DEPARTMENTS;
  }
}
