package spmf.extension.prefixspan;

import java.util.Set;

import spmf.extension.algorithm.seqgen.SequentialPatternGen;


public class JSPatternGen<T extends Comparable<T>> {

	/* 
	 * Only make reference to the actual sequence in the memory. 
	 * Multiple reference can be made (multiple JSPatterns may refer to the same sequence)
	 * for memory efficiency. So, DO NOT modify the sequence
	 */
	private SequentialPatternGen<T> left,right;
	private int tag;
	private Set<Integer> sequencesIds;
	
	public JSPatternGen(SequentialPatternGen<T> seqLeft, SequentialPatternGen<T> seqRight, int tag,
			Set<Integer> seqIds){
		left = seqLeft;
		right = seqRight;
		this.tag = tag;
		sequencesIds = seqIds;
	}
	
	/**
	 * Used to create length-2 or more patterns from separate prefixSpan run.
	 * seqLeft and seqRight are the result from the run. Can be null if only one side
	 * will be appended
	 */
	public JSPatternGen(JSPatternGen<T> rootPattern, SequentialPatternGen<T> seqLeft, 
			SequentialPatternGen<T> seqRight, Set<Integer> seqIds){
		tag = rootPattern.tag;
		sequencesIds = seqIds;
		
		if(seqLeft != null){
			left = seqLeft;
		}else{
			left = rootPattern.left.cloneSequence();
		}
		
		if(seqRight != null){
			right = seqRight;
		}else{
			right = rootPattern.right.cloneSequence();
		}
	}
	
	/**
	 * Return left side sequence. DO NOT modify it
	 */
	public SequentialPatternGen<T> getLeftSide(){
		return left;
	}
	
	/**
	 * Return right side sequence. DO NOT modify it
	 */
	public SequentialPatternGen<T> getRightSide(){
		return right;
	}
	
	public Set<Integer> getSequenceIds(){
		return sequencesIds;
	}
	
	public int getTag(){
		return tag;
	}
	
	@Override
	public String toString(){
		StringBuilder s = new StringBuilder("< ");
		if(left != null){
			s.append(left.toString().trim());
		}
		s.append(" | ");
		if(right != null){
			s.append(right.toString().trim());
		}
		s.append(" > (");
		s.append(tag);
		s.append(") seqIds: ");
		for(int i : sequencesIds){
			s.append(i);
			s.append(" ");
		}
		return s.toString().trim();
	}
}
