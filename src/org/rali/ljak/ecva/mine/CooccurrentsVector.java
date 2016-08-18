package org.rali.ljak.ecva.mine;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.rali.ljak.ecva.utils.Utils;

public class CooccurrentsVector {
	
	private String word;
	private int frequency;
	
	private int windowSize; // or MaxWindowPos
	private Map<String, Integer[]> cooccurrents; // [key:cooccurrent ; value:window-based frequencies]
	
	
	public CooccurrentsVector(String w){ 
		this.word = w;
		this.frequency = 0;
		this.windowSize = 0;
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

	public int getWindowSize() {
		return windowSize;
	}

	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
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
	
	
	public void addCooc(String cooc, int coocFrequency, int windowPos){
		
		assert cooc != "" || cooc != null;
		assert coocFrequency > 0;
		assert windowPos > 0 && windowPos <= windowSize;
		
		if (!this.cooccurrents.containsKey(cooc)){
			Integer[] wbf = new Integer[windowSize+1];
			Arrays.fill(wbf, 0);
			wbf[0] = coocFrequency;
			wbf[windowPos] = 1;
			this.cooccurrents.put(cooc, wbf);
		} else {
			Integer[] wbf = this.cooccurrents.get(cooc);
			wbf[windowPos] = wbf[windowPos]+1;
			this.cooccurrents.put(cooc, wbf);
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
	
	
	public static CooccurrentsVector readFromString(String str){
		
		if (str == null || str.isEmpty()) return null;
		
		String[] str_parts = str.split("\t"); // str_parts[0] = word, str_parts[1] = frequency, str_parts[2] = cooccurrents(, str_parts[3] = size)
		if (str_parts.length == 0 || str_parts[0].isEmpty()) return null;
		
		CooccurrentsVector cv = new CooccurrentsVector(str_parts[0]);
		cv.setFrequency(Integer.parseInt(str_parts[1]));
		
		String[] coocs_wbf = str_parts[2].split("\\]\\|");
		for (String cooc_wbf : coocs_wbf){
			String[] cooc_td_wbf = cooc_wbf.split(":\\[");
			
			String cooc = cooc_td_wbf[0]; 
			Integer[] wbf_int = Utils.convertStringArrayToIntegerArray(cooc_td_wbf[1]); // window-based frequencies array, from String to Integer.
			cv.setWindowSize(wbf_int.length-1);
			
			cv.getCooccurrents().put(cooc, wbf_int);
		}
		return cv;
	}
	
	
	public void construcOnTextFile(Path textFile, int windowSize, int maxFrequency){
		
		
	}
	
	
	public void constructOnLuceneIndex(Path LuceneIndex, int windowSize, int maxFrequency){
		
		
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
		
		String test = "test	0	trois:[0 1 0 2 0 1 ]|cinq:[0 0 0 0 0 1 ]|quatre:[0 0 0 0 1 0 ]|un:[0 1 0 0 0 0 ]|	5"; //deux:[0 0 1 0 0 0 ]|
		
		CooccurrentsVector cv = CooccurrentsVector.readFromString(test);
		System.out.println(cv.toString());
		System.out.println(cv.getWindowSize());
	}

}
