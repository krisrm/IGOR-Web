package com.igor.shared;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.igor.shared.VariableDTO.VarType;

public class VariableDTO implements IsSerializable {

	public enum VarType {
		TEXT, NUMERIC, IMAGE, CONSTRAINT
	}

	public VarType type = VarType.TEXT;
	private String name = "";

	@Override
	public String toString() {
		return  "[" + name + "type=" + type + ", keys="
				+ keys + ", step=" + step + ", min=" + min + ", max=" + max
				+ ", urls=" + urls + ", constraint=" + constraint + "]\n";
	}

	public String getName() {
		if (type == VarType.CONSTRAINT)
			return constraint;
		return name;
	}

	public void setName(String name) {
		if (type == VarType.CONSTRAINT) {
			constraint = name;
		} else {
			this.name = name;
		}
	}

	// text
	public List<String> keys = new ArrayList<String>();

	// numeric
	public double step;
	public double min;
	public double max;

	// image
	public List<String> urls = new ArrayList<String>();

	// constraint
	public String constraint = "";

	public VariableDTO() {
	}

	public static VariableDTO test(int i) {
		VariableDTO test = new VariableDTO();
		test.setName("Test Variable" + i);
		test.type = VarType.TEXT;
		return test;
	}

	public String[] getTextKeys() {
		return keys.toArray(new String[] {});
	}

//	public String[] getEscapedUrls() {
//		String[] r = new String[urls.size()];
//		int i = 0;
//		for (String url : urls) {
//			r[i] = "&lt;img src='" + url + "' alt='" + name + "'&gt;";
//			i++;
//		}
//		return r;
//	}
	
	public String[] getWrappedUrls() {
		String[] r = new String[urls.size()];
		int i = 0;
		for (String url : urls) {
			r[i] = "<img src=\"" + url + "\" alt=\"" + name + "\">";
			i++;
		}
		return r;
	}

	public void addImg(String img) {
		if (VariableDTO.isImg(img)){
			img = img.substring(img.indexOf("src")+3);
			img = img.substring(img.indexOf("=")+1);
			try {
				img = img.substring(img.indexOf('"')+1);
				img = img.substring(0, img.indexOf('"'));
			} catch (StringIndexOutOfBoundsException e){
				img = img.substring(img.indexOf("'")+1);
				img = img.substring(0, img.indexOf("'"));
			}
			urls.add(img);
		}
	}

	public void setImages(String[] imgTags){
		for (String img : imgTags){
			addImg(img);
		}
	}

	public static boolean isImg(String img) {
		return (img.contains("img") && img.contains("src"));
	}

	public int[] getIntKeysUrl() {
		if (urls == null || urls.size() == 0)
			return new int[] {};
		int[] r = new int[urls.size()];
		for (int i = 0; i < r.length; i++)
			r[i] = i + 1;
		return r;
	}

	public int[] getIntKeysText() {
		if (keys == null || keys.size() == 0)
			return new int[] {};
		int[] r = new int[keys.size()];
		for (int i = 0; i < r.length; i++)
			r[i] = i + 1;
		return r;
	}

	public double[] getRange() {
		return new double[] { min, max };
	}

	public void setTypeString(String cellValue) {
		VarType type = VarType.valueOf(cellValue);
		if (type != null) {
			this.type = type;
		}
	}

	public String getTypeString() {
		String type = this.type.toString();
		return type.substring(0, 1) + type.substring(1).toLowerCase();
	}

	public static String[] typeStrings() {
		String[] r = new String[VarType.values().length];
		int i = 0;
		for (VarType v : VarType.values()) {
			r[i] = v.toString().substring(0, 1)
					+ v.toString().substring(1).toLowerCase();
			i++;
		}
		return r;
	}

	public static int toIndex(VarType type) {
		for (int i = 0; i < VarType.values().length; i++) {
			if (VarType.values()[i] == type) {
				return i;
			}
		}
		return 0;
	}

	public String getMin() {
		return Double.toString(min);
	}

	public void setMin(String text) {
		try {
			min = Double.parseDouble(text);
		} catch (NumberFormatException e) {
			min = 0;
		}
	}

	public String getMax() {
		return Double.toString(max);
	}

	public void setMax(String text) {
		try {
			max = Double.parseDouble(text);
		} catch (NumberFormatException e) {
			max = 0;
		}
	}

	public String getStep() {
		return Double.toString(step);
	}

	public void setStep(String text) {
		try {
			step = Double.parseDouble(text);
		} catch (NumberFormatException e) {
			step = 0;
		}
	}


}
