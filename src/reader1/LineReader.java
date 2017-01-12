package reader1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

//A classe eh responsavel por ler apenas as mensagens de erro sem 
//sem considerar os stacktraces

public class LineReader {
	private BufferedReader input;
	private ArrayList<String[]> logLines;
	private float sum = 0, var = 0, mean = 0, max = 0;
	private int numEvents = 0;
	
	public static void main(String args[]){
		LineReader reader = new LineReader("server.log");
		reader.readLog();
		
	}
	
	public LineReader(String filename){	
		try{
			input = new BufferedReader(new FileReader(filename));
		} catch(FileNotFoundException ex){
			System.err.println("Arquivo nao encontrado: "+filename);
		}
		logLines = new ArrayList<String[]>();
	}
	
	public void readLog(){
		String actual = getLine();
		String next = getLine();
		while(actual != null){
			//if(actual.length()!=0){
				String actualSplit[] = actual.split(" ");
				//Situacoes em que sao listados diversos binds
				//if(actualSplit.length != 1){
					if(actualSplit[1].equals("WARNING") || actualSplit[1].equals("ERROR") || actualSplit[1].equals("WARN")){
						logLines.add(actualSplit);
						System.out.println(actual);
					}
				//}
				next = getLine();
				//if(next.length() != 0){
					String nextSplit[] = next.split(" ");
					if(nextSplit.length != 1){
						
				//	}
				}
			//}
			actual = next;
			
		}
		String[] line = logLines.get(logLines.size()-1);
		for(int i = 0; i < line.length;i++)
			System.out.println(line[i]);
		System.out.println(logLines.size());
	}
	
	private String getLine(){
		String aux = "";
		try {
			aux = input.readLine();
			while(aux != null && (aux.length() == 0 || aux.split(" ").length == 1)){
				aux = input.readLine();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return aux;
	}
}
