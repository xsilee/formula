package com.biddingo.framework.formula.constants;

import java.util.HashMap;
import java.util.Map;

import com.biddingo.framework.formula.core.FxFormulaConverter;

public class FxConstants {

    public static final String REGEX_RANGE              = "(?=[^\"]*(?:\"[^\"]*\"[^\"]*)*$)((S[0-9]![a-zA-Z]+[0-9]+:[a-zA-Z]+[0-9]+)|(S[0-9]![a-zA-Z]+[0-9]+)|([a-zA-Z]+[0-9]+:[a-zA-Z]+[0-9]+)|([a-zA-Z]+[0-9]+))(\\#\\w*)?";
    public static final String REGEX_SHEET_NAME         = "^S[0-9]+!";
    public static final String REGEX_FORMULA            = "^=[a-zA-Z]+[(]";
    
    public static final String REGEX_FORMULA_GROUP      = "([a-zA-Z]+\\d+)|(AND\\(([^)(])*(,(\\s*)(\"(([^\"]|\"\"))*)\")*\\)|OR\\(([^)(]*)(,(\\s*)(\"(([^\"]|\"\"))*)\")*\\))|([a-zA-Z]+\\(((\\w+\\d+)|(\\d*\\.?\\d*))((:\\w+\\d+)|(,\\w+\\d+)*)((,\\w+\\d+)((:\\w+\\d+)|(,\\w+\\d+)*))*(,\\d*\\.?\\d*)*(,\\w+)*\\))";
    
    public static final String REGEX_IF_FORMULA_GROUP   = "([a-zA-Z]+\\d+)|(\\s*(\\d*\\.?\\d+)(\\s*(\\+|-|\\*|\\/)\\s*)(\\d*\\.?\\d+))|([a-zA-Z]+\\((\\s*)((\\w+\\d+)|(\\d*\\.?\\d*)|((\\d*\\.?\\d*)(\\s*(==|<=|>=|<|>|=|!=|lt|gt|eq|ne|le|ge)\\s*)(\\d*\\.?\\d*))|(((['\"]\\w+['\"])|(\\d*\\.?\\d*))(\\s*(==|<=|>=|<|>|=|!=|lt|gt|eq|ne|le|ge)\\s*)(([\"]\\w+[\"])|(\\d*\\.?\\d*))((\\s*)(and|or|&&|\\|\\|)(\\s*)\\(?((['\"]\\w+['\"])|(\\d*\\.?\\d*))(\\s*(==|<=|>=|<|>|=|!=|lt|gt|eq|ne|le|ge)\\s*)(((['\"]\\w+['\"])|(\\d*\\.?\\d*)\\)?))(\\s*))*)|(([^,]*)))((:\\w+\\d+)|(,(\\s*)\\w+\\d+)*)((,(\\s*)\\w+\\d+)((:\\w+\\d+)|(,(\\s*)\\w+\\d+)*))*((,(\\s*)(\\d+)(\\s*))|(,(\\s*)(\\d+\\.?\\d*)(\\s*))|(,(\\s*)(\"(([^\"]|\"\"))*)\"))*\\))";
    public static final String REGEX_IF_CONDITION_GROUP = "([a-zA-Z]+\\(([^,]*))";
    
    public static final String REGEX_NUMERIC            = "^(([0-9]*)|(([0-9]*)\\.([0-9]*)))$";
    public static final String REGEX_NUMBER             = "^[0-9]+$";
    public static final String REGEX_NO_VALUE           = "[0]+\\.[0]+";
    
	public static final String REGEX_FORMULAR_IDENTIFIER = "\\b(?!MIN|MAX|COUNT|SUM|AVG|AVERAGE|IF|AND|OR)[A-Z]+";
	public static final String REGEX_DOUBLE_QUOATED_STRING = "(?!^)\".*?\"";

    /* 
    Do not change when this code is copied into other system like e-response or biddingo system.
    The class path will be created dynamically
    */
    public static String FX_METHOD_RANGE_OBJECT   		= "T(com.biddingo.framework.formula.core.FxFormulaConverter).fxRangeArea";
    public static String FX_METHOD_RANGE_VALUE    		= "T(com.biddingo.framework.formula.core.FxFormulaConverter).fxRangeValue";
    public static String FX_FULL_QUALIFIED_CLASS  		= "com.biddingo.framework.formula.core.";
    
    // [**Notice**] When additional formula functions need, then please append below
    public static final String FX_SUM                   = "sum";
    public static final String FX_AVERAGE               = "average";
    public static final String FX_AVG		            = "avg";
    public static final String FX_COUNT                 = "count";
    public static final String FX_MAX                   = "max";
    public static final String FX_MIN                   = "min";
    public static final String FX_IF                    = "if";
    public static final String FX_AND                   = "and";
    public static final String FX_OR                    = "or";
    
    public static final String ERROR_DIVID_BY_ZERO      = "#Div/0!";
    public static final String ERROR_NOT_NUMERIC        = "#VALUE!";
    public static final String ERROR_NO_REFERENCE       = "#REF!";
    public static final String ERROR_INFINIT_LOOP       = "#INFINIT!";
    
    public static final Map<String, String> fxFunctions = new HashMap<String, String>();
    public static final Map<String, String> fxClassNames = new HashMap<String, String>();
    
    static {
    	// [**Notice**]  When additional formula functions need, then please append below
        fxFunctions.put(FX_SUM, "evaluate");
        fxFunctions.put(FX_AVERAGE, "evaluate");
        fxFunctions.put(FX_AVG, "evaluate");
        fxFunctions.put(FX_COUNT, "evaluate");
        fxFunctions.put(FX_MAX, "evaluate");
        fxFunctions.put(FX_MIN, "evaluate");
        fxFunctions.put(FX_IF, "evaluateIF");
        fxFunctions.put(FX_AND, "evaluateAnd");
        fxFunctions.put(FX_OR, "evaluateOr");
        
        // [**Notice**] When additional formula functions need, then please append below
        fxClassNames.put(FX_SUM, "FxSumFormula");
        fxClassNames.put(FX_AVERAGE, "FxAvgFormula");
        fxClassNames.put(FX_AVG, "FxAvgFormula");
        fxClassNames.put(FX_COUNT, "FxCountFormula");
        fxClassNames.put(FX_MAX, "FxMaxFormula");
        fxClassNames.put(FX_MIN, "FxMinFormula");        
        fxClassNames.put(FX_IF, "FxIFFormula");
        fxClassNames.put(FX_AND, "FxAndFormula");
        fxClassNames.put(FX_OR, "FxOrFormula");
        
        // [**Notice**] When additional formula functions need, then please append below
        fxClassNames.put(FX_SUM.toUpperCase(), "FxSumFormula");
        fxClassNames.put(FX_AVERAGE.toUpperCase(), "FxAvgFormula");
        fxClassNames.put(FX_AVG.toUpperCase(), "FxAvgFormula");
        fxClassNames.put(FX_COUNT.toUpperCase(), "FxCountFormula");
        fxClassNames.put(FX_MAX.toUpperCase(), "FxMaxFormula");
        fxClassNames.put(FX_MIN.toUpperCase(), "FxMinFormula");    
        fxClassNames.put(FX_IF.toUpperCase(), "FxIFFormula");
        fxClassNames.put(FX_AND.toUpperCase(), "FxAndFormula");
        fxClassNames.put(FX_OR.toUpperCase(), "FxOrFormula");
        
        String className = FxFormulaConverter.class.getName();
        FX_FULL_QUALIFIED_CLASS = className.replace("FxFormulaConverter", "");
        
        StringBuilder sb = new StringBuilder();
        sb.append("T(").append(className).append(").fxRangeArea");
        FX_METHOD_RANGE_OBJECT = sb.toString();
        
        sb.setLength(0);
        sb.append("T(").append(className).append(").fxRangeValue");
        FX_METHOD_RANGE_VALUE = sb.toString();
    }    
}
