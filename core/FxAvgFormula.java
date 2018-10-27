package com.biddingo.framework.formula.core;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang.StringUtils;

import com.biddingo.framework.formula.exceptions.BlankDataException;
import com.biddingo.framework.formula.pojos.FxCellData;
import com.biddingo.framework.formula.pojos.FxRangeArea;
import com.biddingo.framework.formula.utils.FxUtils;

public class FxAvgFormula extends AbstractFxFormula {
    
    @Override
    public BigDecimal evaluate(final String contentId, Object... args) {
        
        BigDecimal result = new BigDecimal("0");        
        int conId = Integer.valueOf(contentId);
        int count = 0;
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
                        	    
                        	    if (cellData.isPercentage() /*&& StringUtils.isEmpty(cellData.getFormula())*/) {
                                    BigDecimal pValue = new BigDecimal(cellValue);
                                    BigDecimal dValue = new BigDecimal("100");
                                    //BigDecimal rValue = pValue.divide(dValue, 3, RoundingMode.CEILING);
                                    BigDecimal rValue = pValue.divide(dValue, 2, RoundingMode.HALF_UP);
                                    result = result.add(rValue);
                                } else {
                                    result = result.add(new BigDecimal(cellValue));
                                }
                        		count++;
                        	} else if (StringUtils.isEmpty(cellValue)) {
                        		if (cellData.isAllowEmptyValue()) {
                        			result = result.add(new BigDecimal("0"));
                        			//count++;
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
                    count++;
                }
            } else if (obj instanceof Integer) {
                result = result.add(new BigDecimal((Integer) obj));
                count++;
            } else if (obj instanceof Double) {
                result = result.add(new BigDecimal((Double) obj));
                count++;
            } else if (obj instanceof Long) {
                result = result.add(new BigDecimal((Long) obj));
                count++;
            } else if (obj instanceof Float) {
                result = result.add(new BigDecimal((Float) obj));
                count++;
            } else {
            }
        }
        
        result = result.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP);
        return result;
    }
}
