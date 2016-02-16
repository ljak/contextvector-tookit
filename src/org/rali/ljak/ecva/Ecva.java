package org.rali.ljak.ecva;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.rali.ljak.ecva.align.SimilarityMeasures;
import org.rali.ljak.ecva.build.AssociationMeasures;
import org.rali.ljak.ecva.eval.PostProcessing;
import org.rali.ljak.ecva.eval.QueryFile;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

public class Ecva {
	
	public static final Logger logger = LogManager.getLogger();

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
		 * 
		 * MAIN COMMANDS
		 * 
		 **/
		
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
		EARparser.addArgument("-filter_file", "-filt").metavar("FILE").type(Arguments.fileType());//.verifyCanRead());
		
		EARparser.addArgument("-output_fails_in_file_at", "-fail").type(Integer.class).nargs("*").metavar("@N"); //.metavar("FILE").type(Arguments.fileType());//.verifyCanCreate());
		EARparser.addArgument("-output_success_in_file_at", "-succ").type(Integer.class).nargs("*").metavar("@N"); //.metavar("FILE").type(Arguments.fileType());//.verifyCanCreate());
		
		EARparser.addArgument("-map").type(Integer.class).nargs("*").metavar("@N");
		EARparser.addArgument("-pre").type(Integer.class).nargs("*").metavar("@N");
		EARparser.addArgument("-rec").type(Integer.class).nargs("*").metavar("@N");
		EARparser.addArgument("-top").type(Integer.class).nargs("*").metavar("@N");
		
		EARparser.addArgument("-verbose", "-v").type(Integer.class).action(Arguments.count());
		EARparser.addArgument("-results_file_delimiter", "-rsd").type(String.class).setDefault("\\|").metavar("EXPR");
		EARparser.addArgument("-references_file_delimiter", "-rfd").type(String.class).setDefault("\t").metavar("EXPR");
		/**
		 */
		
		/**
		 * 
		 * TOOLS COMMANDS
		 * 
		 **/
		
		/**
		 * PostProcessing.extractCandidatesCountInResults (pp.count) Command and Arguments Definitions
		 */
		Subparser ECCIRparser = subParserManager.addParser("PostProcessing.extractCandidatesCountInResults").aliases("pp.count").help("'eval -h' for Additional Help");
		ECCIRparser.setDefault("call", new extractCandidatesCountInResults());
		
		ECCIRparser.addArgument("results_file").type(Arguments.fileType());
		
		
		/**
		 * PostProcessing.removeCandidatesInResultsAccordingList (pp.rem) Command and Arguments Definitions
		 */
		Subparser RCIRALparser = subParserManager.addParser("PostProcessing.removeCandidatesInResultsAccordingList").aliases("pp.rem").help("'eval -h' for Additional Help");
		RCIRALparser.setDefault("call", new removeCandidatesInResultsAccordingList());
		
		RCIRALparser.addArgument("results_file").type(Arguments.fileType());//.verifyCanRead());
		RCIRALparser.addArgument("cand_list_file").type(Arguments.fileType());//.verifyCanRead());
		
		
		/**
		 ** END
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
			//System.out.println(ns);
			
			if (ns.getString("verbose") != null) {
				if (ns.getInt("verbose") == 1) setLevelLogger(Level.INFO);
				if (ns.getInt("verbose") == 2) setLevelLogger(Level.TRACE);
				if (ns.getInt("verbose") == 3) setLevelLogger(Level.DEBUG);
			}
			
			QueryFile eval = null;
			
			try {
				if (ns.getString("filter_file") == null) {
					eval = new QueryFile(ns.getString("results_file"), ns.getString("references_file"));
				} else {
					eval = new QueryFile(ns.getString("results_file"), ns.getString("references_file"), ns.getString("filter_file"));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (String m : java.util.Arrays.asList("top","map","pre","rec")){
				if (ns.getList(m) != null){
					for (Object i : new ArrayList<Object>(ns.getList(m))){
						if (m.equalsIgnoreCase("top")) System.out.println("Top@"+i+": "+eval.getTOPAtK((int)i));
						if (m.equalsIgnoreCase("map")) System.out.println("Map@"+i+": "+eval.getMeanAveragePrecisionAtK((int)i));
						if (m.equalsIgnoreCase("pre")) System.out.println("Pre@"+i+": "+eval.getMeanPrecisionAtK((int)i));
						if (m.equalsIgnoreCase("rec")) System.out.println("Rec@"+i+": "+eval.getMeanRecallAtK((int)i));	
					}
				}
			}
			
			try {
				for (String o : java.util.Arrays.asList("output_fails_in_file_at","output_success_in_file_at")){
					if (ns.getList(o) != null){
						String extension;
						if (o.equals("output_fails_in_file_at")) {extension = ".failAt";} else {extension = ".succAt";}
						for (Object i : new ArrayList<Object>(ns.getList(o))){
							FileWriter writer = new FileWriter(ns.getString("results_file")+extension+String.valueOf((int)i)); // TODO: what if is a relative path ?
							if (o.equalsIgnoreCase("output_fails_in_file_at")){
								for (String str: eval.getFailsAtK((int)i)) {
									writer.write(str);
									writer.write("\n");
								}
							} else {
								for (String str: eval.getSuccessAtK((int)i)) {
									writer.write(str);
									writer.write("\n");
								}
							}
							writer.close();
						}
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	private static class extractCandidatesCountInResults implements CommandToExecute{
		@Override
		public void toExecute(Namespace ns) {
			
			try {
				PostProcessing.extractCandidatesCountInResults(ns.getString("results_file"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	private static class removeCandidatesInResultsAccordingList implements CommandToExecute{
		@Override
		public void toExecute(Namespace ns) {
			
			try {
				PostProcessing.removeCandidatesInResultsAccordingList(ns.getString("results_file"), ns.getString("cand_list_file"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	private static void setLevelLogger(Level l){
		
		LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		Configuration config = ctx.getConfiguration();
		LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME); //LogManager.getLogger(Ecva.class).getName()
		loggerConfig.setLevel(l);
		ctx.updateLoggers();  // This causes all Loggers to refetch information from their LoggerConfig.
		
	}

}
