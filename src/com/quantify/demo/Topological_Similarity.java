package com.quantify.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.jblas.ComplexDouble;
import org.jblas.ComplexDoubleMatrix;
import org.jblas.DoubleMatrix;
import org.jblas.Eigen;

import com.quantify.demo.Reader.MyPair;

public class Topological_Similarity {
	
	public double get_Topological_Score(ArrayList<MyPair> al_of_mp, Graph g1, Graph g2, int k)
	{
		double score = 0.0;
		String node1, node2;
		for(MyPair mp : al_of_mp)
		{
			node1 = mp.n1;
			node2 = mp.n2;
			score += get_k_hop_topo_dist(g1, g2, get_node_obj(g1, node1), get_node_obj(g2, node2), k);
		}		
		return score;		
	}
	
	public double get_k_hop_topo_dist(Graph g1, Graph g2, MyNode n1, MyNode n2, int k)
	{
		double d_topo = 0.0;		
		for(int i = 1; i <= k; i++)
		{
			Graph g_one = get_k_hop_neighbourhood(g1, n1, i);
			Graph g_two = get_k_hop_neighbourhood(g2, n2, i);
			HashMap<String, Integer> hash_map_1;
			HashMap<String, Integer> hash_map_2;
			hash_map_1 = get_HashMap(g_one);
			hash_map_2 = get_HashMap(g_two);
			d_topo += get_topological_distance(g_one, g_two, hash_map_1, hash_map_2);
		}
		return d_topo;
	}
	
	public Graph get_k_hop_neighbourhood(Graph g, MyNode seed, int k){
		Graph g_out_k_hop = new Graph();
		g_out_k_hop.al_of_nodes = new ArrayList<MyNode>();
		g_out_k_hop.al_of_edges = new ArrayList<MyEdge>();
		
		ArrayList<Graph> al_of_graph = new ArrayList<Graph>();
		ArrayList<MyNode> al_of_nodes = new ArrayList<MyNode>();
		Graph returned_graph = null;
		al_of_nodes.add(seed);
		
		HashSet<MyNode> hs_node_1 = new HashSet<MyNode>();		
		
		for(int hop_counter = 1; hop_counter <= k; hop_counter++){
			for(MyNode mn1 : al_of_nodes)
			{
				returned_graph = get_1_hop_neighbourhood(g, mn1);
				al_of_graph.add(returned_graph);
			}
			for(Graph graph : al_of_graph)
			{
				al_of_nodes.addAll(graph.al_of_nodes);
			}
			hs_node_1.addAll(al_of_nodes);
			al_of_nodes.clear();
			al_of_nodes.addAll(hs_node_1);			
		}
		for(Graph graph : al_of_graph)
		{
			for(MyNode mn : graph.al_of_nodes)
			{
				g_out_k_hop.al_of_nodes.add(mn);
			}
			for(MyEdge me : graph.al_of_edges)
			{
				g_out_k_hop.al_of_edges.add(me);
			}
		}
		HashSet<MyNode> hs_node2 = new HashSet<MyNode>();
		HashSet<MyEdge> hs_edge = new HashSet<MyEdge>();
		
		hs_node2.addAll(g_out_k_hop.al_of_nodes);
		hs_edge.addAll(g_out_k_hop.al_of_edges);
		
		g_out_k_hop.al_of_nodes.clear();
		g_out_k_hop.al_of_edges.clear();
		
		g_out_k_hop.al_of_nodes.addAll(hs_node2);
		g_out_k_hop.al_of_edges.addAll(hs_edge);
		
		return g_out_k_hop;
	}

	public HashMap<String, Integer> get_HashMap(Graph g){
		int i = 0;
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		for(MyNode mn : g.al_of_nodes){
			hm.put(mn.Id, i);
			i++;			
		}
		return hm;		
	}
	
	public Graph get_1_hop_neighbourhood(Graph g_in, MyNode seed){
		Graph g_out = new Graph();
		g_out.al_of_nodes = new ArrayList<MyNode>();
		g_out.al_of_edges = new ArrayList<MyEdge>();
		g_out.al_of_nodes.add(seed);
		for(MyEdge me : g_in.al_of_edges)
		{
			if(me.source.equals(seed.Id))
			{
				g_out.al_of_nodes.add(get_node_obj(g_in, me.target));
				g_out.al_of_edges.add(me);
			}
			else if(me.target.equals(seed.Id))
			{
				g_out.al_of_nodes.add(get_node_obj(g_in, me.source));
				g_out.al_of_edges.add(me);
			}
		}
		return g_out;
	}
	
	public MyNode get_node_obj(Graph g, String s){
		for(MyNode mn : g.al_of_nodes)
		{
			if(mn.Id.equals(s))
				return mn;
		}
		return null;
	}
	
	public double get_topological_distance(Graph g1, Graph g2, HashMap<String, Integer> hm1, HashMap<String, Integer> hm2){
		Get_Matrices gm = new Get_Matrices();
		
		int[][] adj_matrix_1 = gm.get_adjacency_matrix(g1, hm1);
		int[][] adj_matrix_2 = gm.get_adjacency_matrix(g2, hm2);
		
		double[][] normalized_laplacian_1 = gm.get_normalized_laplacian_matrix(adj_matrix_1, get_degree_map(adj_matrix_1, hm1));
		double[][] normalized_laplacian_2 = gm.get_normalized_laplacian_matrix(adj_matrix_2, get_degree_map(adj_matrix_2, hm2));
		
		DoubleMatrix dm1 = new DoubleMatrix(normalized_laplacian_1);
		DoubleMatrix dm2 = new DoubleMatrix(normalized_laplacian_2);
		
		ComplexDoubleMatrix eigen_values_1 = Eigen.eigenvalues(dm1);
		ComplexDoubleMatrix eigen_values_2 = Eigen.eigenvalues(dm2);
		
		double[] eigen_val_decomp_1 = new double[eigen_values_1.toArray().length];
		double[] eigen_val_decomp_2 = new double[eigen_values_2.toArray().length];
		
		int idx = 0;
		for(ComplexDouble cde1 : eigen_values_1.toArray())
		{
			eigen_val_decomp_1[idx] = cde1.abs();
			idx++;
		}
		
		idx = 0;
		for(ComplexDouble cde2 : eigen_values_2.toArray())
		{
			eigen_val_decomp_2[idx] = cde2.abs();
			idx++;
		}

		int n = 100;
		double bin_size = 2.0/n;
		int freq;
		HashMap<Integer, Integer> hm_from_ev_1 = new HashMap<Integer, Integer>();
		for(int i = 1; i < n; i++)
		{
			hm_from_ev_1.put(i, 0);
		}
		HashMap<Integer, Integer> hm_from_ev_2 = new HashMap<Integer, Integer>();
		for(int i = 1; i < n; i++)
		{
			hm_from_ev_2.put(i, 0);
		}
		
		for(Double d : eigen_val_decomp_1)
		{
			for(Integer i : hm_from_ev_1.keySet())
			{
				if((i-1)*bin_size <= d && d < i*bin_size)
				{
					freq = hm_from_ev_1.get(i);
					freq++;
					hm_from_ev_1.put(i, freq);
				}
				else if(d == i*bin_size)
				{
					freq = hm_from_ev_1.get(i);
					freq++;
					hm_from_ev_1.put(i, freq);					
				}
			}
		}
		for(Double d : eigen_val_decomp_2)
		{
			for(Integer i : hm_from_ev_2.keySet())
			{
				if((i-1)*bin_size <= d && d < i*bin_size)
				{
					freq = hm_from_ev_2.get(i);
					freq++;
					hm_from_ev_2.put(i, freq);
				}
				else if(d == i*bin_size)
				{
					freq = hm_from_ev_2.get(i);
					freq++;
					hm_from_ev_2.put(i, freq);					
				}
			}
		}
		
		double[] da1 = new double[n];
		double[] da2 = new double[n];
		int j = 0;
		for(Integer i : hm_from_ev_1.keySet())
		{
			da1[j] = hm_from_ev_1.get(i);
			j++;
		}
		j=0;
		for(Integer i : hm_from_ev_2.keySet())
		{
			da2[j] = hm_from_ev_2.get(i);
			j++;
		}
		return jensenShannonDivergence(da1, da2);
	}

	public static double jensenShannonDivergence(double[] p1, double[] p2) {
		assert(p1.length == p2.length);
	    double[] average = new double[p1.length];
	    for (int i = 0; i < p1.length; ++i) {
	    	average[i] += (p1[i] + p2[i])/2;
	    }
	    return (klDivergence(p1, average) + klDivergence(p2, average))/2;
	}

	public static final double log2 = Math.log(2);
	   
	public static double klDivergence(double[] p1, double[] p2) {
		double klDiv = 0.0;
		for (int i = 0; i < p1.length; ++i) {
			if (p1[i] == 0) { continue; }
			if (p2[i] == 0.0) { continue; }
			klDiv += p1[i] * Math.log( p1[i] / p2[i] );
		}
    return klDiv / log2;
    }
	
	public HashMap<String, Integer> get_degree_map(int[][] adjacency_matrix, HashMap<String, Integer> hm){
		HashMap<String, Integer> degree_map = new HashMap<String, Integer>();
		for(int i = 0; i < adjacency_matrix.length; i++)
		{
			int temp_sum = 0;
			for(int j = 0; j < adjacency_matrix.length; j++)
			{
				temp_sum += adjacency_matrix[i][j];
			}
			degree_map.put(Integer.toString(i), temp_sum);
		}
		return degree_map;
	}
}
