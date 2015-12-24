package org.rali.ljak.ecva;

import java.io.IOException;
import java.text.DecimalFormat;

import org.rali.ljak.ecva.align.SimilarityMeasures;
import org.rali.ljak.ecva.build.AssociationMeasures;
import org.rali.ljak.ecva.eval.QueryFile;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class Ecva {

	/**
	 * Main class of the eCVa-Toolkit.
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		ArgumentParser MainParser = ArgumentParsers.newArgumentParser("eCVa-Toolkit");
		MainParser.description("explicit Context Vector analysis - Toolkit");
		
		Subparsers subParserManager = MainParser.addSubparsers().title("Commands").description("Available Commands").metavar("COMMAND");
		
		/**
		 * MineCoocMatrix Command and Arguments Definitions
		 */
		Subparser MCMparser = subParserManager.addParser("MineCoocMatrix").aliases("mine").help("'mine -h' for Additional Help");
		MCMparser.setDefault("call", new MineCoocMatrix());
		
		MCMparser.addArgument("source_corpora").type(Arguments.fileType()); //could be a plain text or a lucene index
		MCMparser.addArgument("output_file(.cm)").type(Arguments.fileType());
		
		MCMparser.addArgument("-words_list_file", "-wlf").metavar("FILE").type(Arguments.fileType()); //if not present, take all the vocabulary
		MCMparser.addArgument("-coocs_filter_file", "-cff").metavar("FILE").type(Arguments.fileType()); //if not present, all words can be coocs
		MCMparser.addArgument("-max_win", "-mw").type(Integer.class).setDefault(7).metavar("N");
		MCMparser.addArgument("-max_freq", "-mf").type(Long.class).setDefault(Long.MAX_VALUE).metavar("N");
		
		MCMparser.addArgument("-verbose", "-v").action(Arguments.count());
		//MCMparser.addArgument("-lg");
		//MCMparser.addArgument("-tokenizer");
		//MCMparser.addArgument("-sub_set_text");
		/**
		 */
		
		/**
		 * BuildContextVectors Command and Arguments Definitions
		 */
		Subparser BCVparser = subParserManager.addParser("BuildContextVectors").aliases("build").help("'build -h' for Additional Help");
		BCVparser.setDefault("call", new BuildContextVectors());
		
		BCVparser.addArgument("cooc_matrix_file(.cm)").type(Arguments.fileType());
		BCVparser.addArgument("output_file(.cv)").type(Arguments.fileType());
		
		BCVparser.addArgument("association_measure").type(AssociationMeasures.class).choices(AssociationMeasures.values());
		BCVparser.addArgument("-window_size", "-ws").type(Integer.class).setDefault(7).metavar("N"); //verify according to max_win from .cm file
		BCVparser.addArgument("-cut_off_freq", "-cof").type(Integer.class).setDefault(0).metavar("N");
		BCVparser.addArgument("-coocs_filter_file", "-cff").metavar("FILE").type(Arguments.fileType()); //if not present, all words can be coocs
		
		BCVparser.addArgument("-verbose", "-v").action(Arguments.count());
		/**
		 */
		
		/**
		 * TranslateContextVectors Command and Arguments Definitions
		 */
		Subparser TCVparser = subParserManager.addParser("TranslateContextVectors").aliases("trans").help("'trans -h' for Additional Help");
		TCVparser.setDefault("call", new TranslateContextVectors());
		
		TCVparser.addArgument("contextvectors_file(.cv)").type(Arguments.fileType());
		TCVparser.addArgument("output_file(.tcv)").type(Arguments.fileType());
		TCVparser.addArgument("bilingual_lexicon").type(Arguments.fileType());
		
		TCVparser.addArgument("-filter_by_lex", "-fbl").action(Arguments.storeTrue());
		TCVparser.addArgument("-how_many_trans", "-hmt"); //TODO: choice ? voir dico classe
		
		TCVparser.addArgument("-verbose", "-v").action(Arguments.count());
		/**
		 */
		
		/**
		 * AlignContextVectors Command and Arguments Definitions
		 * --thread (?)
		 */
		Subparser ACRparser = subParserManager.addParser("AlignContextVectors").aliases("align").help("'align -h' for Additional Help");
		ACRparser.setDefault("call", new AlignContextVectors());
		
		ACRparser.addArgument("source_contextvectors_file(.cv/.tcv)").type(Arguments.fileType());
		ACRparser.addArgument("targuet_output_file(.cv/.tcv)").type(Arguments.fileType());
		ACRparser.addArgument("output_file(.alignRes)").type(Arguments.fileType());
		
		ACRparser.addArgument("-nbr_candidats", "-nbc").type(Integer.class).setDefault(20).metavar("N");
		ACRparser.addArgument("-similarity_measure", "-sim").type(SimilarityMeasures.class).choices(SimilarityMeasures.values()).setDefault(SimilarityMeasures.COS);
		
		ACRparser.addArgument("-verbose", "-v").action(Arguments.count());
		/**
		 */
		
		/**
		 * EvaluateAlignResults Command and Arguments Definitions
		 */
		Subparser EARparser = subParserManager.addParser("EvaluateAlignResults").aliases("eval").help("'eval -h' for Additional Help");
		EARparser.setDefault("call", new EvaluateAlignResults());
		
		EARparser.addArgument("results_file").type(Arguments.fileType());//.verifyCanRead());
		EARparser.addArgument("references_file").type(Arguments.fileType());//.verifyCanRead());
		EARparser.addArgument("-filter_file", "-flt").metavar("FILE").type(Arguments.fileType());//.verifyCanRead());
		EARparser.addArgument("-output_fails_file", "-oaf").metavar("FILE").type(Arguments.fileType());//.verifyCanCreate());
		
		EARparser.addArgument("-map").type(Integer.class).nargs("*").metavar("@N");
		EARparser.addArgument("-pre").type(Integer.class).nargs("*").metavar("@N");
		EARparser.addArgument("-rec").type(Integer.class).nargs("*").metavar("@N");
		EARparser.addArgument("-top").type(Integer.class).nargs("*").metavar("@N");
		
		EARparser.addArgument("-verbose", "-v").action(Arguments.count());
		EARparser.addArgument("-results_file_delimiter", "-rsd").type(String.class).setDefault("\\|").metavar("EXPR");
		EARparser.addArgument("-references_file_delimiter", "-rfd").type(String.class).setDefault("\t").metavar("EXPR");
		/**
		 */
		
		try {
			Namespace res = MainParser.parseArgs(args);
			((CommandToExecute) res.get("call")).toExecute(res);
	    } catch (ArgumentParserException e) {
	    	MainParser.handleError(e);
	    }
	
	}
	
	
	private static interface CommandToExecute{
		void toExecute(Namespace ns);
	}
	
	private static class MineCoocMatrix implements CommandToExecute{
		@Override
		public void toExecute(Namespace ns) {
			// TODO Auto-generated method stub
			
		}
	}
	
	private static class BuildContextVectors implements CommandToExecute{
		@Override
		public void toExecute(Namespace ns) {
			// TODO Auto-generated method stub
			
		}
	}
	
	private static class TranslateContextVectors implements CommandToExecute{
		@Override
		public void toExecute(Namespace ns) {
			// TODO Auto-generated method stub
			
		}
	}
	
	private static class AlignContextVectors implements CommandToExecute{
		@Override
		public void toExecute(Namespace ns) {
			// TODO Auto-generated method stub
			
		}
	}
	
	private static class EvaluateAlignResults implements CommandToExecute{
		@Override
		public void toExecute(Namespace ns) {
			System.out.println(ns);
			
			QueryFile eval = null;
			
			try {
				if (ns.get("filter_file").equals(null)) {
					eval = new QueryFile(ns.get("results_file"), ns.get("references_file"));
				} else {
					eval = new QueryFile(ns.get("results_file"), ns.get("references_file"), ns.get("filter_file"));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//TODO
		}
	}
	
//	private static void EvaluateAlignResults(Namespace ns) throws IOException{
//		
//		QueryFile eval = null;
//		
//		switch (args.length){
//			case (0): System.out.println("Missing Arguments"); //TODO: improve error msg clarity
//				break;
//			case (1): System.out.println("Missing Arguments"); //TODO: improve error msg clarity
//				break;
//			case (2): eval = new QueryFile(args[0], args[1]);
//				break;
//			case (3): eval = new QueryFile(args[0], args[1], args[2]);
//				break;
//		}
//
//		DecimalFormat df = new DecimalFormat("#.###");
//		
//		System.out.println("MAP@20: "+df.format(eval.getMeanAveragePrecisionAtK(20)));
//		System.out.println("P@1: "+df.format(eval.getMeanPrecisionAtK(1)));
//		System.out.println("P@5: "+df.format(eval.getMeanPrecisionAtK(5)));
//		System.out.println("P@20: "+df.format(eval.getMeanPrecisionAtK(20)));
//		System.out.println("hP@1: "+df.format(eval.getTOPAtK(1)));
//		System.out.println("hP@5: "+df.format(eval.getTOPAtK(5)));
//		System.out.println("hP@20: "+df.format(eval.getTOPAtK(20)));
//		
//	}

}
