package Metodos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ProjetoADS {
	private int _mandates;
	private int _numberState;
	private long _totalPopulation;
	//private static TypeOfMethod _method;
	
	public ProjetoADS(int mandates, int numberState) {
		_mandates = mandates;
		_numberState = numberState;
	}
	
	public long getTotalPopulation() {
		return _totalPopulation;
	}
	
	public void setTotalPopulation(long population) {
		_totalPopulation = population;
	}
	
	public static void main(String[] args) throws IOException {
		BufferedReader br=new BufferedReader(new FileReader("data")); // ler do ficheiro
		//BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); // ler do teclado
		
		String line=br.readLine(); //ler a primeira linha
		String[] split=line.split("\\s+"); //guarda as duas strings(mandatos e nr de estados)
		ProjetoADS proj = new ProjetoADS(Integer.parseInt(split[0]),Integer.parseInt(split[1]));
		
		ArrayList<State> estados = new ArrayList<>(); // para guardar os estados
		//ler as restantes linhas, excepto a ultima
		//System.out.println("Comece por introduzir o nome do estado, e a população do estado, mas separados por um espaço em branco: ");
		long totalPopulation = 0;
		for(int i=0;i<proj.getNumberState();i++) { 
			line=br.readLine();
			split=line.split("\\s+");
			estados.add(new State(split[0],split[1])); // criar os estados
			totalPopulation += Long.parseLong(split[1]);
			}
		proj.setTotalPopulation(totalPopulation);
		//System.out.println("Introduza o método: ");
		String metodo = br.readLine();//line a guardar a chave para a escolha dos metodos)
		br.close();
		proj.hamilton(estados);
		proj.jeferson(estados);
	}


	public int getMandates() {
		return _mandates;
	}

	public int getNumberState() {
		return _numberState;
	}


	

	private static class State implements Comparable<State>{
		private String _name;
		private long _population;
		private int _mandates;
		private double _quota;
		
		public State(String name, String population) {
			_population = Long.parseLong(population);
			_name = name;
		}

		public long getPopulation() {
			return _population;
		}

		public int getMandates() {
			return _mandates;
		}
		
		public String getName() {
			return _name;
		}

		public void setMandates(int mandates) {
			_mandates = mandates;
		}

		public double getQuota() {
			return _quota;
		}

		public void setQuota(double quota) {
			_quota = quota;
			
			setMandates((int)_quota); //atualizar o mandato automaticamente
		}
		
		@Override
		public int compareTo(State o) {
				if(((this.getQuota() - (int)this.getQuota()) < (o.getQuota() - (int)o.getQuota())))	
					return 1;
				else if(((this.getQuota() - (int)this.getQuota()) > (o.getQuota() - (int)o.getQuota())))
					return -1;
				else if(this.getQuota() == o.getQuota()) {
					System.out.println("Invalid");
					System.exit(0);
				}
					
				return 0;
		}

		
	}
	
	public void hamilton(ArrayList<State> listOfStates) {
		long totalPopulation = getTotalPopulation();
		int mandates = getMandates();
		int new_mandates = 0;
		double quota = 0;

		for (State s : listOfStates) {
			quota = s.getPopulation() * (double)mandates/totalPopulation;
			s.setQuota(quota);
			new_mandates += (int)quota;
		}

		ArrayList<State> states_copy = new ArrayList<>();
		states_copy.addAll(listOfStates);
		Collections.sort(states_copy); // Ordenar por ordem da parte decimal
		int i = 0;
		while(new_mandates != mandates)	{ // atribuiçao dos restantes mandatos
			states_copy.get(i).setQuota((int)states_copy.get(i).getQuota() + 1);
			new_mandates++;
			i++;
		}
		//System.out.println("**************** depois de ordenar por ordem da parte decimal *******************");
		
		for(State s : listOfStates) {
			//System.out.println(states.get(i));
			System.out.printf("%s %3d\n", s.getName(), s.getMandates());
		}
	}
	
	
	public void jeferson(ArrayList<State> states) {
		long totalPopulation = getTotalPopulation();
		int mandates = getMandates();
		int numberStates = states.size();
		int new_mandates = 0;
		long mindivisor = mandates;
		long maxdivisor = mandates + numberStates;
		int quota = 0;
	    long mid;
	    long denominator;
	    System.out.println("*****Jefferson*****");
	    long tini = System.nanoTime();
		mid = (maxdivisor + mindivisor)/2;
		while(new_mandates != mandates) {
			denominator = totalPopulation/mid;
			System.out.println("denominador e tot pop" + denominator + "; " + totalPopulation);
			for(State s: states) {
				quota = ((int) ((double)(1.0/denominator) * s.getPopulation()));
				System.out.println("1/denominador " + ((1.0/denominator)));
				System.out.println("popula��o do estado: " + s.getPopulation());
				System.out.println(quota);
				s.setMandates(quota);
				new_mandates += quota;
			}

			System.out.println(new_mandates);
			if(new_mandates < mandates) {
				System.out.println("Entrei no if");
				long old_mid = mid;
				mid = (maxdivisor + mid)/2;
				mindivisor = old_mid;
				new_mandates = 0;

			}
			else if (new_mandates > mandates) {
				System.out.println("Entrei no else");
				long old_mid = mid;
				mid = (mid + mindivisor)/2;
				maxdivisor = old_mid;
				new_mandates = 0;

			}
		}
		long tfin = System.nanoTime();
		System.out.println(tfin-tini);
		for(State s: states)
			System.out.printf("%s %3d\n", s.getName(), s.getMandates());
	}
}