package com.caotc.excel4j.matcher.usermodel;

import org.apache.poi.hssf.util.CellReference;
import com.caotc.excel4j.matcher.BaseMatcher;
import com.caotc.excel4j.matcher.data.ComparableValueMatcher;
import com.caotc.excel4j.matcher.data.DataMatcher;
import com.caotc.excel4j.matcher.data.StringMatcher;
import com.caotc.excel4j.parse.result.StandardCell;

public class StandardCellMatcher extends BaseMatcher<StandardCell>{
  private DataMatcher valueMatcher;
  private ComparableValueMatcher<Integer> rowNumberMatcher;
  private ComparableValueMatcher<Integer> columnNumberMatcher;
  private StringMatcher columnStringMatcher;

  public boolean test(StandardCell cell) {
    return valueMatcher.test(cell.getValue())
        && rowNumberMatcher.test(cell.getValueCell().getRowIndex())
        && columnNumberMatcher.test(cell.getValueCell().getColumnIndex()) && columnStringMatcher
            .test(CellReference.convertNumToColString(cell.getValueCell().getColumnIndex()));
  }
}
