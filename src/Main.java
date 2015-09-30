


public class Main {

	/*
	public static void main(String[] args){
		SequenceDatabaseGen<String> db = new SequenceDatabaseGen<String>();
		AlgoPrefixSpanGen<String> ps = new AlgoPrefixSpanGen<String>();
		try {
			db.loadFile("data2.txt", new StringParsable<String>() {
				@Override
				public String parse(String s) {
					return s;
				}
			});
			ps.runAlgorithm(db, 0.6, "data2out_gen.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/
	
	/*
	public static void main(String[] args){
		StringParsable<String> parser = new StringParsable<String>() {
			@Override
			public String parse(String s) {
				return s;
			}
		};
		String str = "0 | 1 2 | 2 3 | 1 | 1 \n"
				+ "1 | 1 2 | 1 | 1 \n"
				+ "2 | 1 2 | 4 | 2 3 | 1 \n"
				+ "3 | 1 2 | 5 | 3 | 1 \n"
				+ "4 | 1 2 \n";
		String str2 = "0 | 1 3 | 1 \n"
				+ "1 | 2 | 1 \n"
				+ "2 | 2 | 1 | 1 \n"
				+ "3 | 3 | 1 \n"
				+ "4 | 1 | 2 | 1 \n";
		SequenceDatabaseGen<String> db = Utility.loadGen(str,parser);
		SequenceDatabaseGen<String> db2 = Utility.loadGen(str2,parser);
		int[] tag = new int[]{0,0,0,0,0};
		AlgoPrefixSpanJSGen<String> jsp = new AlgoPrefixSpanJSGen<String>();
		try {
			jsp.prefixSpan(db, 3);
			System.out.println("============================================OOOOOOO");
			jsp.prefixSpan(db2, 3);
			System.out.println("============================================OOOOOOO");
			jsp.prefixSpanJS(db, db2, tag, 3);
			for(JSPatternGen<String> pattern : jsp.getResult()){
				System.out.println(pattern);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/
	
	/*
	public static void main(String[] args){		
		String str = "0 | 1 2 | 2 3 | 1 | 1 \n"
				+ "1 | 1 2 | 1 | 1 \n"
				+ "2 | 1 2 | 4 | 2 3 | 1 \n"
				+ "3 | 1 2 | 5 | 3 | 1 \n"
				+ "4 | 1 2 \n";
		String str2 = "0 | 1 3 | 1 \n"
				+ "1 | 2 | 1 \n"
				+ "2 | 2 | 1 | 1 \n"
				+ "3 | 3 | 1 \n"
				+ "4 | 1 | 2 | 1 \n";
		SequenceDatabase db = Utility.load(str);
		SequenceDatabase db2 = Utility.load(str2);
		int[] tag = new int[]{0,0,0,0,0};
		AlgoPrefixSpanJS jsp = new AlgoPrefixSpanJS();
		try {
			jsp.prefixSpan(db, 3);
			System.out.println("============================================OOOOOOO");
			jsp.prefixSpan(db2, 3);
			System.out.println("============================================OOOOOOO");
			jsp.prefixSpanJS(db, db2, tag, 3);
			for(JSPattern pattern : jsp.getResult()){
				System.out.println(pattern);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/
	
}
