package com.caotc.excel4j.validator;

import com.caotc.excel4j.parse.error.ValidationError;
import com.google.common.collect.ImmutableCollection;

public interface Validator<T>{
  ImmutableCollection<ValidationError<T>> validate(T object);
}