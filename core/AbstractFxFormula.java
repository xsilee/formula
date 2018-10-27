package com.biddingo.framework.formula.core;

import java.math.BigDecimal;
import java.util.Map;

import com.biddingo.framework.formula.pojos.FxCellData;

public abstract class AbstractFxFormula {

    protected Map<Integer, Map<Integer, FxCellData>> mapTable;
    
    public void setTable(Map<Integer, Map<Integer, FxCellData>> mapTable) {
        this.mapTable = mapTable;
    }
    
    public abstract BigDecimal evaluate(final String contentId, Object... args);
}
