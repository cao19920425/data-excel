package com.caotc.excel4j.matcher;

import java.util.function.Function;
import com.caotc.excel4j.matcher.constant.ComparableMatcherType;

public class ComparableMatcher<T extends Comparable<T>> extends BaseMatcher<T> {

  public ComparableMatcher<T> add(ComparableMatcherType type, T predicateValue) {
    add(type, predicateValue, Function.identity());
    return this;
  }

  public ComparableMatcher<T> lt(T predicateValue) {
    add(ComparableMatcherType.LT, predicateValue);
    return this;
  }

  public ComparableMatcher<T> le(T predicateValue) {
    add(ComparableMatcherType.LE, predicateValue);
    return this;
  }

  public ComparableMatcher<T> gt(T predicateValue) {
    add(ComparableMatcherType.GT, predicateValue);
    return this;
  }

  public ComparableMatcher<T> ge(T predicateValue) {
    add(ComparableMatcherType.GE, predicateValue);
    return this;
  }

  public ComparableMatcher<T> eq(T predicateValue) {
    add(ComparableMatcherType.EQ, predicateValue);
    return this;
  }

  public ComparableMatcher<T> ne(T predicateValue) {
    add(ComparableMatcherType.NE, predicateValue);
    return this;
  }

  // TODO between和其他type对于参数个数不同要求的处理?
  public ComparableMatcher<T> between(T lowValue, T highValue) {
    add(value -> ComparableMatcherType.LE.apply(value, highValue)
        && ComparableMatcherType.GE.apply(value, lowValue));
    return this;
  }

}
