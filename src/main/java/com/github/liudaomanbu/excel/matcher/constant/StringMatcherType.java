package com.github.liudaomanbu.excel.matcher.constant;

import java.util.Objects;

public enum StringMatcherType{
  EQUALS {
    @Override
    public boolean apply(String value, String predicateValue) {
      return Objects.nonNull(value) && value.equals(predicateValue);
    }
  },
  EQUALS_IGNORE_CASE {
    @Override
    public boolean apply(String value, String predicateValue) {
      return Objects.nonNull(value) && value.equalsIgnoreCase(predicateValue);
    }
  },
  CONTAINS {
    @Override
    public boolean apply(String value, String predicateValue) {
      return Objects.nonNull(value) && value.contains(predicateValue);
    }
  },
  MATCHES {
    @Override
    public boolean apply(String value, String predicateValue) {
      return Objects.nonNull(value) && value.matches(predicateValue);
    }
  },
  STARTS_WITH {
    @Override
    public boolean apply(String value, String predicateValue) {
      return Objects.nonNull(value) && value.startsWith(predicateValue);
    }
  },
  ENDS_WITH {
    @Override
    public boolean apply(String value, String predicateValue) {
      return Objects.nonNull(value) && value.endsWith(predicateValue);
    }
  };

  public abstract boolean apply(String value, String predicateValue);
}
