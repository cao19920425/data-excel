package com.caotc.excel4j.config;

import java.util.Collection;
import org.apache.commons.collections4.CollectionUtils;
import com.caotc.excel4j.constant.Direction;
import com.caotc.excel4j.constant.LoadType;
import com.caotc.excel4j.constant.MenuNecessity;
import com.caotc.excel4j.constant.MenuType;
import com.caotc.excel4j.matcher.data.DataMatcher;
import com.caotc.excel4j.matcher.usermodel.StandardCellMatcher;
import com.caotc.excel4j.parse.result.Menu;
import com.caotc.excel4j.parse.result.StandardCell;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.Iterables;

public class MenuConfig {
  public static class Builder {
    private TableConfig tableConfig;
    // 菜单匹配器
    private StandardCellMatcher menuMatcher;
    // 第一个数据单元格相对于菜单单元格的单元格距离
    private int distance;
    private MenuNecessity menuNecessity;
    private Direction direction;
    // 属性名字
    private String fieldName;
    private MenuType menuType;
    private MenuConfig parentMenuConfig;
    private ImmutableCollection<MenuConfig> childrenMenuConfigs;
    private DataConfig dataConfig;

    public MenuConfig build() {
      Preconditions.checkState(tableConfig != null || parentMenuConfig != null);
      Preconditions.checkNotNull(menuMatcher);
      Preconditions.checkNotNull(menuNecessity);
      Preconditions.checkState(direction != null || parentMenuConfig != null);
      Preconditions.checkNotNull(menuType);
      Preconditions.checkState(CollectionUtils.isEmpty(childrenMenuConfigs) || dataConfig != null);
      // TODO

      if (parentMenuConfig != null) {
        if (direction == null) {
          direction = parentMenuConfig.direction;
        }
        if (tableConfig == null) {
          tableConfig = parentMenuConfig.tableConfig;
        }
      }
      return new MenuConfig(this);
    }

    public TableConfig getTableConfig() {
      return tableConfig;
    }

    public Builder setTableConfig(TableConfig tableConfig) {
      this.tableConfig = tableConfig;
      return this;
    }

    public StandardCellMatcher getMenuMatcher() {
      return menuMatcher;
    }

    public Builder setMenuMatcher(StandardCellMatcher menuMatcher) {
      this.menuMatcher = menuMatcher;
      return this;
    }

    public int getDistance() {
      return distance;
    }

    public Builder setDistance(int distance) {
      this.distance = distance;
      return this;
    }

    public MenuNecessity getMenuNecessity() {
      return menuNecessity;
    }

    public Builder setMenuNecessity(MenuNecessity menuNecessity) {
      this.menuNecessity = menuNecessity;
      return this;
    }

    public Direction getDirection() {
      return direction;
    }

    public Builder setDirection(Direction direction) {
      this.direction = direction;
      return this;
    }

    public String getFieldName() {
      return fieldName;
    }

    public Builder setFieldName(String fieldName) {
      this.fieldName = fieldName;
      return this;
    }

    public MenuType getMenuType() {
      return menuType;
    }

    public Builder setMenuType(MenuType menuType) {
      this.menuType = menuType;
      return this;
    }

    public MenuConfig getParentMenuConfig() {
      return parentMenuConfig;
    }

    public Builder setParentMenuConfig(MenuConfig parentMenuConfig) {
      this.parentMenuConfig = parentMenuConfig;
      return this;
    }

    public ImmutableCollection<MenuConfig> getChildrenMenuConfigs() {
      return childrenMenuConfigs;
    }

    public Builder setChildrenMenuConfigs(ImmutableCollection<MenuConfig> childrenMenuConfigs) {
      this.childrenMenuConfigs = childrenMenuConfigs;
      return this;
    }

    public DataConfig getDataConfig() {
      return dataConfig;
    }

    public Builder setDataConfig(DataConfig dataConfig) {
      this.dataConfig = dataConfig;
      return this;
    }

  }

  private static final int DEFAULT_DISTANCE = 1;
  private static final MenuNecessity DEFAULT_MENU_NECESSITY = MenuNecessity.MUST;

  public static Builder builder() {
    return new Builder().setDistance(DEFAULT_DISTANCE).setMenuNecessity(DEFAULT_MENU_NECESSITY);
  }

  private final TableConfig tableConfig;
  // 菜单匹配器
  private final StandardCellMatcher menuMatcher;
  // 第一个数据单元格相对于菜单单元格的单元格距离
  private final int distance;
  private final MenuNecessity menuNecessity;
  private final Direction direction;
  // 属性名字
  private final String fieldName;
  private final MenuType menuType;
  private final MenuConfig parentMenuConfig;
  private final ImmutableCollection<MenuConfig> childrenMenuConfigs;
  private final DataConfig dataConfig;

  public MenuConfig(Builder builder) {
    tableConfig = builder.tableConfig;
    menuMatcher = builder.menuMatcher;
    distance = builder.distance;
    menuNecessity = builder.menuNecessity;
    direction = builder.direction;
    fieldName = builder.fieldName;
    menuType = builder.menuType;
    parentMenuConfig = builder.parentMenuConfig;
    childrenMenuConfigs = builder.childrenMenuConfigs;
    dataConfig = builder.dataConfig;
  }

  public boolean isTopMenu() {
    return parentMenuConfig == null;
  }

  public boolean isMustMenu() {
    return MenuNecessity.MUST.equals(menuNecessity);
  }

  public boolean isNotMustMenu() {
    return MenuNecessity.NOT_MUST.equals(menuNecessity);
  }

  public boolean isDataMenu() {
    return Iterables.isEmpty(childrenMenuConfigs);
  }

  public boolean isFixedDataMenu() {
    return isDataMenu() && LoadType.FIXED.equals(dataConfig.getLoadType());
  }

  public boolean isUnFixedDataMenu() {
    return isDataMenu() && LoadType.UNFIXED.equals(dataConfig.getLoadType());
  }

  public boolean isMixedDataMenu() {
    return isDataMenu() && LoadType.MIXED.equals(dataConfig.getLoadType());
  }

  // delegate methods start

  public boolean matches(StandardCell cell) {
    return menuMatcher.matches(cell);
  }

  public boolean matches(Object value) {
    return dataConfig.matches(value);
  }

  public boolean support(Object value) {
    return dataConfig.support(value);
  }

  public Collection<Class<?>> canCastClasses() {
    return dataConfig.canCastClasses();
  }

  public <T> boolean canCast(Class<T> clazz) {
    return dataConfig.canCast(clazz);
  }

  public <T> T cast(Object value, Class<T> clazz) {
    return dataConfig.cast(value, clazz);
  }
  // delegate methods end

  public MenuType getMenuType() {
    return menuType;
  }

  public StandardCellMatcher getMenuMatcher() {
    return menuMatcher;
  }

  public int getDistance() {
    return distance;
  }

  public String getFieldName() {
    return fieldName;
  }

  public MenuConfig getParentMenuConfig() {
    return parentMenuConfig;
  }

  public Direction getDirection() {
    return direction;
  }

  public MenuNecessity getMenuNecessity() {
    return menuNecessity;
  }

  public DataConfig getDataConfig() {
    return dataConfig;
  }

  public ImmutableCollection<MenuConfig> getChildrenMenuConfigs() {
    return childrenMenuConfigs;
  }

  public TableConfig getTableConfig() {
    return tableConfig;
  }

}
