package org.rali.ljak.ecva;

import java.io.IOException;
import java.text.DecimalFormat;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
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
		
		ArgumentParser MAINparser = ArgumentParsers.newArgumentParser("eCVa-Toolkit");
		MAINparser.description("explicit Context Vector analysis - Toolkit");
		
		Subparsers spManager = MAINparser.addSubparsers();
			
		
		/**
		 * 
		 * EvaluateAlignResults, EAR
		 * results_file
		 * --results_file_delimiter, --rsd ; default = "\\|" ; string
		 * reference_file
		 * --reference_file_delimiter, --rfd ; default = "\t" ; string
		 * --filter_file, --flt
		 * --fails_file, --flf
		 * --fails_file_delimiter, --ffd ; default = "\t" ; string ?
		 * --map N (liste); default : map 20
		 * --pre N (liste); default : pre 20
		 * --rec N (liste); default : rec 20
		 * --top N (liste); default : top 20
		 * --verbose, -v, -vv (debug?)
		 */
		Subparser EARparser = spManager.addParser("EvaluateAlignResults").aliases("EAR");
		EARparser.addArgument("results_file").type(Arguments.fileType());
		EARparser.addArgument("reference_file").type(Arguments.fileType());
		EARparser.addArgument("--filter_file", "--flt").type(Arguments.fileType());
		EARparser.addArgument("--fails_file", "--flf").type(Arguments.fileType());
		
		EARparser.addArgument("--results_file_delimiter", "--rsd").type(String.class).setDefault("\\|");
		EARparser.addArgument("--reference_file_delimiter", "--rfd").type(String.class).setDefault("\t");
		
		EARparser.addArgument("--map").type(Integer.class).nargs("*").setDefault(20);
		EARparser.addArgument("--pre").type(Integer.class).nargs("*").setDefault(20);
		EARparser.addArgument("--rec").type(Integer.class).nargs("*").setDefault(20);
		EARparser.addArgument("--top").type(Integer.class).nargs("*").setDefault(20);
		
		EARparser.addArgument("--verbose", "-v,").action(Arguments.storeTrue());
			
		
		try {
	        System.out.println(MAINparser.parseArgs(args));
	    } catch (ArgumentParserException e) {
	    	MAINparser.handleError(e);
	    }
			
			
			
			
			
			
			

			
		
		
			
			
			
			
			
			
			
			
//		if (args[0].equalsIgnoreCase("-eval")){
//			Evaluation(Arrays.copyOfRange(args, 1, args.length));
//		} else {
//			System.err.println("Bad Arguments"); //TODO: improve error msg clarity
//		}	
	}
	
	
	
	
	
	
	
	
	
	
	
	private static void Evaluation(String[] args) throws IOException{
		
		QueryFile eval = null;
		
		switch (args.length){
			case (0): System.out.println("Missing Arguments"); //TODO: improve error msg clarity
				break;
			case (1): System.out.println("Missing Arguments"); //TODO: improve error msg clarity
				break;
			case (2): eval = new QueryFile(args[0], args[1]);
				break;
			case (3): eval = new QueryFile(args[0], args[1], args[2]);
				break;
		}

		DecimalFormat df = new DecimalFormat("#.###");
		
		System.out.println("MAP@20: "+df.format(eval.getMeanAveragePrecisionAtK(20)));
		System.out.println("P@1: "+df.format(eval.getMeanPrecisionAtK(1)));
		System.out.println("P@5: "+df.format(eval.getMeanPrecisionAtK(5)));
		System.out.println("P@20: "+df.format(eval.getMeanPrecisionAtK(20)));
		System.out.println("hP@1: "+df.format(eval.getTOPAtK(1)));
		System.out.println("hP@5: "+df.format(eval.getTOPAtK(5)));
		System.out.println("hP@20: "+df.format(eval.getTOPAtK(20)));
		
	}

}
