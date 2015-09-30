package spmf.extension.prefixspan;

import java.util.ArrayList;
import java.util.List;

import spmf.extension.input.sequence_db_generic.SequenceDatabaseGen;
import spmf.extension.input.sequence_db_generic.SequenceGen;
import ca.pfv.spmf.input.sequence_database_list_integers.Sequence;
import ca.pfv.spmf.input.sequence_database_list_integers.SequenceDatabase;

public class Utility {
	
	public static SequenceDatabase load(String strDB){
		SequenceDatabase db = new SequenceDatabase();
		Sequence seq;
		List<Integer> iset;
		String[] sequences = strDB.split("\\n");
		String[] itemsets;
		String[] items;
		for(String seqStr : sequences){
			itemsets = seqStr.trim().split("\\s*\\|\\s*");
			seq = new Sequence(Integer.valueOf(itemsets[0]));
			for(int i=1; i<itemsets.length; i++){
				items = itemsets[i].split("\\s+");
				iset = new ArrayList<Integer>();
				for(String itemStr : items){
					iset.add(Integer.valueOf(itemStr));
				}
				seq.addItemset(iset);
			}
			db.addSequence(seq);
		}
		return db;
	}
	
	public static SequenceDatabase load(List<List<Integer>>[] aryListDB){
		SequenceDatabase db = new SequenceDatabase();
		Sequence seq;
		for(int i=0; i<aryListDB.length; i++){
			if(aryListDB[i] != null){
				seq = new Sequence(i);
				for(List<Integer> itemset : aryListDB[i]){
					seq.addItemset(itemset);
				}
				db.addSequence(seq);
			}
		}
		return db;
	}
	
	//===============================================
	
	public static <T extends Comparable<T>> SequenceDatabaseGen<T> loadGen(String strDB,
			StringParsable<T> parser){
		SequenceDatabaseGen<T> db = new SequenceDatabaseGen<T>();
		SequenceGen<T> seq;
		List<T> iset;
		String[] sequences = strDB.split("\\n");
		String[] itemsets;
		String[] items;
		for(String seqStr : sequences){
			itemsets = seqStr.trim().split("\\s*\\|\\s*");
			seq = new SequenceGen<T>(Integer.valueOf(itemsets[0]));
			for(int i=1; i<itemsets.length; i++){
				items = itemsets[i].split("\\s+");
				iset = new ArrayList<T>();
				for(String itemStr : items){
					iset.add(parser.parse(itemStr));
				}
				seq.addItemset(iset);
			}
			db.addSequence(seq);
		}
		return db;
	}
	
	public static <T extends Comparable<T>> SequenceDatabaseGen<T> loadGen(
			List<List<T>>[] aryListDB){
		SequenceDatabaseGen<T> db = new SequenceDatabaseGen<T>();
		SequenceGen<T> seq;
		for(int i=0; i<aryListDB.length; i++){
			if(aryListDB[i] != null){
				seq = new SequenceGen<T>(i);
				for(List<T> itemset : aryListDB[i]){
					seq.addItemset(itemset);
				}
				db.addSequence(seq);
			}
		}
		return db;
	}
}
