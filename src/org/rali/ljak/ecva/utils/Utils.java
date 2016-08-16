package org.rali.ljak.ecva.utils;

import java.text.DecimalFormat;
import java.util.Arrays;

public class Utils {

	public static final DecimalFormat df = new DecimalFormat("0.000"); 
	
	/**
	 * Transform a String array (eg. "1 2 3") to Integer array.
	 * @param stringArray
	 * @return
	 */
	public static Integer[] convertStringArrayToIntegerArray(String stringArray){
		String[] splitStringArray = stringArray.split(" ");
		Integer[] integerArray = new Integer[splitStringArray.length];
		for (int i = 0; i < splitStringArray.length; i++){
			integerArray[i] = Integer.parseInt(splitStringArray[i]);
		}
		return integerArray;
	}
	
	
	public static void main(String[] args) {
		System.out.println(Arrays.toString(convertStringArrayToIntegerArray("1 2 3")));

	}

}
