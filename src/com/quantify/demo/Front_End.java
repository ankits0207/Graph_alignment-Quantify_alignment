package com.quantify.demo;

import java.io.IOException;
import java.util.ArrayList;

import com.quantify.demo.Reader.MyPair;

public class Front_End {

	public static void main(String[] args) throws IOException {
		int k = 4;
		Topological_Similarity ts = new Topological_Similarity();
		
		Gexf_Parser gfp = new Gexf_Parser();
		Graph g1 = gfp.read_File("scere05.gexf");
		Graph g2 = gfp.read_File("scerehc.gexf");
		
		ArrayList<MyPair> al_of_mp = Reader.read("Final_Alignment.txt");
		double topo_score = ts.get_Topological_Score(al_of_mp, g1, g2, k);
		
		System.out.println(topo_score);
		
	}
}
