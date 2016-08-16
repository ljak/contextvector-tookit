package org.rali.ljak.ecva.eval;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OneQuery {

	/**
	 * Attributes
	 */
	private List<?> candidats;
	private Set<?> references;
	private Set<?> candidatsFilter;
	
	private List<Integer> positionOfRefsInCands; // get(i) return the position in the candidats list of the i found reference. (as an index)
	private boolean evaluate;
	
	/**
	 * Constructors
	 */
	public OneQuery(List<?> result, Set<?> reference, Set<?> resultFilter) {
		super();
		this.candidats = result;
		this.references = reference;
		this.candidatsFilter = resultFilter;
		
		this.positionOfRefsInCands = new ArrayList<Integer>();
		this.evaluate = false;
	}

	public OneQuery(List<?> result, Set<?> reference) {
		this(result, reference, new HashSet<>());
	}
	
	public OneQuery(Set<?> reference) {
		this(new ArrayList<>(), reference, new HashSet<>());
	}

	
	/**
	 * Getters and Setters
	 */
	public List<?> getCandidats() {
		return candidats;
	}

	public void setCandidats(List<?> candidats) {
		this.candidats = candidats;
	}

	public Set<?> getReferences() {
		return references;
	}

	public void setReferences(Set<?> references) {
		this.references = references;
	}

	public Set<?> getCandidatsFilter() {
		return candidatsFilter;
	}

	public void setCandidatsFilter(Set<?> candidatsFilter) {
		this.candidatsFilter = candidatsFilter;
	}
	
	public List<Integer> getpositionOfRefsInCands(){
		return this.positionOfRefsInCands;
	}

	
	/**
	 * Public Methods
	 */
	public void evaluate(){
		if (evaluate) return;
		genPositionsOfRefsInCands();
	}
	
	public double getPrecisionAtK(int cut_off_K){
		evaluate();
		if (positionOfRefsInCands.isEmpty()) return 0.00;
		
		int up_bound = (cut_off_K < candidats.size()) ? cut_off_K : candidats.size();
		
		return (nbrRefFoundAtK(cut_off_K)/(double)up_bound); 
	}

	public double getPrecision(){
		return getPrecisionAtK(references.size());
	}
	
	public double getRecallAtK(int cut_off_K){
		evaluate();
		if (positionOfRefsInCands.isEmpty()) return 0.00;
		
		return (nbrRefFoundAtK(cut_off_K)/(double)references.size());
	}
	
	public double getRecall(){
		return getPrecisionAtK(references.size());
	}
	
	public int getTOPAtK(int cut_off_K){
		evaluate();
		if (positionOfRefsInCands.isEmpty()) return 0;
		
		if (nbrRefFoundAtK(cut_off_K) > 0) return 1;
		
		return 0;
	}
	
	public int getTOP(){
		return getTOPAtK(references.size());
	}
	
	public double getAveragePrecisionAtK(int cut_off_K){
		evaluate();
		double ap = 0.0;
		
		if (positionOfRefsInCands.isEmpty()) return ap;
		
		for (int i = 0 ; i < positionOfRefsInCands.size() ; i++){
			int pos = positionOfRefsInCands.get(i);
			if (pos <= cut_off_K) {ap = ap + ((i+1)/((double)pos));}
		}
		
		int up_bound = (cut_off_K < candidats.size()) ? cut_off_K : candidats.size();
		return (up_bound <= references.size()) ? ap/up_bound : ap/references.size();
	}
	
	public double getAveragePrecision(){
		return getAveragePrecisionAtK(references.size());
	}
	
	
	/**
	 * Private Methods
	 */
	private void genPositionsOfRefsInCands() {
		
		if (candidats.size() == 0 || references.size() == 0) return;
		
		Object cand = null;
		
		for (int i = 0 ; i < candidats.size() && positionOfRefsInCands.size() < references.size() ; i++){
			
			if (candidats.get(i).getClass().equals(Arrays.class)){
				cand = ((Object[]) candidats.get(i))[0];
			} else {
				cand = candidats.get(i);
			}
			
			if (references.contains(cand)) {
				if (!candidatsFilter.isEmpty() && candidatsFilter.contains(cand)){
					positionOfRefsInCands.add(i+1);
				} else if (candidatsFilter.isEmpty()) {
					positionOfRefsInCands.add(i+1);
				}
			}
		}
		evaluate = true;
	}
	
	private int nbrRefFoundAtK(int cut_off_K) {
		
		if (positionOfRefsInCands.isEmpty()) return 0;
		
		if (cut_off_K >= positionOfRefsInCands.get(positionOfRefsInCands.size()-1)) return positionOfRefsInCands.size();
		
		for (int i = 0 ; i < positionOfRefsInCands.size() ; i++){
			if (cut_off_K < positionOfRefsInCands.get(i)){
				if (i == 0) return 0; else return i;
			} else if (cut_off_K == positionOfRefsInCands.get(i)){
				return i+1;
			}
		}
		return 0;
	}
	
	
	/**
	 * For testing only.
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		OneQuery se = new OneQuery(new ArrayList<Integer> (Arrays.asList (new Integer[]{6, 4, 7, 1, 2})), 
				new HashSet<Integer> (Arrays.asList(new Integer[]{4}))
				//(Set<?>) new HashSet<Integer> (Arrays.asList(new Integer[]{7,1}))
				);
		
		System.out.println(se.candidats);
		System.out.println(se.references);
		System.out.println(se.candidatsFilter);
		
		
//		System.out.println(se.getAveragePrecisionAtK(0));
//		System.out.println(se.getAveragePrecisionAtK(1));
//		System.out.println(se.getAveragePrecisionAtK(3));
//		System.out.println(se.getAveragePrecisionAtK(4));
//		System.out.println(se.getAveragePrecisionAtK(5));
//		System.out.println(se.getAveragePrecisionAtK(6));
		
		System.out.println(se.positionOfRefsInCands);
		
		System.out.println(se.getTOPAtK(1));
//		System.out.println(se.getHomeMadePrecisionAtK(2));
//		System.out.println(se.getHomeMadePrecisionAtK(3));
//		System.out.println(se.getHomeMadePrecisionAtK(4));
//		System.out.println(se.getHomeMadePrecisionAtK(5));
//		System.out.println(se.getHomeMadePrecisionAtK(6));
		
		for (int i = 1 ; i < 11 ; i++){
			System.out.println("nbrRefFoundAtK "+i+" :"+se.nbrRefFoundAtK(i));
		}
		
	}
}
