package com.caotc.excel4j.matcher;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import com.caotc.excel4j.matcher.constant.ComparableMatcherType;
import com.caotc.excel4j.matcher.constant.StringMatcherType;
import com.caotc.excel4j.matcher.constant.Type;
import com.caotc.excel4j.script.ScriptEngine;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class BaseMatcher<T> implements Matcher<T> {
  public static class Builder<T> implements Matcher.Builder<T> {
    private Matcher<T> realParent;
    private Type type;
    private Builder<T> parent;
    private String message;
    private Function<T, String> messageFunction;
    private Boolean nonNull;
    private Boolean isNull;
    private List<String> scripts;
    private List<Predicate<T>> predicates = Lists.newArrayList();

    @Override
    public boolean test(T t) {
      return type.apply(predicates).test(t);
    }

    @Override
    public BaseMatcher<T> build() {
      return new BaseMatcher<T>(this);
    }

    @Override
    public Builder<T> add(Predicate<T> predicate) {
      predicates.add(predicate);
      return this;
    }

    @Override
    public Builder<T> add(Matcher.Builder<T> builder) {
      predicates.add(builder);
      return this;
    }

    @Override
    public <R> Builder<T> add(Predicate<R> predicate, Function<T, R> transform) {
      return add(value -> predicate.test(transform.apply(value)));
    }

    @Override
    public Builder<T> add(StringMatcherType type, String predicateValue,
        Function<T, String> transform) {
      return add(value -> type.apply(value, predicateValue), transform);
    }

    @Override
    public <R extends Comparable<R>> Builder<T> add(ComparableMatcherType type, R predicateValue,
        Function<T, R> transform) {
      return add(value -> type.apply(value, predicateValue), transform);
    }

    public Builder<T> stratJunction(Type type) {
      Builder<T> builder = new Builder<T>().setType(type);
      add(builder);
      return builder;
    }

    public Builder<T> endJunction(Type type) {
      return Objects.equals(this.type, type) ? Optional.ofNullable(parent).orElse(this) : this;
    }

    @Override
    public Builder<T> and() {
      return stratJunction(Type.AND);
    }

    @Override
    public Builder<T> or() {
      return stratJunction(Type.OR);
    }

    @Override
    public Builder<T> endAnd() {
      return endJunction(Type.AND);
    }

    @Override
    public Builder<T> endOr() {
      return endJunction(Type.OR);
    }

    public Boolean getNonNull() {
      return nonNull;
    }

    public Builder<T> setNonNull(Boolean nonNull) {
      this.nonNull = nonNull;
      return this;
    }

    public Boolean getIsNull() {
      return isNull;
    }

    public Builder<T> setIsNull(Boolean isNull) {
      this.isNull = isNull;
      return this;
    }

    public List<String> getScripts() {
      return scripts;
    }

    public Builder<T> setScripts(List<String> scripts) {
      this.scripts = scripts;
      return this;
    }

    public Type getType() {
      return type;
    }

    public Builder<T> setType(Type type) {
      this.type = type;
      return this;
    }

    public Builder<T> getParent() {
      return parent;
    }

    public Builder<T> setParent(Builder<T> parent) {
      this.parent = parent;
      return this;
    }

    public String getMessage() {
      return message;
    }

    public Builder<T> setMessage(String message) {
      this.message = message;
      return this;
    }

    public Function<T, String> getMessageFunction() {
      return messageFunction;
    }

    public Builder<T> setMessageFunction(Function<T, String> messageFunction) {
      this.messageFunction = messageFunction;
      return this;
    }

    public List<Predicate<T>> getPredicates() {
      return predicates;
    }

    public Builder<T> setPredicates(List<Predicate<T>> predicates) {
      this.predicates = predicates;
      return this;
    }

  }

  public static final Type DEFAULT_TYPE = Type.AND;
  public static final String SCRIPT_VALUE_KEY = "value";

  private final Type type;
  private final Matcher<T> parent;
  private final ImmutableList<Predicate<T>> predicates;
  private final Function<T, String> messageFunction;

  protected BaseMatcher(Builder<T> builder) {
    this.type = Optional.ofNullable(builder.type).orElse(DEFAULT_TYPE);
    this.parent = builder.realParent;
    this.messageFunction =
        Optional.ofNullable(builder.messageFunction).orElse(Optional.ofNullable(builder.message).map(t->{
          Function<T, String> function=value->t;
          return function;
        }).orElse(null));
    ImmutableList.Builder<Predicate<T>> predicates = ImmutableList.builder();
    if (Objects.nonNull(builder.isNull) && builder.isNull) {
      predicates.add(Objects::isNull);
    }
    if (Objects.nonNull(builder.nonNull) && builder.nonNull) {
      predicates.add(Objects::nonNull);
    }
    if (Objects.nonNull(builder.scripts)) {
      builder.scripts.stream().map(ScriptEngine::compile).map(expression -> {
        Predicate<T> predicate = value -> (Boolean) expression
            .execute(ImmutableMap.<String, Object>builder().put(SCRIPT_VALUE_KEY, value).build());
        return predicate;
      }).forEach(predicates::add);
    }
    if (Objects.nonNull(builder.predicates)) {
      builder.predicates.stream().map(p -> {
        if (p instanceof Builder) {
          Builder<T> b = (Builder<T>) p;
          b.realParent = this;
          return b.build();
        }
        return p;
      }).forEach(predicates::add);
    }
    this.predicates = predicates.build();
    // TODO tip?
    Preconditions.checkState(!this.predicates.isEmpty());
  }

  @Override
  public boolean test(T t) {
    return type.apply(predicates).test(t);
  }

  public Type getType() {
    return type;
  }

  public Matcher<T> getParent() {
    return parent;
  }

  public ImmutableList<Predicate<T>> getPredicates() {
    return predicates;
  }

  public Function<T, String> getMessageFunction() {
    return messageFunction;
  }

  @Override
  public Optional<String> match(T value) {
    return type.apply(this, value);
  }

  public Function<T, String> getEffectiveMessageFunction(){
    return Optional.ofNullable(messageFunction).orElse(Optional.ofNullable(parent)
        .map(Matcher::getEffectiveMessageFunction).orElse(null));
  }
  
}
