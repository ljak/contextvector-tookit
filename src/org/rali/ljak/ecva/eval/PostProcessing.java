package org.rali.ljak.ecva.eval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
		
	}

}
