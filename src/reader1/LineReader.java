package reader1;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

//A classe eh responsavel por ler apenas as mensagens de erro sem 
//sem considerar os stacktraces

public class LineReader {
	private String REFERENCE_DATE = "2017-01-12 ";
	
	private BufferedReader input;
	private ArrayList<String[]> logLines;
	private double sum = 0, var = 0, mean = 0, max = 0.0;
	private int numEvents = 0;
	private ArrayList<Double> deltas = new ArrayList<Double>();
	private HashMap<String,Integer> biGramDic;
	
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
		initializeDic();
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
				actualTime = Timestamp.valueOf(REFERENCE_DATE+actualSplit[0]);
				actual = normalizeLine(actual);
				countOccurences(actual);
				
				String nextSplit[] = next.split(" ");
				nextSplit[0] = nextSplit[0].replace(",",".");
				nextTime = Timestamp.valueOf(REFERENCE_DATE+nextSplit[0]);
				normalizeLine(next);
				
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
						//System.out.println(actual);
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
	
	private String normalizeLine(String line){
		String normLine;
		//@ representa a Timestamp
		normLine = "@"+line.substring(12);
		normLine = normLine.toLowerCase();
		for (int i = 0; i < normLine.length(); i++){
			if(Character.isDigit(normLine.charAt(i))){
				//$ substitui todos os dígitos
				normLine = normLine.substring(0,i)+"$"+normLine.substring(i+1);
				i--;
			}else{
				if(!Character.isLetter(normLine.charAt(i)) && (normLine.charAt(i)!= '@') && (normLine.charAt(i)!= '$')){
					normLine = normLine.substring(0,i)+normLine.substring(i+1);
					i--;
				}
			}
		}
		System.out.println(normLine);
		return normLine;
	}
	
	private void initializeDic(){
		biGramDic = new HashMap<String, Integer>();
		String char1, char2;
		
		//Array que contem todos os simbolos do log normalizado
		ArrayList<String> symbols = new ArrayList<String>();
		for(int i = 97; i<= 122; i++){
			symbols.add(new Character((char) i).toString());
		}
		for(int i = 130; i<= 164; i++){
			symbols.add(new Character((char) i).toString());
			switch(i){
			case 130:
				i = 132;
				break;
			case 133:
				i = 134;
				break;
			case 136:
				i = 137;
				break;
			case 138:
				i = 140;
				break;
			case 141:
				i = 148;
				break;
			case 149:
				i = 150;
				break;
			case 151:
				i = 159;
				break;
				
			}
		}
		symbols.add("@");
		symbols.add("$");
		
		for(int i = 0; i < symbols.size(); i++){
			char1 = symbols.get(i);
			String aux= "";
			for(int j = 0; j < symbols.size(); j++){
				char2 = symbols.get(j);
				biGramDic.put(char1+char2, new Integer(0));
				aux = aux+char1+char2+" ";
			}
			System.out.println(aux);
		}
	}
	
	private void countOccurences(String line){
		int i = 0;
		String aux;
		int val;
		while(i+1 < line.length()){
			aux = line.substring(i,i+2);
			val = biGramDic.get(aux).intValue();
			val++;
			biGramDic.put(aux, new Integer(val));
			i++;
		}
	}
	
	//Substitui virgula por ponto
	private void printARFF(String time){
		
	}
}
