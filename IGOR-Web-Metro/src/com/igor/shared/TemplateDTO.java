package com.igor.shared;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TemplateDTO implements IsSerializable {

	public TemplateDTO(){}
	
	public String stem ="";
	public List<VariableDTO> variables = new ArrayList<VariableDTO>();
	public List<String> getConstraints() {
		List<String> constraints = new ArrayList<String>();
		for (VariableDTO vdt : variables){
			if (VariableDTO.VarType.CONSTRAINT == vdt.type){
				constraints.add(vdt.constraint);
			}
		}
		return constraints;
	}
	@Override
	public String toString() {
		return "TemplateDTO [stem=" + stem + "\n, variables=" + variables + "]";
	}
	
}
