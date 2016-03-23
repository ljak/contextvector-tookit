package org.rali.ljak.ecva.eval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.rali.ljak.ecva.Ecva;
import org.rali.ljak.ecva.utils.FilesOperations;


public class QueryFile {
	
	
	/**
	 * Attributes
	 */
	private String nameResFile;
	private String nameRefFile;
	private String nameSubFile;
	
	private Map<String, OneQuery> data;
	private int size;
	private int maxNbrCandidats;
	
	private List<Double> precisionAtK;
	private List<Double> recallAtK;
	private List<Double> mapAtK;
	private List<Integer> topAtK;
	private List<List<String>> successAtK;
	private List<List<String>> failsAtK;
	
	
	/**
	 * Constructors
	 */
	public QueryFile(String res_file_path, String ref_file_path, String subSetRes_file_path) throws IOException{
		
		this.nameResFile = res_file_path.split("\\/")[res_file_path.split("\\/").length-1];
		this.nameRefFile = ref_file_path.split("\\/")[ref_file_path.split("\\/").length-1];
		this.nameSubFile = subSetRes_file_path.split("\\/")[subSetRes_file_path.split("\\/").length-1];
		
		Ecva.logger.info("Evaluation of Results File: "+nameResFile);
		Ecva.logger.info("According to Reference: "+nameRefFile);
		if (nameSubFile.isEmpty()){Ecva.logger.info("No SubSet File used.");} else {Ecva.logger.info("SubSet File: "+nameSubFile);}
		
		this.data = loadData(res_file_path, ref_file_path, subSetRes_file_path);
		this.size = this.data.size();
		
		this.precisionAtK =  new ArrayList<Double>((Collections.nCopies(maxNbrCandidats+1, 0.0)));
		this.recallAtK =  new ArrayList<Double>((Collections.nCopies(maxNbrCandidats+1, 0.0)));
		this.mapAtK =  new ArrayList<Double>((Collections.nCopies(maxNbrCandidats+1, 0.0)));
		this.topAtK =  new ArrayList<Integer>((Collections.nCopies(maxNbrCandidats+1, 0)));
		
		this.successAtK = new ArrayList<List<String>>();
			for (int i = 0 ; i < maxNbrCandidats+1 ; i++){
				this.successAtK.add(new ArrayList<String>());
			}
		
		this.failsAtK = new ArrayList<List<String>>();
			for (int i = 0 ; i < maxNbrCandidats+1 ; i++){
				this.failsAtK.add(new ArrayList<String>());
			}
			
		evaluateAllQueriesAtAllK();
		
		if (Ecva.logger.isDebugEnabled()) {debug();}
		debug();
	}
	
	public QueryFile(String res_file_path, String ref_file_path) throws IOException{
		this(res_file_path, ref_file_path, "");
	}
	
	
	/**
	 * Getters and Setters
	 */
	public String getResName(){
		return this.nameResFile;
	}
	
	public String getRefName(){
		return this.nameRefFile;
	}
	
	public String getSubName(){
		return this.nameSubFile;
	}


	/**
	 * Public Methods
	 */
	public double getMeanPrecisionAtK(int cut_off){
		if (cut_off > maxNbrCandidats) cut_off = maxNbrCandidats;
		return this.precisionAtK.get(cut_off)/this.size;
	}
	
	public double getMeanRecallAtK(int cut_off){
		if (cut_off > maxNbrCandidats) cut_off = maxNbrCandidats;
		return this.recallAtK.get(cut_off)/this.size;
	}
	
	public double getMeanAveragePrecisionAtK(int cut_off){
		if (cut_off > maxNbrCandidats) cut_off = maxNbrCandidats;
		return this.mapAtK.get(cut_off)/this.size;
	}
	
	public double getTOPAtK(int cut_off){
		if (cut_off > maxNbrCandidats) cut_off = maxNbrCandidats;
		return this.topAtK.get(cut_off)/(double)this.size;
	}
	
	public List<String> getSuccessAtK(int cut_off){
		if (cut_off > maxNbrCandidats) cut_off = maxNbrCandidats;
		return this.successAtK.get(cut_off);
	}
	
	public List<String> getFailsAtK(int cut_off){
		if (cut_off > maxNbrCandidats) cut_off = maxNbrCandidats;
		return this.failsAtK.get(cut_off);
	}
	
	
	/**
	 * Private Methods
	 */
	private void evaluateAllQueriesAtAllK() throws IOException{
		
		Ecva.logger.info("Evaluate All Queries at All K.");
		
		for (Entry<?, OneQuery> entry : this.data.entrySet()){
			
			for (int i = 1; i < this.maxNbrCandidats+1 ; i++){ //TODO: update OneQuery pour éviter de boucler dans ses méthodes pour des entrées de précision (rappels, etc.) différentes.
				
				this.precisionAtK.set(i, this.precisionAtK.get(i) + entry.getValue().getPrecisionAtK(i));
				this.recallAtK.set(i, this.recallAtK.get(i) + entry.getValue().getRecallAtK(i));
				this.topAtK.set(i, this.topAtK.get(i) + entry.getValue().getTOPAtK(i));
				this.mapAtK.set(i, this.mapAtK.get(i) + entry.getValue().getAveragePrecisionAtK(i));
				
				if (entry.getValue().getTOPAtK(i) != 0){
					this.successAtK.get(i).add((String) entry.getKey());
				}
				
				if (entry.getValue().getTOPAtK(i) == 0){
					this.failsAtK.get(i).add((String) entry.getKey());
				}
			}
		}
	}
	
	private Map<String, OneQuery> loadData(String res_file_path, String ref_file_path, String subSetRes_file_path) throws IOException{
		
		// TODO: to move and put in arguments
		String separator_res_file = "\\|";
		String separator_ref_file = "\t"; //String separator_ref_file = " \\| ";
		
		Ecva.logger.info("Loading Data...");
		long startTime = System.nanoTime();
		
		int count = 0;
		int p = 1;
		int pp = 0;
		int nbr_lines = FilesOperations.countLines(res_file_path);
		
		Map<String, OneQuery> loaded_data = new HashMap<String, OneQuery>();
		
		String reading_line = "";
		
		Set<String> subSetRes = null;
		if (!subSetRes_file_path.isEmpty()){
			subSetRes = new HashSet<String>();
			
			BufferedReader bf_ssr = new BufferedReader(new FileReader(new File(subSetRes_file_path)));
			while ((reading_line = bf_ssr.readLine()) != null) {
				subSetRes.add(reading_line.toLowerCase());
			}
			bf_ssr.close();	
		}
		
		
		BufferedReader bf_ffp = new BufferedReader(new FileReader(new File(ref_file_path)));
		while ((reading_line = bf_ffp.readLine()) != null) {
			reading_line = reading_line.toLowerCase();
			String[] entry_ref = reading_line.split(separator_ref_file);
			
			if (subSetRes != null){
				if (subSetRes.contains(entry_ref[0].toLowerCase())) {
					Set<String> references = new HashSet<String>(Arrays.asList (Arrays.copyOfRange(entry_ref, 1, entry_ref.length)));
					replace(references); // TODO: temporary. Try to pass at Java 8 and Stream/Map
					loaded_data.put(entry_ref[0].toLowerCase(), new OneQuery(references));
				}
			} else {
				Set<String> references = new HashSet<String>(Arrays.asList (Arrays.copyOfRange(entry_ref, 1, entry_ref.length)));
				replace(references); // TODO: temporary. Try to pass at Java 8 and Stream/Map
				loaded_data.put(entry_ref[0].toLowerCase(), new OneQuery(references));
			}
		}
		bf_ffp.close();
		
		
		BufferedReader bf_rfp = new BufferedReader(new FileReader(new File(res_file_path)));
		while ((reading_line = bf_rfp.readLine()) != null) {
			reading_line = reading_line.toLowerCase();
			String[] entry_cand = reading_line.split("\t");
			if (loaded_data.containsKey(entry_cand[0].toLowerCase())) {
				if (entry_cand.length > 1){
					List<?> cands = resultsFilter(Arrays.asList (Arrays.copyOfRange(entry_cand[1].split(separator_res_file),0,entry_cand[1].split(separator_res_file).length)));
					OneQuery oq = loaded_data.get(entry_cand[0].toLowerCase()); oq.setCandidats(cands);
					loaded_data.put(entry_cand[0].toLowerCase(), oq);
				} else { // If no candidates, return a empty list for safe coding.
					List<?> empty_cands = new ArrayList<String>(); 
					OneQuery oq = loaded_data.get(entry_cand[0].toLowerCase()); oq.setCandidats(empty_cands);
					loaded_data.put(entry_cand[0].toLowerCase(), oq);
				}
			}
			
			count++;
			
        	p = Math.round((((float)count/(float)nbr_lines)*10));
        	if (p != pp) {
        		if (Ecva.logger.isInfoEnabled()) {System.out.print(p+"0%..");}
        		pp = p;
        	}
			
		}
		bf_rfp.close();
		
		long stopTime = System.nanoTime();
		if (Ecva.logger.isInfoEnabled()) {System.out.println("");}
		Ecva.logger.info("Done. (Execution Time: "+((stopTime-startTime)/1000000000)+" seconds.)");
			
		return loaded_data;
	}
	
	/**
	 * Can be used for more advanced filters, like filters on specifics words, etc.
	 * @param in
	 * @return
	 */
	private List<String> resultsFilter(List<String> in){
		
		List<String> res = new ArrayList<String>();
		
		for (String entry : in){
			String[] trad_score = entry.split(";");
			res.add(trad_score[0]);
		}
		
		if (res.size() > this.maxNbrCandidats) this.maxNbrCandidats = res.size();
		
		return res;
	}
	

	/**
	 * replace all string in Set by their lowercase version.
	 * Place in utils.
	 * @param strings
	 */
	private static void replace(Set<String> strings)
	{
		String[] stringsArray = strings.toArray(new String[0]);
		for (int i=0; i<stringsArray.length; ++i)
		{
			stringsArray[i] = stringsArray[i].toLowerCase();
		}
		strings.clear();
		strings.addAll(Arrays.asList(stringsArray));
	}
	
	
	private void debug(){
		
		for (Entry<String, OneQuery> entry : data.entrySet()) {
			Ecva.logger.debug(entry.getKey()+" : "+entry.getValue().getCandidats()+"\t"+entry.getValue().getReferences());			
		}
		
		Ecva.logger.debug("size: "+size);
		Ecva.logger.debug("maxNbrCandidats: "+maxNbrCandidats);
		Ecva.logger.debug("precisionAtK: "+precisionAtK);
		Ecva.logger.debug("recallAtK: "+recallAtK);
		Ecva.logger.debug("mapAtK: "+mapAtK);
		Ecva.logger.debug("homeMadePrecisionAtK: "+topAtK);
		//logger.debug(failsAtK);
	}
	
	
	/**
	 * For testing only.
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		/**
		 * Tests 'getAveragePrecisionAtK'
		 */
//		double t = getAveragePrecisionAtK(new ArrayList<Integer> (Arrays.asList (new Integer[]{6, 4, 7, 1, 2})), 
//				new HashSet<Integer> (Arrays.asList(new Integer[]{1, 2, 3, 4, 5})), 
//				0);
//		
//		double t2 = getAveragePrecisionAtK(new ArrayList<String> (Arrays.asList (new String[]{"1", "4", "7", "6", "2"})), 
//				new HashSet<String> (Arrays.asList(new String[]{"1", "2", "3", "4", "5"})), 
//				1);
//		
//		double t3 = getAveragePrecisionAtK(new ArrayList<String> (Arrays.asList (new String[]{"6", "4", "7", "1", "2"})), 
//				new HashSet<String> (Arrays.asList(new String[]{"1", "2", "3", "4", "5"})), 
//				5);
//
//		System.out.println(t);
//		System.out.println(t2);
//		System.out.println(t3);
		
		/**
		 * Tests getMeanAveragePrecisionAtK
		 */
//		Map<String, List<List<?>>> test = new HashMap<String, List<List<?>>>();
//		
//		List<List<?>> t1 = new ArrayList<List<?>>();
//		List<Integer> t1_can = new ArrayList<Integer> (Arrays.asList (new Integer[]{6, 4, 7, 1, 2}));
//		List<Integer> t1_ref = new ArrayList<Integer> (Arrays.asList (new Integer[]{1, 2, 3, 4, 5}));
//		t1.add(t1_can);
//		t1.add(t1_ref);
//		
//		List<List<?>> t2 = new ArrayList<List<?>>();
//		List<String> t2_can = new ArrayList<String> (Arrays.asList (new String[]{"6", "4", "7", "1", "2"}));
//		List<String> t2_ref = new ArrayList<String> (Arrays.asList (new String[]{"1", "2", "3", "4", "5"}));
//		t2.add(t2_can);
//		t2.add(t2_ref);
//		
//		test.put("t1", t1);
//		test.put("t2", t2);
//		
//		double t = getMeanAveragePrecisionAtK(test, 5);
//		
//		System.out.println(t);
		
		/**
		 * Tests Complet Class
		 */
		QueryFile t1 = new QueryFile("/data/rali6/Tmp/jakubinl/CLUSTER/Experiments/wmt2016_taskBiAlign/bug/game", 
				"/data/rali6/Tmp/jakubinl/CLUSTER/Experiments/wmt2016_taskBiAlign/train.pairs_game");
		
		
		
//		System.out.println("precisionatK "+t1.precisionAtK);
//		System.out.println("recallatK "+t1.recallAtK);
//		System.out.println("hmpatK "+t1.homeMadePrecisionAtK);
//		System.out.println("mapatK "+t1.mapAtK);
		
		//System.out.println(t1.getMeanPrecisionAtK(2));
//		System.out.println(t1.getTOPAtK(20));
////		System.out.println(t1.getMeanPrecisionAtK(1));
////		System.out.println(t1.getMeanPrecisionAtK(5));
//		System.out.println(t1.getMeanPrecisionAtK(20));
		
		System.out.println(t1.getTOPAtK(1));
//		System.out.println(t1.getSuccessAtK(1));
//		System.out.println(t1.getFailsAtK(1));
	}
	
}
