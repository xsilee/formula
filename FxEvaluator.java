package com.biddingo.framework.formula;

import java.util.Map;

import com.biddingo.framework.formula.core.FormulaTableHolder;
import com.biddingo.framework.formula.core.FxFormulaConverter;
import com.biddingo.framework.formula.pojos.FxCellData;

/*
 * Formula Engine by James : Evaluate all cells with formula using map table 
 * Only one thread can access the method at a time.
 */

public class FxEvaluator {

	public static synchronized void evaluate(Integer contentId, Map<Integer, Map<Integer, FxCellData>> mapTable) {
        
		if (contentId == null) contentId = 0;
		FormulaTableHolder.set(mapTable);		
        for (Map.Entry<Integer, Map<Integer, FxCellData>> rowEntry : mapTable.entrySet()) {
            Map<Integer, FxCellData> mapCol = rowEntry.getValue();
            for (Map.Entry<Integer, FxCellData> colEntry : mapCol.entrySet()) {
				FxFormulaConverter.fxCalculate(contentId, rowEntry.getKey(), colEntry.getKey(), 1, colEntry.getValue());
            }
        }
        FormulaTableHolder.clear();
        //FxUtils.cm_printMap(mapTable, "");
    }
}
