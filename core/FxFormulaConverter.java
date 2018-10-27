package com.biddingo.framework.formula.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.biddingo.framework.formula.constants.FxConstants;
import com.biddingo.framework.formula.exceptions.BlankDataException;
import com.biddingo.framework.formula.exceptions.InfinitDataException;
import com.biddingo.framework.formula.exceptions.ReferenceDataException;
import com.biddingo.framework.formula.pojos.FxCellData;
import com.biddingo.framework.formula.pojos.FxRangeArea;
import com.biddingo.framework.formula.utils.FxUtils;

import edu.emory.mathcs.backport.java.util.Collections;

public class FxFormulaConverter {

    private static Map<Integer, Map<Integer, FxCellData>> mapTable = new HashMap<Integer, Map<Integer, FxCellData>>();
    private static ExpressionParser parser;
    private static Integer MAX_COUNT = 100;
    
    static {
    	 parser = new SpelExpressionParser();
    }
    
    public static void initMapTable(Map<Integer, Map<Integer, FxCellData>> table) {
    	mapTable = table;
    }
    
	public static FxRangeArea fxRangeArea(final String contentId, 
										  final String row, 
										  final String col, 
										  final String rangeExpression) {

		if (!StringUtils.isEmpty(rangeExpression) 
				&& FxUtils.cm_isNumeric(rangeExpression)) {
			return null;
		}

		String expression = rangeExpression;
		String adjustedExpression = fxValidateRange(rangeExpression);
		if (expression.split(":").length > 1 
				&& !StringUtils.isEmpty(adjustedExpression) 
				&& !expression.equals(adjustedExpression)) {
			expression = adjustedExpression;
		}
		
		FxRangeArea fxRange = new FxRangeArea();
		int conId = Integer.valueOf(contentId);
		if (!StringUtils.isEmpty(expression)) {

			String[] ranges = expression.split(":");
			int cnt = 0;
			for (String range : ranges) {
				String rangeStr = range.trim();

				if (FxUtils.cm_isPatternMatching(FxConstants.REGEX_SHEET_NAME, rangeStr)) {
					String[] sheetPattern = rangeStr.split("!");
					if (sheetPattern.length > 0) {
						// ----------------------------------------------------------------------------------
						// FIXME : [important] conId =
						// getContentId(sheetPattern[0]); using sheetname
						// ----------------------------------------------------------------------------------
						rangeStr = sheetPattern[1];
					}
				}

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

				fxRange.setContentId(Integer.valueOf(contentId));
				if (cnt == 0) {
					fxRange.setStartRow(rowNum == "" ? 0 : Integer.valueOf(rowNum));
					fxRange.setStartCol(FxUtils.cm_columnCharToNumber(colChar, 1));
					fxRange.setEndRow(fxRange.getStartRow());
					fxRange.setEndCol(fxRange.getStartCol());
				} else {
					fxRange.setEndRow(rowNum == "" ? 0 : Integer.valueOf(rowNum));
					fxRange.setEndCol(FxUtils.cm_columnCharToNumber(colChar, 1));
				}
				cnt++;
			}
		}

		return fxRange;
	}
	
	public static String fxValidateRange(final String rangeExpression) {
		LinkedList<String> colList = new LinkedList<String>();
		LinkedList<Integer> rowList = new LinkedList<Integer>();
		
		if (!StringUtils.isEmpty(rangeExpression)) {
			String[] ranges = rangeExpression.split(":");
			for (String range : ranges) {
				String rangeStr = range.trim();
				String rowStr = "";
				String colStr = "";

				for (int i = 0; i < rangeStr.length(); i++) {
					if (FxUtils.cm_isOnlyRowNumber(String.valueOf(rangeStr.charAt(i)))) {
						rowStr += rangeStr.charAt(i);
					} else {
						colStr += rangeStr.charAt(i);
					}
				}
				
				if (!StringUtils.isEmpty(rowStr)) {
					rowList.add(Integer.valueOf(rowStr));
				}
				
				if (!StringUtils.isEmpty(colStr)) {
					colList.add(colStr);
				}
				
				if (rowList.size() == 0 || colList.size() == 0) {
					return null;
				}
			}
		}

		StringBuilder sb = new StringBuilder();
		sb.append(Collections.min(colList, null)).append(Collections.min(rowList, null))
		  .append(":")
		  .append(Collections.max(colList, null)).append(Collections.max(rowList, null));
		
		return sb.toString();
	}	
    
	public static String fxRangeValue(final String contentId, 
			  						  final String row, 
			  						  final String col, 
			  						  final String rangeStatement) {

        if (!StringUtils.isEmpty(rangeStatement)) {
            // Only accept a single cell not range area
            String[] ranges = rangeStatement.split(":");
            FxRangeArea rangeObj = fxRangeArea(contentId, row, col, ranges[0]);
            if (rangeObj != null) {
                return fxCellValue(rangeStatement, rangeObj);
            } else {
            	if (!StringUtils.isEmpty(rangeStatement) 
            			&& FxUtils.cm_isNumeric(rangeStatement)) {
            		return rangeStatement;
            	}
            }
        }
        
        if (!StringUtils.isEmpty(rangeStatement)) {
        	return rangeStatement;
        } else {
        	throw new BlankDataException("Data is empty! : " + rangeStatement);
        }
	}
	
	private static String fxCellValue(final String rangeStatement, final FxRangeArea range) {
        
    	for (int row = range.getStartRow(); row <= range.getEndRow(); row++) {
            for (int col = range.getStartCol(); col <= range.getEndCol(); col++) {
                FxCellData cellData = mapTable.get(row).get(col);
                if (cellData != null) {
                	if (cellData.isDeleted()) {
                		throw new ReferenceDataException("No Reference Data! : " + rangeStatement);
                	}
                	//String value = FxUtils.cm_replaceWhiteSpace(cellData.getValue());
                	String value = fxReplaceWhiteSpaces(cellData.getValue());
                	if (!StringUtils.isEmpty(value) 
                			&& FxUtils.cm_isNumeric(value)) {
                	    
                	    if (cellData.isPercentage() /*&& StringUtils.isEmpty(cellData.getFormula())*/) {
                            BigDecimal pValue = new BigDecimal(value);
                            BigDecimal dValue = new BigDecimal("100");
                            //BigDecimal rValue = pValue.divide(dValue, 3, RoundingMode.CEILING);
                            BigDecimal rValue = pValue.divide(dValue, 2, RoundingMode.HALF_UP);
                            value = rValue.toString();
                        }
                		return value;
                	} else if (!StringUtils.isEmpty(value)) {
                		return value;
                	} else {
                		if (cellData.isAllowEmptyValue()) {
                			return "0";
                		} else {
                			throw new BlankDataException("Data is empty! : " + rangeStatement);
                		}
                	}
                }
            }
        }
        
    	return null;
    }
    
    private static String fxPatternReplacement(final Integer contentId, 
											   final Integer row, 
											   final Integer col, 
											   final String regex, 
											   final String methodName, 
											   final String formula,
											   String input,
											   final Integer depth) {

    	//String orgInput = "=" + input;
    	String orgInput = formula;
		int conId = contentId;
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(input);
		int tryCount = 0;
		while (m.find() && tryCount <= MAX_COUNT) {
			String token = m.group(1);

			if (FxUtils.cm_isPatternMatching(FxConstants.REGEX_SHEET_NAME, token)) {
				String[] sheetPattern = token.split("!");
				if (sheetPattern.length > 1) {
					// ----------------------------------------------------------------------------------
					// FIXME : [important] conId = getContentId(sheetPattern[0])
					// using sheetname
					// ----------------------------------------------------------------------------------
					token = sheetPattern[1];
				}
			}

			String fullyMethodName = fxGenerateMethodStatement(token.split(":").length > 1 ? FxConstants.FX_METHOD_RANGE_OBJECT : methodName,
					  											String.valueOf(conId),
					  											String.valueOf(row),
					  											String.valueOf(col),
					  											token);
			
			//FIXME : After enhancement about regEx for IF function then I do not use below logic. Check =OR(C1==1,D1==2), =OR("C1"==1,"D1"==2)
			if (fullyMethodName != null 
					&& (formula.toUpperCase().indexOf("AND(") >= 0
							|| formula.toUpperCase().indexOf("OR(") >= 0)
					&& token.indexOf(":") < 0) {				
				fullyMethodName = fxGenerateMethodStatement(FxConstants.FX_METHOD_RANGE_VALUE,
															String.valueOf(conId),
															String.valueOf(row),
															String.valueOf(col),
															token);
				
				fullyMethodName = parser.parseExpression((String) fullyMethodName).getValue(String.class);	
				if (fullyMethodName == null) {
					return null;
				}
			}
			
			input = input.replaceAll(token, fullyMethodName);

			FxRangeArea range = fxRangeArea(String.valueOf(contentId), String.valueOf(row), String.valueOf(col), token);
			
			if (FxUtils.fxIsIFFormula(formula)) {
				if (range.getEndRow() > mapTable.size()) {
					// Case of each item status change. ex) 5 row item changes normal item to required item. 
					if (mapTable.size() == 1) {
						return input;
					}
					return null;
				}
			} else {
				if (range.getEndRow() > mapTable.size()) {
					tryCount++;
					continue;
				}
			}
			
			for (int r = range.getStartRow(); r <= range.getEndRow(); r++) {
				for (int c = range.getStartCol(); c <= range.getEndCol(); c++) {
					
					FxCellData cellData = mapTable.get(r).get(c);					
					if (cellData != null && !StringUtils.isEmpty(cellData.getFormula()) 
							&& orgInput.equals(cellData.getFormula())) {
						throw new ReferenceDataException("No Reference Data! : " + orgInput);
					}
					
					// Protect the infinite loop to check inner formula in the range of cell with the same token. 
					if (cellData != null && !StringUtils.isEmpty(cellData.getFormula()) 
							&& cellData.getFormula().indexOf(token) < 0) {
						fxCalculate(contentId, r, c, depth + 1, cellData);
					} else if (cellData != null && !StringUtils.isEmpty(cellData.getFormula()) 
							&& cellData.getFormula().indexOf(token) >= 0){

						m = p.matcher(cellData.getFormula());
						int tryCount1 = 0;
						while (m.find() && tryCount1 <= MAX_COUNT) {
							String tmp = m.group(1);
							if (tmp.equals(token)) {
								throw new InfinitDataException("Infinit Data! : " + token);
							}
							tryCount1++;
						}
						fxCalculate(contentId, r, c, depth + 1, cellData);
					}
				}
			}
			
			tryCount++;
		}

		return input;
	}

    private static String fxGenerateMethodStatement(final String methodName, final String... params) {
        
        StringBuilder sb = new StringBuilder();        
        sb.append(methodName).append("(");        
        for (int i = 0; i < params.length; i++) {
            Object o = params[i];            
            if (i == 0) {
                sb.append("'").append(o.toString()).append("'");                
            } else {
                sb.append(",'").append(o.toString()).append("'");
            }
        }        
        sb.append(")");
        return sb.toString();
    } 
    
	public static String fxConvertToFormulaExpresstion(final Integer contentId, 
													   final Integer row, 
													   final Integer col, 
													   final String formula,
													   final String formulaToken,
													   final Integer depth) {
		String expression = "";
		
		if (FxUtils.cm_isPatternMatching(FxConstants.REGEX_FORMULA, formulaToken)) {
			expression = fxPatternReplacement(contentId, row, col,
					//FxConstants.REGEX_RANGE, FxConstants.FX_METHOD_RANGE_OBJECT, formula, fx[1]);
					FxConstants.REGEX_RANGE, FxConstants.FX_METHOD_RANGE_OBJECT, formula, formulaToken.substring(1), depth);
		} else {
			expression = fxPatternReplacement(contentId, row, col,
					FxConstants.REGEX_RANGE, FxConstants.FX_METHOD_RANGE_VALUE, formula, formulaToken.substring(1), depth);
		}

		for (Map.Entry<String, String> entry : FxConstants.fxFunctions.entrySet()) {
			String funcName = entry.getKey();
			String mappingFunction = entry.getValue();

			StringBuilder sbFuncNameRegex = new StringBuilder();
			sbFuncNameRegex.append("\\b").append(funcName).append("\\(")
							.append("|")
							.append("\\b").append(funcName.toUpperCase()).append("\\(");

			StringBuilder sbMappingFunction = new StringBuilder();
			sbMappingFunction.append(mappingFunction).append("(").append("'")
					.append(contentId).append("'").append(",");

			if (expression != null) {
				expression = expression.replaceAll(sbFuncNameRegex.toString(),
						sbMappingFunction.toString());
			}
		}

		return expression;
	}    
	
	public static void fxCalculate_old(final Integer contentId, 
								   final Integer row, 
								   final Integer col, 
								   FxCellData cellData) {
		
		BigDecimal resultVal = new BigDecimal("0");
        if (!StringUtils.isEmpty(cellData.getFormula())) {
            
        	AbstractFxFormula formulaObj = FxFormulaFactory.getFormulaInstance(cellData.getFormula());
            if (formulaObj == null) { 
            	initMapTable(FormulaTableHolder.get());
            	try {
                    resultVal = resultVal.setScale(2, BigDecimal.ROUND_HALF_UP);
                    if (!StringUtils.isEmpty(resultVal.toString()) 
                    		&& FxUtils.cm_isNumeric(resultVal.toString())) {
                    	cellData.setValue("0.00".equals(resultVal.toString()) ? "" : resultVal.toString());
                    }
            	} catch( BlankDataException e) {
            		cellData.setValue("");
            	} catch (ReferenceDataException e) {
            		cellData.setValue(FxConstants.ERROR_NO_REFERENCE);
            	} catch (InfinitDataException e) {
            		cellData.setValue(FxConstants.ERROR_INFINIT_LOOP);
            	} catch (ArithmeticException e) {
                    cellData.setValue(FxConstants.ERROR_DIVID_BY_ZERO);
                } catch (NumberFormatException e) {
                    cellData.setValue("");                        	
                } catch (Exception e) {
                    cellData.setValue(FxConstants.ERROR_NOT_NUMERIC);
                }
            } else {
            	initMapTable(FormulaTableHolder.get());
            	formulaObj.setTable(FormulaTableHolder.get());            
                StandardEvaluationContext formulaContext = new StandardEvaluationContext(formulaObj);
            	try {
                    /*resultVal = parser.parseExpression(
                    					fxConvertToFormulaExpresstion(contentId, row, col, FxUtils.cm_replaceWhiteSpace(cellData.getFormula())))
                    				.getValue(formulaContext, BigDecimal.class);*/
                    if (cellData.getFormula().toLowerCase().indexOf("count(") < 0 
                    		&& cellData.getFormula().toLowerCase().indexOf("min(") < 0
                    		&& cellData.getFormula().toLowerCase().indexOf("max(") < 0) {
                    	resultVal = resultVal.setScale(2, BigDecimal.ROUND_HALF_UP);
                    }
                    if (!StringUtils.isEmpty(resultVal.toString()) 
                    		&& FxUtils.cm_isNumeric(resultVal.toString())) {
                    	cellData.setValue("0.00".equals(resultVal.toString()) ? "" : resultVal.toString());
                    }
            	} catch (BlankDataException e) {
            		cellData.setValue("");
            	} catch (ReferenceDataException e) {
            		cellData.setValue(FxConstants.ERROR_NO_REFERENCE);
            	} catch (InfinitDataException e) {
            		cellData.setValue(FxConstants.ERROR_INFINIT_LOOP);
            	} catch (ArithmeticException e) {
                    cellData.setValue(FxConstants.ERROR_DIVID_BY_ZERO);
                } catch (NumberFormatException e) {
                    cellData.setValue("");                            
                } catch (Exception e) {
                    cellData.setValue(FxConstants.ERROR_NOT_NUMERIC);
                }
            }
            mapTable.get(row).put(col, cellData);
        }
	}
	
	public static String fxCalculateTokenFormula(final Integer contentId, 
								   				 final Integer row, 
								   				 final Integer col,
								   				 final String orgFormula,
								   				 final String formula,
								   				 final String formulaToken,
								   				 final Integer depth) {

		String resultVal = "";
		if (!StringUtils.isEmpty(formulaToken)) {
			
			initMapTable(FormulaTableHolder.get());
			AbstractFxFormula formulaObj = FxFormulaFactory.getFormulaInstance("="+formulaToken);
			if (formulaObj == null) {
        		resultVal = parser.parseExpression(
						fxConvertToFormulaExpresstion(contentId, row, col, orgFormula, fxReplaceWhiteSpaces("="+formulaToken), depth))
					.getValue(String.class);
			} else {
				formulaObj.setTable(FormulaTableHolder.get());
				StandardEvaluationContext formulaContext = new StandardEvaluationContext(formulaObj);
				
				String expression = fxConvertToFormulaExpresstion(contentId, row, col, orgFormula, fxReplaceWhiteSpaces("="+formulaToken), depth);
                resultVal = parser.parseExpression(expression)
    				.getValue(formulaContext, String.class);
			}

			if (FxUtils.fxIsIFFormula(orgFormula)) {
				if (resultVal != null && !FxUtils.cm_isNumeric(resultVal)) {
					StringBuilder sbFormulaVal = new StringBuilder();
					// Be able to recognize literal data with double quotation in SpEL. Ex) =IF(A3>100,"AA","BB")
					sbFormulaVal.append("\"")
								.append(resultVal)
								.append("\"");
					resultVal = sbFormulaVal.toString();
				} else if (resultVal == null) {
					int pos = FxUtils.getDigitNumberPositionFromString(formulaToken);
					StringBuilder sb = new StringBuilder();
					sb.append(formulaToken.substring(0, pos))
					  .append("_BDG_")
					  .append(formulaToken.substring(pos));
					resultVal = sb.toString();
				}
				
				resultVal = resultVal.replace("$", "\\$");
			}
			
			// Exception : $0.00 -> 0.00 otherwise infinite happens
			if (resultVal.charAt(0) == '$') {
				resultVal = resultVal.substring(1);
			}
			return formula.replaceFirst(Pattern.quote(formulaToken), resultVal.toString());
		}
		
		return formula;
	}
	
	public static Pattern fxGetPatternCompiler(final String formula) {
		
		if (FxUtils.fxIsIFFormula(formula)) {
			return Pattern.compile(FxConstants.REGEX_IF_FORMULA_GROUP, Pattern.CASE_INSENSITIVE);
		}
		
		return Pattern.compile(FxConstants.REGEX_FORMULA_GROUP, Pattern.CASE_INSENSITIVE);
	}
	
	public static String fxReplaceWhiteSpaces(final String formula) {
		
		if (FxUtils.fxIsIFFormula(formula)) {
	        String tmp = formula.substring(1);
	        tmp = tmp.replaceAll("=", "==");
	        tmp = tmp.replaceAll("====", "==");
	        tmp = tmp.replaceAll(">==", ">=");
	        tmp = tmp.replaceAll("<==", "<=");
	        tmp = tmp.replaceAll("<>", "!=");
			return "=" + tmp;
		}
		
		return FxUtils.cm_replaceWhiteSpace(formula);
	}
	
	public static String fxConvertFormula(final Integer contentId, 
	                                      final Integer row, 
	                                      final Integer col, 
	                                      final String orgFormula, 
	                                      String formula, 
	                                      final String regex, 
	                                      final Integer depth) {
		
		//Pattern p = fxGetPatternCompiler(formula);
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(formula);
		int tryCount = 0;
		while (m.find() && tryCount <= MAX_COUNT) {
			String token = m.group();
			formula = fxCalculateTokenFormula(contentId, 
											  row, 
											  col, 
											  orgFormula, 
											  formula, 
											  token,
											  depth);
			m = p.matcher(formula);
			tryCount++;
		}
		
		return formula;
	}
	
	public static void fxCalculate(final Integer contentId, 
								   final Integer row, 
								   final Integer col,
								   final Integer depth,
								   FxCellData cellData) {
		
		if (row < 0 || col < 0) return;
		
		if (!StringUtils.isEmpty(cellData.getFormula())
				&& cellData.getFormula().indexOf("=") >= 0) {
			try {
			    if (depth > 50) {
			        throw new InfinitDataException("Infinit Data! : " + cellData.getFormula());
			    }
			    
				String formula = fxReplaceWhiteSpaces(cellData.getFormula());
				String orgFormula = formula;
				formula = fxConvertFormula(contentId, row, col, orgFormula, formula, FxConstants.REGEX_FORMULA_GROUP, depth);
				
				if (FxUtils.fxIsIFFormula(orgFormula)) {
					formula = fxConvertFormula(contentId, row, col, orgFormula, formula, FxConstants.REGEX_IF_FORMULA_GROUP, depth);
					
					if (!StringUtils.isEmpty(formula.substring(1)) 
	                		&& FxUtils.cm_isNumeric(formula.substring(1))) {
						BigDecimal resultVal = new BigDecimal(formula.substring(1));
						
						if (cellData.isPercentage() && !StringUtils.isEmpty(cellData.getFormula())) {
                            BigDecimal dValue = new BigDecimal("100");
                            resultVal = resultVal.multiply(dValue);
                        }
						
	                	resultVal = resultVal.setScale(2, BigDecimal.ROUND_HALF_UP);
	                	cellData.setValue("0.00".equals(resultVal.toString()) ? "0.00" : resultVal.toString());
	                } else {
	                	String finalResult = formula.substring(1);
	                	finalResult = finalResult.replaceAll("\"", "");
	                	finalResult = finalResult.replaceAll("_BDG_", "");
	                	cellData.setValue("0.00".equals(formula.substring(1)) ? "0.00" : finalResult);
	                }
				} else {
					BigDecimal resultVal = new BigDecimal("0");
	        		resultVal = parser.parseExpression(formula.substring(1)).getValue(BigDecimal.class);        		
	                if (cellData.getFormula().toLowerCase().indexOf("count(") < 0 
	                		&& cellData.getFormula().toLowerCase().indexOf("min(") < 0
	                		&& cellData.getFormula().toLowerCase().indexOf("max(") < 0) {
	                	resultVal = resultVal.setScale(2, BigDecimal.ROUND_HALF_UP);
	                } else {
	                	// If count,min,max then check last char weather it is combination formula or not like =MIN(F1:F4)/H4*0.8
	                	String lastChar = cellData.getFormula().substring(cellData.getFormula().length() - 1);
	                	if (!lastChar.equals(")")) {
	                		resultVal = resultVal.setScale(2, BigDecimal.ROUND_HALF_UP);
	                	}
	                }
	                
	                if (!StringUtils.isEmpty(resultVal.toString()) 
	                		&& FxUtils.cm_isNumeric(resultVal.toString())) {
	                    
	                    if (cellData.isPercentage() && !StringUtils.isEmpty(cellData.getFormula())) {
                            BigDecimal dValue = new BigDecimal("100");
                            resultVal = resultVal.multiply(dValue);
                        }
	                	cellData.setValue("0.00".equals(resultVal.toString()) ? "" : resultVal.toString());
	                }
				}
			} catch (BlankDataException e) {
				cellData.setValue("");
				cellData.setErrMessage(e.getMessage());
			} catch (ReferenceDataException e) {				
				cellData.setValue(FxConstants.ERROR_NO_REFERENCE);
				cellData.setErrMessage(e.getMessage());
        	} catch (InfinitDataException e) {
        		cellData.setValue(FxConstants.ERROR_INFINIT_LOOP);
        		cellData.setErrMessage(e.getMessage());
			} catch (ArithmeticException e) {
				cellData.setValue(FxConstants.ERROR_DIVID_BY_ZERO);
				cellData.setErrMessage(e.getMessage());
			} catch (NumberFormatException e) {
				cellData.setValue("");
				cellData.setErrMessage(e.getMessage());
			} catch (Exception e) {
				cellData.setValue(FxConstants.ERROR_NOT_NUMERIC);
				cellData.setErrMessage(e.getMessage());
			}
			
			if (row >= 0 && col >= 0) {
				if (mapTable.get(row) != null) {
					mapTable.get(row).put(col, cellData);
				}
			}
		}
	}	
}
