package com.biddingo.framework.formula.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.cxf.common.util.StringUtils;

import com.biddingo.framework.formula.constants.FxConstants;
import com.biddingo.framework.formula.pojos.FxCellData;
import com.biddingo.framework.util.tree.TreeNode;
import com.biddingo.procurement.beans.BidDocItemBean;
import com.biddingo.procurement.biddoc.addendum.domain.BidDocAddendumItem;

public class FxUtils {

	private static Integer MAX_COUNT = 100;
	
    public static boolean cm_isOnlyRowNumber(final String value) {
        return value.matches(FxConstants.REGEX_NUMBER);
    }    
    
    public static boolean cm_isNumeric(final String value) {
    	if (StringUtils.isEmpty(value)) return false;
        return value.matches(FxConstants.REGEX_NUMERIC);
    }    

    public static String cm_columnNumberToChar(Integer number, final Integer startFrom) {
        
        if (startFrom == 1) {
            //--------------------------------------------
            // Starting from 1
            //--------------------------------------------
            StringBuilder sb = new StringBuilder();
            while (number-- > 0) {
                sb.append((char)('A' + (number % 26)));
                number /= 26;
            }
            return sb.reverse().toString();
        } else {
            //--------------------------------------------
            // Starting from 0
            //--------------------------------------------
            StringBuilder sb = new StringBuilder();
            while (number >= 0) {
                sb.append((char)('A' + (number % 26)));
                number /= 26;
                number--;
            }
            return sb.reverse().toString();
        }
    }    

    public static int cm_columnCharToNumber(final String colChar, final Integer startFrom) {
    	if (colChar == null) return 0;
    	
        if (startFrom == 1) {
            //--------------------------------------------
            // Starting from 1
            //--------------------------------------------
            int number = 0;
            for (int i = 0; i < colChar.length(); i++) {
                number = number * 26 + (colChar.charAt(i) - ('A' - 1));
            }
            return number;   
        } else {
            //--------------------------------------------
            // Starting from 0
            //--------------------------------------------
            int number = 0;
            for (int i = 0; i < colChar.length(); i++) {
                number = number * 26 + (colChar.charAt(i) - ('A' - 1));
            }
            return number - 1;            
        }
    }
    
    static public Object cm_convertStringToPrimitive(final String str) {
        
        try { return Byte.valueOf(str); } catch (Exception e) {};
        try { return Short.valueOf(str); } catch (Exception e) {};
        try { return Integer.valueOf(str); } catch (Exception e) {};
        try { return new BigInteger(str); } catch (Exception e) {};
        try { if (str.matches(".{1,8}")) { return Float.valueOf(str); } } catch (Exception e) {};
        try { if (str.matches(".{1,17}")) { return Double.valueOf(str); } } catch (Exception e) {};
        try { return new BigDecimal(str); } catch (Exception e) {};
          
        return null;
    }
    
    public static Double cm_getDecimalFormat(final String num) {
        
        Double f1 = Double.parseDouble(num);
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.DOWN);
        //df.setRoundingMode(RoundingMode.HALF_UP);
        return Double.valueOf(df.format(f1));
    }
    
    public static boolean cm_isPatternMatching(final String regex, final String input) {
        
        Pattern ptn = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher mtch = ptn.matcher(input);
        if (mtch.find()) {
            return true;
        }
        return false;
    }
    
    public static void cm_printMap(Map<Integer, Map<Integer, FxCellData>> m, String padding) {
        Set s = m.keySet();
        Iterator ir = s.iterator();

        while (ir.hasNext()) {
            Integer key = (Integer) ir.next();
            Object value = m.get(key);
            if (value == null)
                continue;
            if (value instanceof Map) {
                System.out.println(padding + key + " = {");
                cm_printMap((Map) value, padding + "  ");
                System.out.println(padding + "}");
            } else if (value instanceof String || value instanceof Integer || value instanceof Double
                    || value instanceof Float || value instanceof Long || value instanceof Boolean) {
                System.out.println(padding + key + " = " + value.toString());
            } else {
                System.out.println(padding + key + " = UNKNOWN OBJECT: " + value.toString());
            }
        }
    }
    
    public static String cm_replaceWhiteSpace(String value) {
    	value = value./*replaceAll(" ", "").*/replaceAll("\r", "").replaceAll("\n", "").replaceAll("\t", "");
    	return value;
    }
    
    // Added by Daniel Kim on Mar 22 - replace row identifier to new one.(+ or - index)
    // -> Modified by james on Mar 22, 2017 : Included a formula function such as =sum(...), =avg(...) etc as well as =A1:B3
    public static String rePlaceRowFormulaIdentifier(final String formula, final int addRowInt) {
    	
    	// Removed by jame on May 26, 2017 : Error Case -> F4*0.13
    	/*String newFormula = FxUtils.cm_replaceWhiteSpace(formula);
		if(newFormula == null || "".equals(newFormula.trim())) {
			return null;
		}	
		
    	Pattern pattern = Pattern.compile("([0-9])+");
    	Matcher matcher = pattern.matcher(newFormula);
    	while (matcher.find()) {     		
    		String rowIdentifier = matcher.group();
    		newFormula = newFormula.replaceFirst(Pattern.quote(rowIdentifier),
					String.valueOf(Integer.parseInt(rowIdentifier) + addRowInt));
    	}    	
    	return newFormula;*/
    	
    	return validateIFFormulaFormat4Excel(cm_shiftColumnRow(formula, 0, addRowInt).replaceAll("AVG", "AVERAGE"), true);    	
	}
    
    public static String cm_shiftColumnRow(final String formula, final Integer shiftCol, final Integer shiftRow) {
        if (StringUtils.isEmpty(formula)) {
        	return null;
        }
        
		String newFormula = FxUtils.cm_replaceWhiteSpace(formula);
		Pattern p = Pattern.compile(FxConstants.REGEX_RANGE, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(newFormula);
		int tryCount = 0;
		while (m.find() && tryCount++ <= MAX_COUNT) {
			String token = m.group();
			if (!StringUtils.isEmpty(token)) {
				String[] ranges = token.split(":");
				for (String range : ranges) {
					String rangeStr = range.trim();
					String colChar = "";
					String rowNum = "";

					for (int i = 0; i < rangeStr.length(); i++) {
						if (FxUtils.cm_isOnlyRowNumber(String.valueOf(rangeStr.charAt(i)))) {
							rowNum += rangeStr.charAt(i);
						} else {
							colChar += rangeStr.charAt(i);
						}
					}

					if (StringUtils.isEmpty(colChar) || StringUtils.isEmpty(rowNum)) {
						return null;
					}
					
					Integer colNum = cm_columnCharToNumber(colChar, 1);
					String newCol = cm_columnNumberToChar(colNum + shiftCol, 1);
					Integer newRow = Integer.valueOf(rowNum) + shiftRow;
					
					StringBuilder newToken = new StringBuilder();
					newToken.append("#").append(newCol).append("#").append(newRow).append("#");
					newFormula = newFormula.replaceFirst(Pattern.quote(range), newToken.toString());
				}
			}			
		}
		
		newFormula = newFormula.replaceAll("#", "");
        return newFormula;
    }
    
    public static String cm_adjustIdentifier(final String formula, final Integer compareNum, final Integer incresingCount, final boolean isRow) {
        if (StringUtils.isEmpty(formula)) {
        	return null;
        }
        	
		String newFormula = FxUtils.cm_replaceWhiteSpace(formula);
		Pattern p = Pattern.compile(FxConstants.REGEX_RANGE, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(newFormula);
		int tryCount = 0;
		while (m.find() && tryCount++ <= MAX_COUNT) {
			String token = m.group();
			if (!StringUtils.isEmpty(token)) {
				String[] ranges = token.split(":");
				for (String range : ranges) {
					String rangeStr = range.trim();
					String colChar = "";
					String rowNum = "";

					for (int i = 0; i < rangeStr.length(); i++) {
						if (FxUtils.cm_isOnlyRowNumber(String.valueOf(rangeStr.charAt(i)))) {
							rowNum += rangeStr.charAt(i);
						} else {
							colChar += rangeStr.charAt(i);
						}
					}

					if (StringUtils.isEmpty(colChar) || StringUtils.isEmpty(rowNum)) {
						return null;
					}
					
					String newCol = colChar;
					Integer newRow = Integer.valueOf(rowNum);					
					Integer colNum = cm_columnCharToNumber(colChar, 1);
					if (isRow) {
						if (Integer.valueOf(rowNum) > compareNum) {
							newRow = Integer.valueOf(rowNum) + incresingCount;
						} 
						newCol = cm_columnNumberToChar(colNum, 1);
					} else {
						newRow = Integer.valueOf(rowNum);
						if (colNum > compareNum) {
							newCol = cm_columnNumberToChar(colNum + incresingCount, 1);
						}
					}
					
					StringBuilder newToken = new StringBuilder();
					newToken.append("#").append(newCol).append("#").append(newRow).append("#");
					newFormula = newFormula.replaceFirst(Pattern.quote(range), newToken.toString());
				}
			}			
		}
		newFormula = newFormula.replaceAll("#", "");
        return newFormula;
    }    
    
    public static void cm_adjustRowIdentifier(final String module, List<TreeNode<BidDocItemBean>> itemTree) {
    	int rowIdx = 1;
		for (TreeNode<BidDocItemBean> itemNode : itemTree) {
			if (itemNode.getObject() == null) { 
				continue; 
			}
			
			BidDocAddendumItem formitem = itemNode.getObject().getBidDocAddendumItem();
			if(formitem == null) {
				continue; 
			}
			
			if (StringUtils.isEmpty(formitem.getRowIdentifier()) 
					|| !formitem.getRowIdentifier().equals(String.valueOf(rowIdx))) {
				formitem.setRowIdentifier(String.valueOf(rowIdx));
				itemNode.getObject().setRowIdentifier(String.valueOf(rowIdx));
			}
			rowIdx++;
		}
    }
    
    public static String batchFormula(String formula, String rowNum) {
    	String excludedKeyword = "\\b(?!MIN|MAX|COUNT|SUM|AVG|AVERAGE|IF|AND|OR|";
    	Pattern pattern = Pattern.compile(FxConstants.REGEX_DOUBLE_QUOATED_STRING, Pattern.CASE_INSENSITIVE);
    	Matcher matcher = pattern.matcher(formula);
    	int tryCount = 0;
    	while (matcher.find() && tryCount++ <= MAX_COUNT) {
    		String token = matcher.group();
    		token = token.replaceAll("\"", "");
    		
    		String[] words = token.split(" ");
    		if (words.length > 1) {
    			for (String word : words) {
    				excludedKeyword += word;
    	    		excludedKeyword += "|";
    			}
    		} else {
	    		excludedKeyword += token;
	    		excludedKeyword += "|";
    		}
    	}
    	
    	int pos = excludedKeyword.lastIndexOf("|");
    	if (!StringUtils.isEmpty(excludedKeyword) && pos >= 0) {
	    	excludedKeyword = excludedKeyword.substring(0, pos);
	    	excludedKeyword = excludedKeyword + ")[A-Z]+";
	    	
	    	pattern = Pattern.compile(excludedKeyword, Pattern.CASE_INSENSITIVE);
	    	matcher = pattern.matcher(formula);
	    	int len = 0;
	    	tryCount = 0;
	    	while (matcher.find() && tryCount++ <= MAX_COUNT) {
	    		StringBuilder newToken = new StringBuilder();
				newToken.append(matcher.group()).append(rowNum);
				formula = FxUtils.replaceCharAt(formula, matcher.start() + len, newToken.toString());
				len += (newToken.length() - 1);
	    	}
    	}
    	
    	return formula;    	
    }
    
    public static String replaceCharAt(String orgStr, int pos, String subStr) {
    	return orgStr.substring(0, pos) + subStr + orgStr.substring(pos + 1);
    }
    
    public static String replaceToken(String formual, int pos, String orgToken, String newToken) {
    	return formual.substring(0, pos) + newToken + formual.substring(pos + orgToken.length());
    }
    
    public static Double round2(Double val) {
        return new BigDecimal(val.toString()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
    
	public static boolean fxIsIFFormula(final String formula) {
		if (!StringUtils.isEmpty(formula)) {
			if (formula.toUpperCase().indexOf("=IF") >= 0 
					|| formula.toUpperCase().indexOf("IF(") >= 0
					|| formula.toUpperCase().indexOf("AND(") >= 0
					|| formula.toUpperCase().indexOf("OR(") >= 0) {
				return true;
			}
		}
		
		return false;
	}
	
	public static int getDigitNumberPositionFromString(final String value) {
		Pattern pattern = Pattern.compile("[0-9]+"); 
        Matcher matcher = pattern.matcher(value);
        int tryCount = 0;
        while (matcher.find() && tryCount++ <= MAX_COUNT) { 
        	return matcher.start();
        }
        
        return 0;
	}
	
	public static String validateIFFormulaFormat4Excel(final String formula, boolean isExport) {
		if (fxIsIFFormula(formula)) {
			String tmp = formula;
			Pattern p = Pattern.compile(FxConstants.REGEX_IF_CONDITION_GROUP, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(tmp);
			int tryCount = 0;
			while (m.find() && tryCount++ <= MAX_COUNT) {
				String token = m.group();				
				String resultVal = token;
				if (isExport) {
					resultVal = resultVal.replaceAll("==", "=");
					resultVal = resultVal.replaceAll("!=", "<>");
					resultVal = resultVal.replaceAll(" lt ", "<");
					resultVal = resultVal.replaceAll(" gt ", ">");
					resultVal = resultVal.replaceAll(" le ", "<=");
					resultVal = resultVal.replaceAll(" ge ", ">=");
				} else {
					resultVal = resultVal.replaceAll("=", "==");
					resultVal = resultVal.replaceAll("====", "==");
					resultVal = resultVal.replaceAll(">==", ">=");
					resultVal = resultVal.replaceAll("<==", "<=");
					resultVal = resultVal.replaceAll("<>", "!=");
				}
				tmp = tmp.replaceFirst(Pattern.quote(token), resultVal.toString());
			}
			return tmp;
		}
		return formula;
	}
	
    public static String convertToBatchFormula(String formula) {
        if (StringUtils.isEmpty(formula)) {
        	return null;
        }
        	
		String batchFormula = FxUtils.cm_replaceWhiteSpace(formula);
		Pattern p = Pattern.compile(FxConstants.REGEX_RANGE, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(batchFormula);
		int tryCount = 0;
		while (m.find() && tryCount++ <= MAX_COUNT) {
			String token = m.group();
			if (!StringUtils.isEmpty(token)) {
				String[] ranges = token.split(":");
				for (String range : ranges) {
					String rangeStr = range.trim();
					String colChar = "";
					String rowNum = "";

					for (int i = 0; i < rangeStr.length(); i++) {
						if (FxUtils.cm_isOnlyRowNumber(String.valueOf(rangeStr.charAt(i)))) {
							rowNum += rangeStr.charAt(i);
						} else {
							colChar += rangeStr.charAt(i);
						}
					}

					if (StringUtils.isEmpty(colChar) || StringUtils.isEmpty(rowNum)) {
						return null;
					}
					
					batchFormula = batchFormula.replaceFirst(Pattern.quote(range), colChar);
				}
			}			
		}
		
        return batchFormula;
    }
}
