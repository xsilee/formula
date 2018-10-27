package com.biddingo.framework.formula.pojos;

public class FxCellData {

    private String value;
    private String formula;
    
    //Added by Daniel Kim on Mar 15, 2017
    private String oldValue;
    
    //Added by Daniel Kim on Apr 3, 2017 - if it's calculated or not.
    private boolean isDeleted = false;
    
    // By James
    private boolean isAllowEmptyValue = true;
    private boolean isPercentage = false;    
    
    private String errMessage;
    
    public FxCellData(String value, String formula) {
        this.value = value;
        this.formula = formula;
        
        //Added by Daniel Kim on Mar 15, 2017
        this.oldValue = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    public String getFormula() {
        return formula;
    }
    
    public void setFormula(String formula) {
        this.formula = formula;
    }
    
	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}
	
	public boolean isUpdated() {
		if(this.oldValue != null) {
			if(!this.oldValue.equals(this.value)) {
				return true;
			}
		} else {
			if(this.value == null) {
				return true;
			}
		}
		
		return false;
	}
	
    @Override
    public String toString() {
        return "FxCellData [value=" + value + ", formula=" + formula + ", isDeleted=" + isDeleted + ", isAllowEmptyValue=" + isAllowEmptyValue + ", isPercentage=" + isPercentage + ", errMessage=" + errMessage + "]";
    }

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public boolean isAllowEmptyValue() {
		return isAllowEmptyValue;
	}

	public void setAllowEmptyValue(boolean isAllowEmptyValue) {
		this.isAllowEmptyValue = isAllowEmptyValue;
	}
	
    public boolean isPercentage() {
        return isPercentage;
    }

    public void setPercentage(boolean isPercentage) {
        this.isPercentage = isPercentage;
    }

	public String getErrMessage() {
		return errMessage;
	}

	public void setErrMessage(String errMessage) {
		this.errMessage = errMessage;
	}
}
