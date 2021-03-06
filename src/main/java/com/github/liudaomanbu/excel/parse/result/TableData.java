package com.github.liudaomanbu.excel.parse.result;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.liudaomanbu.excel.config.TableDataConfig;
import com.github.liudaomanbu.excel.parse.error.ValidationError;
import com.github.liudaomanbu.excel.util.ClassUtil;
import com.github.liudaomanbu.excel.util.ExcelUtil;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;

public class TableData {
  private final Table table;
  private final TableDataConfig config;
  private final ImmutableList<Map<Menu, StandardCell>> menuToValueCells;
  private final ImmutableList<ValidationError<TableData>> errors;

  public TableData(Table table) {
    this.table = table;
    this.config = table.getConfig().getDataConfig();
    List<Map<Menu, StandardCell>> menuTodatas = Lists.newArrayList();
    ImmutableList<Menu> menus = table.getDataMenus().collect(ImmutableList.toImmutableList());
    menus.forEach(menu -> {
      ImmutableList<StandardCell> valueCells = menu.getData().getValueCells();
      for (int j = 0; j < valueCells.size(); j++) {
        StandardCell valueCell = valueCells.get(j);

        Map<Menu, StandardCell> map = null;
        if (j < menuTodatas.size()) {
          map = menuTodatas.get(j);
        } else {
          map = Maps.newHashMap();
          menuTodatas.add(map);
        }
        map.put(menu, valueCell);
      }
    });

    menuToValueCells = menuTodatas
        .stream().filter(map -> map.values().stream().map(StandardCell::getValue)
            .filter(Objects::nonNull).findAny().isPresent())
        .collect(ImmutableList.toImmutableList());

    Stream<ValidationError<TableData>> menuMatcherErrors =
        menuToValueCells.stream().map(Map::entrySet).flatMap(Collection::stream)
            .flatMap(entry -> entry.getKey().getData().getConfig().getValidators().stream()
                .map(validator -> validator.validate(entry.getValue())).flatMap(Collection::stream)
                .map(error -> entry.getKey().getFullName() + error.getMessage()))
            .map(message -> new ValidationError<>(this, message));


    Stream<ValidationError<TableData>> tableDataMatcherErrors = Optional.ofNullable(config)
        .map(TableDataConfig::getValidators).orElse(ImmutableList.of()).stream()
        .flatMap(validator -> menuToValueCells.stream().map(map -> validator.validate(map))
            .flatMap(Collection::stream))
        .map(error -> new ValidationError<>(this, error.getMessage()));

    this.errors = Stream.concat(menuMatcherErrors, tableDataMatcherErrors)
        .collect(ImmutableList.toImmutableList());
  }

  public Table getTable() {
    return table;
  }

  public TableDataConfig getConfig() {
    return config;
  }

  public ImmutableList<Map<Menu, StandardCell>> getMenuToValueCells() {
    return menuToValueCells;
  }

  public ImmutableList<ValidationError<TableData>> getErrors() {
    return errors;
  }

  public <T> ImmutableList<T> getDatas(TypeToken<T> type) {
    return menuToValueCells.stream().map(map -> (T) ExcelUtil.toJavaObject(map, type.getRawType()))
        .collect(ImmutableList.toImmutableList());
  }

  public JSONArray getJsonDatas() {
    JSONArray array = new JSONArray();
    menuToValueCells.forEach(map -> array.add(ExcelUtil.toJsonObject(map)));
    return array;
  }
}
