package org.rali.ljak.ecva.align;

public enum SimilarityMeasures {

	COS {
		@Override
		public String getName() {
			return "Cosine";
		}

		@Override
		public String getAcronyme() {
			return "COS";
		}

		@Override
		public double compute() {
			return 0;
		}
	};
	
	/**
	 * Contract Template for all Similarity Measure.
	 */
	public abstract String getName();
	public abstract String getAcronyme();
	public abstract double compute();
    
}
