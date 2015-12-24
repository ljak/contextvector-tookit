package org.rali.ljak.ecva.build;

public enum AssociationMeasures {
	
	LLR {
		@Override
		public String getName() {
			return "Log Likelihood Ratio";
		}

		@Override
		public String getAcronyme() {
			return "LLR";
		}
		
		@Override 
		public double compute() { 
			return 0;
		}
	},
	
	ORD {
		@Override
		public String getName() {
			return "Odd Ratio Discontinu";
		}

		@Override
		public String getAcronyme() {
			return "ORD";
		}
		
		@Override
		public double compute() {
			return 0;
		}
	},
	
	PMI {
		@Override
		public String getName() {
			return "Pointwise Mutual Information";
		}

		@Override
		public String getAcronyme() {
			return "PMI";
		}
		
		@Override
		public double compute() {
			return 0;
		}
	};
	
	/**
	 * Contract Template for all Association Measure.
	 */
	public abstract String getName();
	public abstract String getAcronyme();
    public abstract double compute();

}
