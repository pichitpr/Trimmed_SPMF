package spmf.extension.algorithm.seqgen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import spmf.extension.input.sequence_db_generic.SequenceDatabaseGen;
import spmf.extension.input.sequence_db_generic.SequenceGen;
import spmf.extension.patterns.itemset_list_generic.ItemsetGen;
import spmf.extension.prefixspan.JSPatternGen;


public class AlgoPrefixSpanJSGen<T extends Comparable<T>> {
	
	private List<JSPatternGen<T>> patterns;
	
	public List<JSPatternGen<T>> getResult(){
		return patterns;
	}
	
	public void prefixSpan(SequenceDatabaseGen<T> db, int minSup) throws IOException{
		Map<T, Set<Integer>> map = findSequencesContainingItems(db);
		List<PseudoSequenceGen<T>> initDB = new ArrayList<PseudoSequenceGen<T>>();
		for(SequenceGen<T> sequence : db.getSequences()){
			SequenceGen<T> optimizedSequence = sequence.cloneSequenceMinusItems(map, minSup);
			if(optimizedSequence.size() != 0){
				initDB.add(new PseudoSequenceGen<T>(optimizedSequence, 0, 0));
			}
		}
		SequentialPatternGen<T> prefix;
		List<PseudoSequenceGen<T>> projected1DB;
		SequentialPatternsGen<T> result;
		AlgoPrefixSpanGen<T> algoPrefixSpan = new AlgoPrefixSpanGen<T>();
		for(Entry<T, Set<Integer>> entry : map.entrySet()){
			if(entry.getValue().size() < minSup) continue;
			prefix = new SequentialPatternGen<T>();
			prefix.addItemset(new ItemsetGen<T>(entry.getKey()));
			System.out.println(prefix.toString()+entry.getValue());
			projected1DB = buildProjectedDatabaseForSingleItem(
					prefix.getIthItem(0), initDB, entry.getValue());
			result = algoPrefixSpan.runAlgorithmLength2(prefix, projected1DB, minSup);
			result.printFrequentPatterns(db.size(), true);
		}
	}
	
	/**
	 * Mine JS with prefixSpan. 
	 * Sequences of the same ID from left and right, and tag[ID] form <X|Y>(tag).
	 * The provided tag must cover all seqIDs in both side. Note that tag always
	 * starts from ID:0
	 * @throws IOException 
	 */
	public void prefixSpanJS(SequenceDatabaseGen<T> left, SequenceDatabaseGen<T> right, 
			int[] tag, int minSup) throws IOException{
		patterns = new ArrayList<JSPatternGen<T>>();
		
		//Occurence counting
		Map<T, Set<Integer>> mapLeft = findSequencesContainingItems(left);
		Map<T, Set<Integer>> mapRight = findSequencesContainingItems(right);
		Map<Integer, Set<Integer>> mapTag = new HashMap<Integer, Set<Integer>>();
		int tagNumber;
		Set<Integer> set;
		for(int i=0; i<tag.length; i++){
			tagNumber = tag[i];
			if(mapTag.containsKey(tagNumber)){
				set = mapTag.get(tagNumber);
			}else{
				set = new HashSet<Integer>();
			}
			set.add(i);
			mapTag.put(tagNumber, set);
		}
		//Also remove non-frequent tag
		Iterator<Entry<Integer, Set<Integer>>> iterator = mapTag.entrySet().iterator();
		while(iterator.hasNext()){
			if(iterator.next().getValue().size() < minSup){
				iterator.remove();
			}
		}
		
		//Setup initial database for both sides -- all non-frequent items are removed
		List<PseudoSequenceGen<T>> initDBLeft = new ArrayList<PseudoSequenceGen<T>>();
		for(SequenceGen<T> sequence : left.getSequences()){
			SequenceGen<T> optimizedSequence = sequence.cloneSequenceMinusItems(mapLeft, minSup);
			if(optimizedSequence.size() != 0){
				initDBLeft.add(new PseudoSequenceGen<T>(optimizedSequence, 0, 0));
			}
		}
		
		List<PseudoSequenceGen<T>> initDBRight = new ArrayList<PseudoSequenceGen<T>>();
		for(SequenceGen<T> sequence : right.getSequences()){
			SequenceGen<T> optimizedSequence = sequence.cloneSequenceMinusItems(mapRight, minSup);
			if(optimizedSequence.size() != 0){
				initDBRight.add(new PseudoSequenceGen<T>(optimizedSequence, 0, 0));
			}
		}
		
		//For all frequent length-1 JS (with tag) -- do separated PrefixSpan
		Set<Integer> seqIds;
		SequentialPatternGen<T> prefixLeft,prefixRight;
		JSPatternGen<T> rootPattern;
		
		for(Entry<T, Set<Integer>> entryLeft : mapLeft.entrySet()){
			prefixLeft = new SequentialPatternGen<T>();
			prefixLeft.addItemset(new ItemsetGen<T>(entryLeft.getKey()));
			
			for(Entry<T, Set<Integer>> entryRight : mapRight.entrySet()){
				set = new HashSet<Integer>(entryLeft.getValue());
				set.retainAll(entryRight.getValue());
				if(set.size() < minSup) continue; //left && right
				
				prefixRight = new SequentialPatternGen<T>();
				prefixRight.addItemset(new ItemsetGen<T>(entryRight.getKey()));
				
				for(Entry<Integer, Set<Integer>> entryTag : mapTag.entrySet()){
					seqIds = new HashSet<Integer>(set);
					seqIds.retainAll(entryTag.getValue());
					if(seqIds.size() < minSup) continue; //left && right && tag
					
					rootPattern = new JSPatternGen<T>(prefixLeft, prefixRight, 
							entryTag.getKey(), seqIds);
					patterns.add(rootPattern);
					
					//separated PrefixSpan recursions start here!!!
					runLength2PrefixSpanAndGetResult(
							rootPattern,
							prefixLeft, initDBLeft, 
							prefixRight, initDBRight, 
							seqIds, minSup);
				}
			}
		}
	}
	
	/**
	 * Run separated prefixSpan on left and right JSDatabase then find the frequent
	 * JSPattern using intersection on both side frequent patterns. Frequent JSPatterns
	 * are added to the field "patterns"
	 * @param prefixLeft The first single item prefix for left side
	 * @param initDBLeft The optimized initial database for left side
	 * @param prefixRight Same as prefixLeft but for right side
	 * @param initDBRight Same as initDBLeft but for right side
	 * @param seqIds Joined-Sequences (as IDs) that have 
	 * "&lt;prefixLeft|prefixRight&gt;(tag)" as prefix
	 * @param minSup Minimum support
	 * @throws IOException
	 */
	private void runLength2PrefixSpanAndGetResult(JSPatternGen<T> rootPattern,
			SequentialPatternGen<T> prefixLeft, List<PseudoSequenceGen<T>> initDBLeft,
			SequentialPatternGen<T> prefixRight, List<PseudoSequenceGen<T>> initDBRight,
			Set<Integer> seqIds, int minSup
			) throws IOException{
		List<PseudoSequenceGen<T>> projected1DB;
		SequentialPatternsGen<T> leftResult,rightResult;
		AlgoPrefixSpanGen<T> algoPrefixSpan = new AlgoPrefixSpanGen<T>();
		
		//Run separate prefixSpan , the separate result already include the length-1 prefix
		//(no more extra result processing!)
		projected1DB = buildProjectedDatabaseForSingleItem(
				prefixLeft.getIthItem(0), initDBLeft, seqIds);
		leftResult = algoPrefixSpan.runAlgorithmLength2(
				prefixLeft, projected1DB, minSup);
		
		projected1DB = buildProjectedDatabaseForSingleItem(
				prefixRight.getIthItem(0), initDBRight, seqIds);
		rightResult = algoPrefixSpan.runAlgorithmLength2(
				prefixRight, projected1DB, minSup);
		
		//One side pattern
		for(int leftLevel=0; leftLevel<leftResult.getLevelCount(); leftLevel++){
			for(SequentialPatternGen<T> leftPattern : leftResult.getLevel(leftLevel)){
				patterns.add(new JSPatternGen<T>(rootPattern, leftPattern, null, leftPattern.getSequenceIDs()));
			}
		}
		for(int rightLevel=0; rightLevel<rightResult.getLevelCount(); rightLevel++){
			for(SequentialPatternGen<T> rightPattern : rightResult.getLevel(rightLevel)){
				patterns.add(new JSPatternGen<T>(rootPattern, null, rightPattern, rightPattern.getSequenceIDs()));
			}
		}
		
		//Intersected pattern
		Set<Integer> seqs;
		for(int leftLevel=0; leftLevel<leftResult.getLevelCount(); leftLevel++){
			for(SequentialPatternGen<T> leftPattern : leftResult.getLevel(leftLevel)){
				for(int rightLevel=0; rightLevel<rightResult.getLevelCount(); rightLevel++){
					for(SequentialPatternGen<T> rightPattern : rightResult.getLevel(rightLevel)){
						seqs = new HashSet<Integer>(leftPattern.getSequenceIDs());
						seqs.retainAll(rightPattern.getSequenceIDs());
						if(seqs.size() < minSup) continue;
						
						patterns.add(new JSPatternGen<T>(rootPattern, leftPattern, rightPattern, seqs));
					}
				}
			}
		}
		
	}
	
	/*
	 * Methods below copied from AlgoPrefixSpan
	 */
	
	private Map<T, Set<Integer>> findSequencesContainingItems(SequenceDatabaseGen<T> database) {
		// We use a map to store the sequence IDs where an item appear
		// Key : item   Value :  a set of sequence IDs
		Map<T, Set<Integer>> mapSequenceID = new HashMap<T, Set<Integer>>(); 
		// for each sequence in the current database
		for(SequenceGen<T> sequence : database.getSequences()){
			// for each itemset in this sequence
			for(List<T> itemset : sequence.getItemsets()){
				// for each item
				for(T item : itemset){
					// get the set of sequence IDs for this item until now
					Set<Integer> sequenceIDs = mapSequenceID.get(item);
					if(sequenceIDs == null){
						// if the set does not exist, create one
						sequenceIDs = new HashSet<Integer>();
						mapSequenceID.put(item, sequenceIDs);
					}
					// add the sequence ID of the current sequence to the 
					// set of sequences IDs of this item
					sequenceIDs.add(sequence.getId());
				}
			}
		}
		return mapSequenceID;
	}
	
	private List<PseudoSequenceGen<T>> buildProjectedDatabaseForSingleItem(T item, List<PseudoSequenceGen<T>> initialDatabase,Set<Integer> sidSet) {
		// We create a new projected database
		List<PseudoSequenceGen<T>> sequenceDatabase = new ArrayList<PseudoSequenceGen<T>>();

		// for each sequence in the database received as parameter
		for(PseudoSequenceGen<T> sequence : initialDatabase){ 

			// if this sequence do not contain the current prefix, then skip it.
			if(!sidSet.contains(sequence.getId())){
				continue;
			}
			
			// for each itemset of the sequence
			for(int i = 0; i< sequence.size(); i++){

				// check if the itemset contains the item that is used for the projection
				int index = sequence.indexOfBis(i, item);
				// if it does not, and the current item is part of a suffix if inSuffix is true
				//   and vice-versa
				if(index == -1 ){
					continue;
				}
				
				// if the item is the last item of this itemset
				if(index == sequence.getSizeOfItemsetAt(i)-1){ 
					// if it is not the last itemset
					if ((i != sequence.size()-1)){
						// create new pseudo sequence
						// add it to the projected database.
						sequenceDatabase.add(new PseudoSequenceGen<T>( sequence, i+1, 0));
					}
				}else{
					// create a new pseudo sequence and
					// add it to the projected database.
					sequenceDatabase.add(new PseudoSequenceGen<T>(sequence, i, index+1));
				}

			}
		}
		return sequenceDatabase; // return the projected database
	}
}
