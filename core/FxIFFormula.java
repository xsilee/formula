package com.biddingo.framework.formula.core;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.biddingo.framework.formula.exceptions.BlankDataException;
import com.biddingo.framework.formula.pojos.FxCellData;
import com.biddingo.framework.formula.pojos.FxRangeArea;
import com.biddingo.framework.formula.utils.FxUtils;

public class FxIFFormula extends AbstractFxFormula {

	@Override
	public BigDecimal evaluate(final String contentId, Object... args) {
        
        BigDecimal result = new BigDecimal("0");        
        int conId = Integer.valueOf(contentId);
        
        for (Object obj : args) {      
            if (obj instanceof FxRangeArea) {
                FxRangeArea range = (FxRangeArea) obj;                
                if (range.getContentId() != conId) {
                    conId = range.getContentId();
                    //----------------------------------------------
                    // FIXME :
                    //----------------------------------------------
                    // range = findRangeByContentId();
                }
                for (int row = range.getStartRow(); row <= range.getEndRow(); row++) {
                    for (int col = range.getStartCol(); col <= range.getEndCol(); col++) {
                        FxCellData cellData = mapTable.get(row).get(col);
                        if (cellData != null) {
                        	if (cellData.isDeleted()) { continue; }
                        	String cellValue = FxUtils.cm_replaceWhiteSpace(cellData.getValue());
                        	if (!StringUtils.isEmpty(cellValue) 
                        			&& FxUtils.cm_isNumeric(cellValue)) {
                        		result = result.add(new BigDecimal(cellValue));
                        	} else if (StringUtils.isEmpty(cellValue)) {
                        		if (cellData.isAllowEmptyValue()) {
                        			result = result.add(new BigDecimal("0"));
                        		} else {
                        			throw new BlankDataException("Data is empty!");
                        		}
                        	} else {
                        		return null;
                        	}
                        }
                    }
                }
            } else if (obj instanceof String) {
                String val = (String) obj;
                val = FxUtils.cm_replaceWhiteSpace(val);
                if (!StringUtils.isEmpty(val) 
                		&& FxUtils.cm_isNumeric(val)) {
                    result = result.add(new BigDecimal(val));
                }
            } else if (obj instanceof Integer) {
                result = result.add(new BigDecimal((Integer) obj));
            } else if (obj instanceof Double) {
                result = result.add(new BigDecimal((Double) obj));
            } else if (obj instanceof Long) {
                result = result.add(new BigDecimal((Long) obj));
            } else if (obj instanceof Float) {
                result = result.add(new BigDecimal((Float) obj));
            } else {
            }
        }
        
        return result;
    }
	
	public String evaluateIF(final String contentId, Object... args) {
		
		if (args.length > 3) {
			throw new IllegalArgumentException("Arguments passed through spring el must be three.");
		}
		
		if (args[0] instanceof String) {
			ExpressionParser parser = new SpelExpressionParser();
			String resultVal = parser.parseExpression((String) args[0]).getValue(String.class);
			if (resultVal.toUpperCase().equals("TRUE")) {
				return String.valueOf(args[1]);
			} else {
				return String.valueOf(args[2]);
			}
		} else {
			if (args[0] != null && (boolean) args[0]) {
				return String.valueOf(args[1]);
			} else {
				return String.valueOf(args[2]);
			}
		}
	}
}
