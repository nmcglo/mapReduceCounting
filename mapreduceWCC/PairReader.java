package mapreduce;

import java.io.BufferedReader;
import java.io.FileReader;

public class PairReader {
	BufferedReader br = null;

	public PairReader(String inputFilename) {
		try {
			br = new BufferedReader(new FileReader(inputFilename));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Pair readPair() {
		try {
			String line = br.readLine();
			if (line == null)
				return null;
			String[] keyAndValue = line.split("\t");
			for (int i = 2; i < keyAndValue.length; ++i) keyAndValue[1] += "\t"+keyAndValue[i];
			return new Pair(keyAndValue[0], keyAndValue[1]);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void close() {
		try {
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		PairReader pr = new PairReader("exampleInput.txt");
		Pair p;
		while ((p = pr.readPair()) != null)
			System.out.println("(" + p.key + "," + p.value + ")");
	}

}
