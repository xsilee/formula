package com.biddingo.framework.formula.core;

import java.math.BigDecimal;
import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;

import com.biddingo.framework.formula.exceptions.BlankDataException;
import com.biddingo.framework.formula.pojos.FxCellData;
import com.biddingo.framework.formula.pojos.FxRangeArea;
import com.biddingo.framework.formula.utils.FxUtils;

public class FxCountFormula extends AbstractFxFormula {
    
    @Override
    public BigDecimal evaluate(final String contentId, Object... args) {
        
    	LinkedList<BigDecimal> list = new LinkedList<BigDecimal>();
    	
        for (Object obj : args) {      
            if (obj instanceof FxRangeArea) {
                FxRangeArea range = (FxRangeArea) obj;    
                
                if (range.getEndRow() > mapTable.size()) {
                	throw new BlankDataException("Data range is over!");
				}
                
                for (int row = range.getStartRow(); row <= range.getEndRow(); row++) {
                    for (int col = range.getStartCol(); col <= range.getEndCol(); col++) {
                        FxCellData cellData = mapTable.get(row).get(col);
                        if (cellData != null) {
                        	if (cellData.isDeleted()) { continue; }
                        	String cellValue = FxUtils.cm_replaceWhiteSpace(cellData.getValue());
                        	if (!StringUtils.isEmpty(cellValue) 
                        			&& FxUtils.cm_isNumeric(cellValue)) {
                        		list.add(new BigDecimal(cellValue));  
                        	} else if (StringUtils.isEmpty(cellValue)) {
                        		if (cellData.isAllowEmptyValue()) {
                        			list.add(new BigDecimal("0"));
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
                    list.add(new BigDecimal(val));
                }
            } else if (obj instanceof Integer) {
                list.add(new BigDecimal((Integer) obj));
            } else if (obj instanceof Double) {
                list.add(new BigDecimal((Double) obj));
            } else if (obj instanceof Long) {
                list.add(new BigDecimal((Long) obj));
            } else if (obj instanceof Float) {
                list.add(new BigDecimal((Float) obj));
            } else {
            }
        }
        
        return new BigDecimal(list.size());
    }
}
