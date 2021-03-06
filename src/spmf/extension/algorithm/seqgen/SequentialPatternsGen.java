package spmf.extension.algorithm.seqgen;

import java.util.ArrayList;
import java.util.List;

/**
 * A set of sequential patterns grouped by their size (how many items they have).
 *
 * Copyright (c) 2008-2013 Philippe Fournier-Viger
 * 
 * This file is part of the SPMF DATA MINING SOFTWARE
 * (http://www.philippe-fournier-viger.com/spmf).
 *
 * SPMF is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SPMF is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SPMF.  If not, see <http://www.gnu.org/licenses/>.
 */
public class SequentialPatternsGen<T extends Comparable<T>> {
	
	// A list of list is used to stored the sequential patterns.
	// At position i, a list of sequential patterns contains
	// all sequential patterns of size i.
	public final List<List<SequentialPatternGen<T>>> levels = new ArrayList<List<SequentialPatternGen<T>>>();  // itemset class� par taille
	// the total number of sequential patterns
	public int sequenceCount=0;
	
	// a name that is given to this set of sequential patterns
	private final String name;
	
	/**
	 * Constructor
	 * @param name a name to give to this set of patterns
	 */
	public SequentialPatternsGen(String name){
		this.name = name; // save the name
		// initialize the list of list with an empty list
		// for patterns of size 0.
		levels.add(new ArrayList<SequentialPatternGen<T>>()); 
	}
	
	/**
	 * Print all sequential patterns to System.out.
	 * @param nbObject the size of the original database in terms of sequences.
	 * @param showSequenceIdentifiers  if true, sequence identifiers will be output for each pattern
	 */
	public void printFrequentPatterns(int nbObject, boolean showSequenceIdentifiers){
		System.out.println(toString(nbObject,showSequenceIdentifiers));
	}
	
	/**
	 * Get a string representation of this set of sequential patterns.
	 * @param nbObject  the number of sequences in the database where these patterns were found.
	 * @param showSequenceIdentifiers  if true, sequence identifiers will be output for each pattern
	 * @return a string
	 */
	public String toString(int nbObject, boolean showSequenceIdentifiers){
		StringBuilder r = new StringBuilder(200);
		r.append(" ----------");
		r.append(name);
		r.append(" -------\n");
		int levelCount=0;
		int patternCount =0;
		for(List<SequentialPatternGen<T>> level : levels){
			r.append("  L");
			r.append(levelCount);
			r.append(" \n");
			for(SequentialPatternGen<T> sequence : level){
				patternCount++;
				r.append("  pattern ");
				r.append(patternCount);
				r.append(":  ");
				r.append(sequence.toString());
				r.append("support :  ");
				r.append(sequence.getRelativeSupportFormated(nbObject));
				r.append(" (" );
				r.append(sequence.getAbsoluteSupport());
				r.append('/');
				r.append(nbObject);
				r.append(")" );
				if(showSequenceIdentifiers) {
		        	r.append(" sequence ids: ");
		        	for (Integer sid: sequence.getSequenceIDs()) {
		        		r.append(sid);
		        		r.append(" ");
		        	}
				}
				
				r.append("\n");
			}
			levelCount++;
		}
		r.append(" -------------------------------- Patterns count : ");
		r.append(sequenceCount);
		return r.toString();
	}
	
	/**
	 * Add a sequential pattern to this set of sequential patterns.
	 * @param sequence the sequential pattern
	 * @param k the size of the sequential pattern in temrs of itemset.
	 */
	public void addSequence(SequentialPatternGen<T> sequence, int k){
		while(levels.size() <= k){
			levels.add(new ArrayList<SequentialPatternGen<T>>());
		}
		levels.get(k).add(sequence);
		sequenceCount++;
	}
	
	/**
	 * Get all the sequential patterns of a given size.
	 * @param index the size in terms of items.
	 * @return a list of sequential patterns.
	 */
	public List<SequentialPatternGen<T>> getLevel(int index){
		return levels.get(index);
	}
	
	/**
	 * Get the maximum size of sequential patterns + 1.
	 * @return the maximum size.
	 */
	public int getLevelCount(){
		return levels.size();
	}

	/**
	 * Get a list of list of sequential patterns such that
	 * at position i, there is a list of sequential patterns
	 * containing i items.
	 * @return
	 */
	public List<List<SequentialPatternGen<T>>> getLevels() {
		return levels;
	}
}
