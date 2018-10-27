package com.biddingo.framework.formula.core;

import java.util.Map;

import com.biddingo.framework.formula.pojos.FxCellData;

public class FormulaTableHolder {

    private static final ThreadLocal<Map<Integer, Map<Integer, FxCellData>>> threadLocal = new ThreadLocal<Map<Integer, Map<Integer, FxCellData>>>();

    public static void set(Map<Integer, Map<Integer, FxCellData>> mapTable) {
        threadLocal.set(mapTable);
    }

    public static Map<Integer, Map<Integer, FxCellData>> get() {
        return threadLocal.get();
    }

    public static void clear() {
        threadLocal.remove();
    }
}
