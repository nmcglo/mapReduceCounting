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
		System.out.println("Received Map Command");
		Vector ret = new Vector();
		String s = (String)p.value;
		String[] words = s.split("\\s+"); //regular expressions to split the string into a list of words

		for (int i = 0; i < words.length; ++i)
		{
			ret.add(new Pair(words[i], new Pair(p.key,1))); //add a pair of (word, (doc, 1))
		}
		System.out.println("Returning Mapped Pair");
		return ret;
	}

	public Pair reduce(Pair p) {
		System.out.println("Received Reduce Command");
		String word = (String) p.key;
		Vector v = (Vector)p.value;

		Vector reducedVector = new Vector();

		Set vecSet = new HashSet(v);
		//For each item in the set of docfreq pairs, count how many of each pair exist, that is how many times
		//the word was mentioned in each document
		for(Object item : vecSet)
		{
			Pair thePair = (Pair) item;
			int count = 0;
			for(int i = 0; i < v.size(); i++)
			{	
				if(thePair.equals((Pair) v.get(i)))
					count += 1;
			}

			reducedVector.add(new Pair(thePair.key,count)); //Add pair format to return

		}
		Pair ret = new Pair(word, reducedVector);
		System.out.println("Received Reduced Pair");
		return new Pair(word,reducedVector); //Add final pair format to return

	}

}

