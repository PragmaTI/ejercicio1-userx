package com.pragma.dao;

import java.util.LinkedHashMap;
import java.util.Map;

public class PragmaSPDef {
private String name;
private boolean returnsCursor;

private boolean returnsId;

private String cursorName = "c_result";

private String generatedIdName = "r_id";

private Map inParams = new LinkedHashMap();

private Map outParams = new LinkedHashMap();

public String getCursorName() {
	return cursorName;
}
public void setCursorName(String cursorName) {
	this.cursorName = cursorName;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public boolean isReturnsCursor() {
	return returnsCursor;
}
public void setReturnsCursor(boolean returnsCursor) {
	this.returnsCursor = returnsCursor;
}
public boolean isReturnsId() {
	return returnsId;
}
public void setReturnsId(boolean returnsId) {
	this.returnsId = returnsId;
}
public String getGeneratedIdName() {
	return generatedIdName;
}
public void setGeneratedIdName(String generatedIdName) {
	this.generatedIdName = generatedIdName;
}
public boolean isInParam(String key) {
	return inParams.containsKey(key);
	
}

public void addInParam(String key, int type) {
	inParams.put(key, new Integer(type));	
}

public boolean isOutParam(String key) {
	return outParams.containsKey(key);
	
}

public void addOutParam(String key, int type) {
	outParams.put(key, new Integer(type));	

}
public Map getInParams() {
	return inParams;
}
private void setInParams(Map inParams) {
	this.inParams = inParams;
}
public Map getOutParams() {
	return outParams;
}
private void setOutParams(Map outParams) {
	this.outParams = outParams;
}


}
