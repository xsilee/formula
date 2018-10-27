package com.biddingo.framework.formula.core;

import org.apache.cxf.common.util.StringUtils;

import com.biddingo.framework.formula.constants.FxConstants;
import com.biddingo.framework.formula.utils.FxUtils;


public class FxFormulaFactory {

    public static AbstractFxFormula getFormulaInstance(final String formula) {
        
    	String[] fx = formula.split("=");
        if (fx.length > 1) { 
            if (FxUtils.cm_isPatternMatching(FxConstants.REGEX_FORMULA, formula)) { 
                String fxFormulaName = fx[1].substring(0, fx[1].indexOf("("));
                if (!StringUtils.isEmpty(FxConstants.fxClassNames.get(fxFormulaName))) {
                    Class<?> c;
                    try {
                        c = Class.forName(FxConstants.FX_FULL_QUALIFIED_CLASS + FxConstants.fxClassNames.get(fxFormulaName));
                        return (AbstractFxFormula) c.newInstance();
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
        return null;
    }
}
