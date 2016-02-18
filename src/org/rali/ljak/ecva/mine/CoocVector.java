package org.rali.ljak.ecva.mine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.rali.ljak.ecva.Ecva;
import org.rali.ljak.ecva.utils.Utils;

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
	
	
	public void addCooc(String cooc, int window){
		
		assert cooc != "";
		assert window != 0;
		
		if (!this.cooccurrents.containsKey(cooc)){
			Integer[] wbf = new Integer[Mining.MaxWindow+1];
			Arrays.fill(wbf, 0);
			//wbf[0] = TODO: function that return the frequency of cooc.
			Ecva.logger.info("TODO CoocVector: Function that return the frequency of cooc.");
			wbf[window] = 1;
			this.cooccurrents.put(cooc, wbf);
		} else {
			Integer[] wbf = this.cooccurrents.get(cooc);
			wbf[window] = wbf[window]+1;
			this.cooccurrents.put(cooc, wbf);
		}
	}
	
	
	public void addCooc(String cooc, int window, Set<String> antiCooc){
		if (antiCooc != null && !antiCooc.contains(cooc)){
			addCooc(cooc, window);
		}
	}	
	

	public String toString(){
		String res = this.word+"\t"+this.frequency+"";
		
		if (!this.cooccurrents.isEmpty()) {
			res = res+"\t";
			Iterator<Entry<String, Integer[]>> it = this.cooccurrents.entrySet().iterator();
			while (it.hasNext()){
				Map.Entry<String, Integer[]> elt = it.next();
				res = res + elt.getKey()+":"+"[";
				for (int i = 0 ; i < elt.getValue().length ; i++){
					res = res + elt.getValue()[i] + " ";
				}
				res = res + "]"+"|";
			}
		}
		return res+"\t"+getSize();
	}
	
	
	public static CoocVector readFromString(String str){
		
		if (str == null || str.isEmpty()) return null;
		String[] str_parts = str.split("\t"); // str_parts[0] = word, str_parts[1] = frequency, str_parts[2] = cooccurrents(, str_parts[3] = size)
		if (str_parts.length == 0 || str_parts[0].isEmpty()) return null;
		
		CoocVector cv = new CoocVector(str_parts[0]);
		cv.setFrequency(Integer.parseInt(str_parts[1]));
		
		String[] coocs_wbf = str_parts[2].split("\\]\\|");
		for (String cooc_wbf : coocs_wbf){
			String[] cooc_td_wbf = cooc_wbf.split(":\\[");
			
			String cooc = cooc_td_wbf[0]; 
			Integer[] wbf_int = Utils.convertStringArrayToIntegerArray(cooc_td_wbf[1]); // window-based frequencies array, from String to Integer.
			
			cv.getCooccurrents().put(cooc, wbf_int);
		}
		return cv;
	}
	
	
	public void filter(String cooc, int window, int frequency){
		//TODO
	}

	
	public static void main(String[] args) {
		
//		Mining.MaxWindow = 5;
//		
//		CoocVector cv = new CoocVector("test");
//		cv.addCooc("un", 1);
//		cv.addCooc("deux", 2);
//		cv.addCooc("trois", 3);
//		cv.addCooc("quatre", 4);
//		cv.addCooc("cinq", 5);
//		cv.addCooc("trois", 3);
//		cv.addCooc("trois", 1);
//		cv.addCooc("trois", 5);
//		
//		System.out.println(cv.toString());
		
		String test = "test	0	trois:[0 1 0 2 0 1 ]|cinq:[0 0 0 0 0 1 ]|quatre:[0 0 0 0 1 0 ]|un:[0 1 0 0 0 0 ]|deux:[0 0 1 0 0 0 ]|	5";
		
		CoocVector cv = CoocVector.readFromString(test);
		System.out.println(cv.toString());
	}

}
