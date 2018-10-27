package com.biddingo.framework.formula.core;

import java.math.BigDecimal;

public class FxOrFormula extends AbstractFxFormula {

	public String evaluateOr(final String contentId, Object... args) {
        
        StringBuilder result = new StringBuilder();        
        result.append("( ");
        
        for (Object obj : args) {   
        	/*if (obj instanceof FxRangeArea) {
                FxRangeArea range = (FxRangeArea) obj;                
                
                if (range.getEndRow() > mapTable.size()) {
                	throw new BlankDataException("Data range is over!");
				}
                
                if (range.getEndRow() > mapTable.size()) {
                	result.append("0").append(" or ");
					continue;
				}
                
                for (int row = range.getStartRow(); row <= range.getEndRow(); row++) {
                    for (int col = range.getStartCol(); col <= range.getEndCol(); col++) {
                        FxCellData cellData = mapTable.get(row).get(col);
                        if (cellData != null) {
                        	if (cellData.isDeleted()) { continue; }
                        	String cellValue = FxUtils.cm_replaceWhiteSpace(cellData.getValue());
                        	if (!StringUtils.isEmpty(cellValue) 
                        			&& FxUtils.cm_isNumeric(cellValue)) {
                        		result.append(cellValue).append(" or ");
                        	} else if (StringUtils.isEmpty(cellValue)) {
                        		if (cellData.isAllowEmptyValue()) {
                        			result.append("0").append(" or ");
                        		} else {
                        			throw new BlankDataException("Data is empty!");
                        		}
                        	} else {
                        		return null;
                        	}
                        }
                    }
                }   
        	} else*/ if (obj instanceof String) {
                result.append(obj).append(" or ");
            } else if (obj instanceof Integer) {
            	result.append(obj).append(" or ");
            } else if (obj instanceof Double) {
            	result.append(obj).append(" or ");
            } else if (obj instanceof Long) {
            	result.append(obj).append(" or ");
            } else if (obj instanceof Float) {
            	result.append(obj).append(" or ");
            } else {
            	result.append(obj).append(" or ");
            }
        }
        
        String tmp = result.toString();
        int lastPos = tmp.lastIndexOf("or ");
        tmp = tmp.substring(0, lastPos) + " )";
        
        return tmp;
    }

	@Override
	public BigDecimal evaluate(String contentId, Object... args) {
		return null;
	}
}
