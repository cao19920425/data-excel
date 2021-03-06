package com.github.liudaomanbu.excel.config;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import org.apache.poi.ss.usermodel.Sheet;
import com.github.liudaomanbu.excel.constant.Necessity;
import com.github.liudaomanbu.excel.matcher.Matcher;
import com.github.liudaomanbu.excel.parse.result.SheetParseResult;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public class SheetConfig extends Config {
  public static class Builder extends Config.Builder {
    private List<TableConfig.Builder> tableConfigBuilders;
    private WorkbookConfig workbookConfig;
    private Matcher<Sheet> matcher;
    private Necessity necessity;
    private ParserConfig parserConfig;

    public Builder() {
      tableConfigBuilders = Lists.newLinkedList();
    }

    public SheetConfig build() {
      return new SheetConfig(this);
    }

    @Override
    public Builder setId(Object id) {
      super.setId(id);
      return this;
    }

    public Necessity getNecessity() {
      return necessity;
    }

    public Builder setNecessity(Necessity necessity) {
      this.necessity = necessity;
      return this;
    }

    public List<TableConfig.Builder> getTableConfigBuilders() {
      return tableConfigBuilders;
    }

    public Builder setTableConfigBuilders(List<TableConfig.Builder> tableConfigBuilders) {
      this.tableConfigBuilders = tableConfigBuilders;
      return this;
    }

    public WorkbookConfig getWorkbookConfig() {
      return workbookConfig;
    }

    public Builder setWorkbookConfig(WorkbookConfig workbookConfig) {
      this.workbookConfig = workbookConfig;
      return this;
    }


    public Matcher<Sheet> getMatcher() {
      return matcher;
    }

    public Builder setMatcher(Matcher<Sheet> matcher) {
      this.matcher = matcher;
      return this;
    }

    public ParserConfig getParserConfig() {
      return parserConfig;
    }

    public Builder setParserConfig(ParserConfig parserConfig) {
      this.parserConfig = parserConfig;
      return this;
    }

  }

  private static final Predicate<Sheet> DEFAULT_MATCHER = Predicates.alwaysTrue();

  public static Builder builder() {
    return new Builder();
  }

  private final ImmutableCollection<TableConfig> tableConfigs;
  private final WorkbookConfig workbookConfig;
  private final Predicate<Sheet> matcher;
  private final Necessity necessity;
  private final ParserConfig parserConfig;

  private SheetConfig(Builder builder) {
    super(builder);
    tableConfigs = builder.tableConfigBuilders.stream()
        .peek(tableConfigBuilder -> tableConfigBuilder.setSheetConfig(this))
        .map(TableConfig.Builder::build).collect(ImmutableSet.toImmutableSet());
    workbookConfig = builder.workbookConfig;
    necessity = builder.necessity;
    matcher = Optional.ofNullable(builder.matcher).map(Matcher::reduce).orElse(DEFAULT_MATCHER);
    parserConfig = builder.parserConfig;
  }

  public SheetParseResult.Builder parse(Sheet sheet) {
    return SheetParseResult.builder().setSheet(sheet).setConfig(this).setTableBuilders(tableConfigs
        .stream().map(config -> config.parse(sheet)).collect(ImmutableList.toImmutableList()));
  }

  public ParserConfig getEffectiveParserConfig() {
    return Optional.ofNullable(parserConfig).orElse(workbookConfig.getEffectiveParserConfig());
  }

  public ImmutableCollection<TableConfig> getTableConfigs() {
    return tableConfigs;
  }

  public WorkbookConfig getWorkbookConfig() {
    return workbookConfig;
  }

  public Predicate<Sheet> getMatcher() {
    return matcher;
  }

  public ParserConfig getParserConfig() {
    return parserConfig;
  }

  public Necessity getNecessity() {
    return necessity;
  }
}
