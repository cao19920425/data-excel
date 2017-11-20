package com.caotc.excel4j.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Table;
import com.google.common.reflect.TypeToken;

public class ClassUtil extends org.apache.commons.lang3.ClassUtils {
  private static final ImmutableCollection<Class<?>> COLLECTORS =
      ImmutableSet.of(Iterable.class, Map.class, Multimap.class, Table.class);

  /**
   * get all fields of the Class.
   * 
   * @param <T>
   * 
   * @param type Class Object
   * @return all fields of the Class
   */
  public static ImmutableCollection<Field> getAllFields(Class<?> type) {
    return getAllFields(TypeToken.of(type));
  }

  public static ImmutableCollection<Field> getAllFields(TypeToken<?> token) {
    return FluentIterable.from(token.getTypes().classes().rawTypes())
        .transform(Class::getDeclaredFields).transformAndConcat(Arrays::asList).toSet();
  }

  public static ImmutableMultimap<String, Field> getNameToFields(Class<?> type) {
    return getNameToFields(TypeToken.of(type));
  }

  public static ImmutableMultimap<String, Field> getNameToFields(TypeToken<?> token) {
    return Multimaps.index(getAllFields(token), Field::getName);
  }

  public static Optional<Field> getField(Class<?> type, String fieldName) {
    return getField(TypeToken.of(type), fieldName);
  }

  public static Optional<Field> getField(TypeToken<?> token, String fieldName) {
    return Optional
        .ofNullable(Iterables.getOnlyElement(getNameToFields(token).get(fieldName), null));
  }

  public static boolean isCollector(Class<?> type) {
    return isCollector(TypeToken.of(type));
  }

  public static boolean isCollector(TypeToken<?> token) {
    return token.isArray() || Iterables.any(COLLECTORS, token::isSubtypeOf);
  }

  @SuppressWarnings("unchecked")
  public static <T> Optional<Constructor<T>> getDefaultConstructor(Class<T> type) {
    return Arrays.stream(type.getDeclaredConstructors())
        .filter(constructor -> constructor.getParameterTypes().length == 0).findAny()
        .map(constructor -> (Constructor<T>) constructor);// TODO

    // TODO
    // if (defaultConstructor == null) {
    // if (type.isMemberClass() && !Modifier.isStatic(type.getModifiers())) {
    // Class<?>[] types;
    // for (Constructor<?> constructor : constructors) {
    // if ((types = constructor.getParameterTypes()).length == 1
    // && types[0].equals(type.getDeclaringClass())) {
    // defaultConstructor = (Constructor<T>) constructor;
    // break;
    // }
    // }
    // }
    // }
  }

  private ClassUtil() {
    throw new AssertionError();
  }
}