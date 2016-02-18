package org.rali.ljak.ecva.mine;

import java.util.HashMap;
import java.util.Map;

public class CoocVector {
	
	private String word;
	private int frequency;
	
	private Map<String, Integer[]> cooccurrents; //key: cooc ; value: window-based frequencies
	
	public CoocVector(String w){ 
		this.word = w;
		this.frequency = 0;
		this.cooccurrents = new HashMap<String, Integer[]>();
	}
	
	
	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public Map<String, Integer[]> getCooccurrents() {
		return cooccurrents;
	}

	public void setCooccurrents(Map<String, Integer[]> cooccurrents) {
		this.cooccurrents = cooccurrents;
	}

	public int getSize(){
		return this.cooccurrents.size();
	}
	
	// +filter
	public void addCooc(String cooc){
		
	}
	
	public String toString(){
		return "";	
	}
	
	public void readFromString(String str){
		
	}
	
	public void filter(String cooc, int window, int frequency){
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
