package org.rali.ljak.ecva.eval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.rali.ljak.ecva.Ecva;
import org.rali.ljak.ecva.utils.FilesOperations;

public class PostProcessing {
	
	/**
	 * Write the pairs (candidate, occurrence_of_candidate_in_res_file) in the file res_file_path+"_candCound".
	 * Occurrence is the number of times a candidate is proposed through all the n-best lists of the results file.
	 * 
	 * Method name in Ecva-cli: pp.count
	 * 
	 * @param res_file_path, n-best list from 'align' methods.
	 * @throws IOException
	 */
	public static void extractCandidatesCountInResults(String res_file_path) throws IOException{
		
		Ecva.logger.info("Extract Candidates Occurrences from Results File: "+res_file_path.split("/")[res_file_path.split("/").length-1]);
		
		String separator_res_file = "\\|";
		
		String cand;
		Map<String, Integer> candidatesCount = new HashMap<String, Integer>();
		
		Ecva.logger.trace("Read data from results file.");
		
		String reading_line = "";
		BufferedReader bf_rfp = new BufferedReader(new FileReader(new File(res_file_path)));
		while ((reading_line = bf_rfp.readLine()) != null) {
			String[] entry_cand = reading_line.split("\t");
			if (entry_cand.length > 1){
				for (String cand_val : entry_cand[1].split(separator_res_file)){
					 cand = cand_val.split(";")[0];
					 Ecva.logger.debug(cand);
					 Integer oldCount = candidatesCount.get(cand);
					 if ( oldCount == null ) {
					      oldCount = 0;
					  }
					 candidatesCount.put(cand, oldCount + 1);
				}
			}
		}
		bf_rfp.close();
		
		Ecva.logger.trace("Write candidates occurrences to file.");
		
		FileWriter fw = new FileWriter(res_file_path+"_candCount");
		Iterator<Entry<String, Integer>> it_cc = candidatesCount.entrySet().iterator();
		while (it_cc.hasNext()){
			Entry<String, Integer> it_cc_entry = it_cc.next();
			fw.write(it_cc_entry.getKey()+"\t"+it_cc_entry.getValue());
			fw.write("\n");
		}
		fw.close();
		
		Ecva.logger.trace("Done.");
	}
	
	
	/**
	 * Write filtered n-best lists from results file (res_file_path) to new results file (res_file_path+"_REMOVEFROM_"+filter_file_path)
	 * n-best lists are filtred according to a filter (filter_file_path).
	 * A candidate in n-best list is removed if it appears in filter. 
	 * 
	 * Method name in Ecva-cli: pp.rem
	 * 
	 * @param res_file_path, n-best list from 'align' methods.
	 * @param filter_file_path, list of words to filter out from res_file_path
	 * @throws IOException 
	 */
	public static void removeCandidatesInResultsAccordingList(String res_file_path, String list_file_path) throws IOException{
		
		if (FilesOperations.countLines(list_file_path) == 0){
			Ecva.logger.info("Candidates List is Empty. Skipped Step.");
			return;
		}
		
		String name_res_file = res_file_path.split("/")[res_file_path.split("/").length-1];
		String name_filter_file = list_file_path.split("/")[list_file_path.split("/").length-1];
		String name_remres_file = name_res_file+"_REMOVEFROM_"+name_filter_file;
		String path_remres_file = res_file_path+"_REMOVEFROM_"+name_filter_file;
		
		Ecva.logger.info("Remove Candidates from Results File: "+name_res_file);
		Ecva.logger.info("According to List: "+name_filter_file);
		Ecva.logger.info("Save In: "+name_remres_file);
		
		String reading_line = "";
		String cand;
		
		Ecva.logger.trace("Load List.");
		Set<String> filter = new HashSet<>();
		BufferedReader bf_ffp = new BufferedReader(new FileReader(new File(list_file_path)));
		while ((reading_line = bf_ffp.readLine()) != null) {
			filter.add(reading_line.toLowerCase());
		}
		bf_ffp.close();
		
		
		Ecva.logger.trace("Remove Candidates and Write new Results.");
		FileWriter fw = new FileWriter(path_remres_file);
	
		BufferedReader bf_rfp = new BufferedReader(new FileReader(new File(res_file_path)));
		while ((reading_line = bf_rfp.readLine()) != null) {
			String[] entry_cand = reading_line.split("\t");
			if (entry_cand.length > 1){
				fw.write(entry_cand[0]+"\t");	
				for (String cand_val : entry_cand[1].split("\\|")){
					cand = cand_val.split(";")[0];
					if (!filter.contains(cand)){
						fw.write(cand_val+"|");
					}
				}			
			}
		fw.write("\n");
		}
		bf_rfp.close();
		fw.close();
		
		Ecva.logger.trace("Done.");
	}
	
	/**
	 * TODO
	 * @param nbestList_one_path
	 * @param nbestList_two_path
	 * @param ref_file_path
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static void jaccardSimilarityBetweenTwoNBestLists(String nbestList_one_path, String nbestList_two_path, String ref_file_path) throws IOException{
		
		QueryFile nbest1 = new QueryFile(nbestList_one_path, ref_file_path, "simple");
		QueryFile nbest2 = new QueryFile(nbestList_two_path, ref_file_path, "simple");
		int size = 0;
		
		String queryBothNbest = "";
		List<String> proposedCandsForNbest1 = null;
		List<String> proposedCandsForNbest2 = null;
		double jaccardForQuery = 0.0;
		double jaccardTotal = 0.0;
		
		for (Entry<String, OneQuery> entry : nbest1.getData().entrySet())
		{
			queryBothNbest = entry.getKey();
			proposedCandsForNbest1 = (List<String>) entry.getValue().getCandidats();
			 System.out.println(queryBothNbest + "   " + proposedCandsForNbest1);
			proposedCandsForNbest2 = (List<String>) nbest2.getData().get(queryBothNbest).getCandidats();
			 System.out.println(queryBothNbest + "   " + proposedCandsForNbest2);
			
			double num = CollectionUtils.intersection(proposedCandsForNbest1, proposedCandsForNbest2).size();
			double den = CollectionUtils.union(proposedCandsForNbest1, proposedCandsForNbest2).size();
			jaccardForQuery = num/den;

			System.out.println(jaccardForQuery);
			jaccardTotal=jaccardTotal+jaccardForQuery;
			size++;
		}
		
		System.out.println("jaccardTotal: "+jaccardTotal);
		System.out.println("Size: "+size);
		System.out.println("jaccardTotalMoyen: "+jaccardTotal/size);
	}
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

//		String path = "/u/jakubinl/Documents/PhD/Redaction/Bucc2016/";
//		String file = "testSet_starbuck_1k_highfreq_7_PMI.cv_true_ALL_1-S.tcl_AGAINT_allFrTerms_7_PMI.cv_corrected";
//		
//		//extractCandidatesCountInResults(path+file);
//		
//		String list = "test_list";
//		
//		removeCandidatesInResultsAccordingList(path+file, path+list);
		
		String path = "/data/rali7/Tmp/jakubinl/CLUSTER/Experiments/coling16/ranker_analyzer/frequent/";
		String refPath = "/u/jakubinl/Documents/PhD/Ressources/data/starbuck/intersection_wikipedia/";
		
		jaccardSimilarityBetweenTwoNBestLists(path+"context-7-PMI-TRUE-ALL-1S_1k-FREQ-SB_NBEST100.align", 
				path+"embedding-CBOW-ITER2-NEGATIVE10-SIZE200-WIN5-TrainOn5kHighSB_1k-FREQ-SB_NBEST100.align", 
				refPath+"testSet_starbuck_1k_highFreq.txt");
		
	}

}
