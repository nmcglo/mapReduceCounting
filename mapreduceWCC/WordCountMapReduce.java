package mapreduce;

import java.util.Vector;
import java.util.HashSet;
import java.util.Set;

public class WordCountMapReduce implements MapReduce {

	private int nMappers;
	private int nShufflers;
	private int nReducers;
	
	public WordCountMapReduce(int m, int s, int r) {
		nMappers = m;
		nShufflers = s;
		nReducers = r;
	}
	
	public int nMappers() {
		return nMappers;
	}

	public int nReducers() {
		return nReducers;
	}

	public int nShufflers() {
		return nShufflers;
	}

	public Vector map(Pair p) {
		Vector ret = new Vector();
		String s = (String)p.value;
		String[] words = s.split("\\s+");

		for (int i = 0; i < words.length; ++i)
		{
			ret.add(new Pair(words[i], new Pair(p.key,1)));
		}
		return ret;
	}

	public Pair reduce(Pair p) {
		String word = (String) p.key;
		Vector v = (Vector)p.value;

		Vector reducedVector = new Vector();

		Set vecSet = new HashSet(v);
		for(Object item : vecSet)
		{
			Pair thePair = (Pair) item;
			int count = 0;
			for(int i = 0; i < v.size(); i++)
			{	
				if(thePair.equals((Pair) v.get(i)))
					count += 1;
			}

			reducedVector.add(new Pair(thePair.key,count));

		}
		Pair ret = new Pair(word, reducedVector);

		return new Pair(word,reducedVector);
		// int sum = 0;
		// for (int i = 0; i < v.size(); ++i) sum += (Integer)v.get(i);
		// Vector ret = new Vector();
		// ret.add(sum);
		// return new Pair(p.key, ret);
	}

}

