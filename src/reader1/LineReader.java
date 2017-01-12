package reader1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

//A classe eh responsavel por ler apenas as mensagens de erro sem 
//sem considerar os stacktraces

public class LineReader {
	private String REFERENCE_DATE = "2017-01-12 ";
	
	private BufferedReader input;
	private ArrayList<String[]> logLines;
	private double sum = 0, var = 0, mean = 0, max = 0.0;
	private int numEvents = 0;
	private ArrayList<Double> deltas = new ArrayList<Double>();
	
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
		String actualTimeStr;
		String nextTimeStr;
		Timestamp actualTime, nextTime;
		double auxVar = 0;
		while(next != null){
			//if(actual.length()!=0){
				String actualSplit[] = actual.split(" ");
				actualSplit[0] = actualSplit[0].replace(",",".");
				String nextSplit[] = next.split(" ");
				nextSplit[0] = nextSplit[0].replace(",",".");
				actualTime = Timestamp.valueOf(REFERENCE_DATE+actualSplit[0]);
				nextTime = Timestamp.valueOf(REFERENCE_DATE+nextSplit[0]);
				long auxDelta = Math.abs(nextTime.getTime()-actualTime.getTime());
				deltas.add(new Double(auxDelta));
				
				//Atualizando os valores das features
				//SOMA
				sum = sum + auxDelta;
				//MAXIMO
				if(max < auxDelta)
					max = auxDelta;
				
				
				System.out.println(deltas.get(deltas.size()-1));
				
				//Situacoes em que sao listados diversos binds
				//if(actualSplit.length != 1){
					//if(actualSplit[1].equals("WARNING") || actualSplit[1].equals("ERROR") || actualSplit[1].equals("WARN")){
						logLines.add(actualSplit);
						System.out.println(actual);
					//}
				//}
				//next = getLine();
				//if(next.length() != 0){
					//String nextSplit[] = next.split(" ");
					//if(nextSplit.length != 1){
						
				//	}
				//}
			//}
			actual = next;
			next = getLine();
		}
		System.out.println("Chegou!");
		//MEDIA
		mean = sum/deltas.size();
		//AUXILIAR VARIANCIA
		for(int i = 0; i < deltas.size();i++){
			auxVar = auxVar + Math.pow(deltas.get(i)-mean, 2);
		}
		var = auxVar/deltas.size();
		
		
		/*
		String[] line = logLines.get(logLines.size()-1);
		for(int i = 0; i < line.length;i++)
			System.out.println(line[i]);
		System.out.println(logLines.size());
		
		for(int i=0; i < deltas.size();i++)
			System.out.println(deltas.get(i));
		*/
		System.out.println("Media = "+mean);
		System.out.println("Variancia = "+var);
		System.out.println("Soma = "+sum);
		System.out.println("Maximo = "+max);
	}
	
	private String getLine(){
		String aux = "";
		try {
			aux = input.readLine();
			while(!isOK(aux)){
				aux = input.readLine();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return aux;
	}
	
	private boolean isOK(String aux){
		if(aux == null)
			return true;
		if(aux.length() == 0 || aux.split(" ").length == 1 || isStackTrace(aux))
			return false;
		return true;
	}
	
	private boolean isStackTrace(String aux){
		//if(aux.contains("\t") || aux.contains("Caused") ||)
		if(!aux.matches("[0-9]{2}:[0-9]{2}:[0-9]{2},[0-9]{3}.*"))
			return true;
		return false;
	}
	
	//Substitui virgula por ponto
	private void printARFF(String time){
		
	}
}
