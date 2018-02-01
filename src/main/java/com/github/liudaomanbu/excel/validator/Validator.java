package com.github.liudaomanbu.excel.validator;

import com.github.liudaomanbu.excel.parse.error.ValidationError;
import com.google.common.collect.ImmutableCollection;

public interface Validator<T> {
  boolean needValidate(T value);

  ImmutableCollection<ValidationError<T>> validate(T value);
}
