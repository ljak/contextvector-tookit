package org.rali.ljak.ecva;

import java.io.IOException;
import java.text.DecimalFormat;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

import org.rali.ljak.ecva.evaluation.QueryFile;

public class Ecva {

	/**
	 * Main class of the eCVoL-Toolkit.
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
		Subparser MCMparser = subParserManager.addParser("MineCoocMatrix").aliases("mine").help("-h for Additional Help");
		MCMparser.setDefault("call", new MineCoocMatrix());
		
		/**
		 */
		
		/**
		 * BuildContextVectors Command and Arguments Definitions
		 */
		Subparser BCVparser = subParserManager.addParser("BuildContextVectors").aliases("build").help("-h for Additional Help");
		BCVparser.setDefault("call", new BuildContextVectors());
		
		/**
		 */
		
		/**
		 * TranslateContextVectors Command and Arguments Definitions
		 */
		Subparser TCVparser = subParserManager.addParser("TranslateContextVectors").aliases("trans").help("-h for Additional Help");
		TCVparser.setDefault("call", new TranslateContextVectors());
		
		/**
		 */
		
		/**
		 * AlignContextVectors Command and Arguments Definitions
		 */
		Subparser ACRparser = subParserManager.addParser("AlignContextVectors").aliases("align").help("-h for Additional Help");
		ACRparser.setDefault("call", new AlignContextVectors());
		
		/**
		 */
		
		/**
		 * EvaluateAlignResults Command and Arguments Definitions
		 */
		Subparser EARparser = subParserManager.addParser("EvaluateAlignResults").aliases("eval").help("-h for Additional Help");
		EARparser.setDefault("call", new EvaluateAlignResults());
		
		EARparser.addArgument("results_file").type(Arguments.fileType());//.verifyCanRead());
		EARparser.addArgument("references_file").type(Arguments.fileType());//.verifyCanRead());
		EARparser.addArgument("-filter_file", "-flt").type(Arguments.fileType());//.verifyCanRead());
		EARparser.addArgument("-output_fails_file", "-oaf").type(Arguments.fileType());//.verifyCanCreate());
		
		EARparser.addArgument("-map").type(Integer.class).nargs("*").setDefault(20);
		EARparser.addArgument("-pre").type(Integer.class).nargs("*").setDefault(20);
		EARparser.addArgument("-rec").type(Integer.class).nargs("*").setDefault(20);
		EARparser.addArgument("-top").type(Integer.class).nargs("*").setDefault(20);
		
		EARparser.addArgument("-verbose", "-v", "-vv").action(Arguments.storeTrue());
		EARparser.addArgument("-results_file_delimiter", "-rsd").type(String.class).setDefault("\\|");
		EARparser.addArgument("-references_file_delimiter", "-rfd").type(String.class).setDefault("\t");
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
			// TODO Auto-generated method stub
			System.out.println(ns);
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
