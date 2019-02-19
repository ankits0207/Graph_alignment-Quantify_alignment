package com.quantify.demo;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Reader {

	public static void main(String[] args) throws IOException {
		ArrayList<MyPair> al_of_mp = read("Final_Alignment.txt");
		for(int i = 0; i < al_of_mp.size(); i++)
		{
			MyPair mp1 = al_of_mp.get(i);
			for(int j = i+1; j < al_of_mp.size(); j++)
			{
				MyPair mp2 = al_of_mp.get(j);
				if(mp1.n2.equals(mp2.n2))
				{
					System.out.println(mp1.n2);
				}
			}
		}
	}
	
	public static ArrayList<MyPair> read(String fname) throws IOException{
		System.out.println("Reading " + fname + " file...");
		
		ArrayList<MyPair> al_of_pair = new ArrayList<MyPair>();
		
		BufferedReader br = null;
		FileReader fr = null;
		
		try{
			fr = new FileReader(fname);
			br = new BufferedReader(fr);
			
			String Line;
			
			while((Line = br.readLine()) != null){
				MyPair mp = new MyPair();
				String[] str_arr = Line.split(" ");
				mp.n1 = str_arr[0];
				mp.n2 = str_arr[1];
				
				al_of_pair.add(mp);				
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if(br != null)
				br.close();
			if(fr != null)
				fr.close();
		}		
		return al_of_pair;		
	}
	
	static class MyPair{
		String n1;
		String n2;
	}
}