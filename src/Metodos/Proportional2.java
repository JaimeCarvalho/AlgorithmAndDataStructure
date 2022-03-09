package Metodos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class Proportional2 {
	private int _mandates;
	private int _numberState;
	private long _totalPopulation;
	//private static TypeOfMethod _method;
	
	public Proportional2(int mandates, int numberState) {
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
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); // ler do teclado
		
		String line=br.readLine(); //ler a primeira linha
		String[] split=line.split("\\s+"); //guarda as duas strings(mandatos e nr de estados)
		Proportional2 proj = new Proportional2(Integer.parseInt(split[0]),Integer.parseInt(split[1]));
		
		ArrayList<State> estados = new ArrayList<>(); // para guardar os estados
		//ler as restantes linhas, excepto a ultima
		//System.out.println("Comece por introduzir o nome do estado, e a populaco do estado, mas separados por um espaco em branco: ");
		long totalPopulation = 0;
		for(int i=0;i<proj.getNumberState();i++) { 
			line=br.readLine();
			split=line.split("\\s+");
			estados.add(new State(split[0],split[1])); // criar os estados
			totalPopulation += Long.parseLong(split[1]);
			}
		proj.setTotalPopulation(totalPopulation);
		//System.out.println("Introduza o metodo: ");
		line = br.readLine();//line a guardar a chave para a escolha dos metodos)
		br.close();
		if (line.equals("H"))
			proj.hamilton(estados);
		else if (line.equals("J"))
			proj.jeferson(estados);
		else if (line.equals("A"))
			proj.adam(estados);
		else if (line.equals("W"))
			proj.webster(estados);
		else if (line.equals("FJ"))
			proj.jefferson_fraction(estados);
		else if (line.equals("FW"))
			proj.webster_fraction(estados);
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
			
			setMandates((int)_quota); //autalizar o mandato automaticamente
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
		
		for (int i = 0; i< listOfStates.size(); i++) {
			quota = listOfStates.get(i).getPopulation() *(double)mandates/totalPopulation;
			listOfStates.get(i).setQuota(quota);
			new_mandates = new_mandates + (int)quota;
		}
		
		ArrayList<State> states_copy = new ArrayList<>();
		states_copy.addAll(listOfStates);
		Collections.sort(states_copy); // Ordenar por ordem da parte decimal
		int i = 0;
		while(new_mandates != mandates)	{ // atribuicao dos restantes mandatos
			states_copy.get(i).setQuota((int)states_copy.get(i).getQuota() + 1);
			new_mandates = new_mandates + 1;
			i++;
		}
		//System.out.println("**************** depois de ordenar por ordem da parte decimal *******************");
		
		for(i = 0;i < listOfStates.size(); i++) {
			System.out.printf("%s %3d%n",listOfStates.get(i).getName(), listOfStates.get(i).getMandates());
		}
	}
	
	
	public void jeferson(ArrayList<State> states) {
		long totalPopulation = getTotalPopulation();
		int mandates = getMandates();
		int numberStates = states.size();
		int new_mandates = 0;
		long minvalue = totalPopulation/mandates;
		long maxvalue = totalPopulation/(mandates + numberStates);
		int quota = 0;
	    long mid;
	    //long denominator;
	    //System.out.println("*****Jefferson*****");
		mid = (maxvalue + minvalue)/2;
		while(new_mandates != mandates) {
			//denominator = totalPopulation/mid;
			for(State s: states) {
				quota = ((int) ((double)(1.0/mid) * s.getPopulation()));
				s.setMandates(quota);
				new_mandates += quota;
			}
			if (new_mandates != mandates && (mid == minvalue || mid == maxvalue)) {
				System.out.println("Invalid");
				System.exit(0);
			}
			else if(new_mandates < mandates) {
				long old_mid = mid;
				mid = (maxvalue + mid)/2;
				new_mandates = 0;
				minvalue = old_mid;
			}
			else if (new_mandates > mandates) {
				long old_mid = mid;
				mid = (mid + minvalue)/2;
				new_mandates = 0;
				maxvalue = old_mid;
			}
		}
		//System.out.println(mid);

		for(State s: states)
			System.out.printf("%s %3d%n", s.getName(), s.getMandates());
	}

	public void adam(ArrayList<State> states) {
		long totalPopulation = getTotalPopulation();
		int mandates = getMandates();
		int numberStates = states.size();
		int new_mandates = 0;
		long maxvalue;
		long minvalue;
		
		minvalue = totalPopulation/mandates;
		maxvalue = totalPopulation/(mandates - numberStates);

		double quota = 0;
	    long mid;
		mid = (maxvalue + minvalue)/2;
		while(new_mandates != mandates) {
			
			for(State s: states) {
				quota = (1.0/mid) * s.getPopulation();
				
				if((quota - (int)quota) != 0)//Adam
					quota++;

				s.setMandates((int)quota);
				new_mandates += (int)quota;
			}
			
			if (new_mandates != mandates && (mid == minvalue || mid == maxvalue)) {
				System.out.println("Invalid");
				System.exit(0);
			}
			else if(new_mandates < mandates) {
				long old_mid = mid;
				mid = (minvalue + mid)/2;
				new_mandates = 0;
				maxvalue = old_mid;
			}
						
			else if (new_mandates > mandates) {
				long old_mid = mid;
				mid = (mid + maxvalue)/2;
				new_mandates = 0;
				minvalue = old_mid;
			}
		}
		
		for(State s: states)
			System.out.printf("%s %3d%n", s.getName(), s.getMandates());
	}


	public void webster(ArrayList<State> states) {
		long totalPopulation = getTotalPopulation();
		int mandates = getMandates();
		int numberStates = states.size();
		int new_mandates = 0;
		long minvalue = totalPopulation/(mandates - numberStates);
		long maxvalue = totalPopulation/(mandates + numberStates);
		int quota = 0;
		double f_quota;
	    long mid;
	    //long denominator;
		mid = (maxvalue + minvalue)/2;
		//int count = 0;
		while(new_mandates != mandates) { // && count < 10) {
			//denominator = totalPopulation/mid;
			for(State s: states) {
				f_quota = (double)(1.0/mid) * s.getPopulation();
				if (f_quota - (int)f_quota == 0.5 && (int)f_quota % 2 == 0)
					quota = (int)f_quota;
				else
					quota = (int)Math.round(f_quota);
				s.setMandates(quota);
				new_mandates += quota;
			}
			//System.out.println("mid=" + mid + "\nmandatos atribuidos=" + new_mandates);
			if (new_mandates != mandates && (mid == minvalue || mid == maxvalue)) {
				System.out.println("Invalid");
				System.exit(0);
			}
			else if(new_mandates < mandates) {
				long old_mid = mid;
				mid = (maxvalue + mid)/2;
				new_mandates = 0;
				minvalue = old_mid;
			}
			else if (new_mandates > mandates) {
				long old_mid = mid;
				mid = (mid + minvalue)/2;
				new_mandates = 0;
				maxvalue = old_mid;
			}
			//count++;
		}
		//System.out.println(mid);
		for(State s: states)
			System.out.printf("%s %3d%n", s.getName(), s.getMandates());
	}


	public void jefferson_fraction(ArrayList<State> states) {
		long totalPopulation = getTotalPopulation();
		int mandates = getMandates();
		int numberStates = states.size();
		int new_mandates = 0;
		long minvalue = totalPopulation/mandates;
		long maxvalue = totalPopulation/(mandates + numberStates);
		//System.out.println("[" + minvalue + ", " + maxvalue + "]");
		int quota = 0;
		//double f_quota;
	    long mid;
	    long smallest_mid = (long) Integer.MAX_VALUE;
	    //boolean found_mid = false;
	    //long denominator;
		mid = (maxvalue + minvalue)/2;
		//int count = 0;
		while(new_mandates != mandates) {// && count < 20) {
			//denominator = totalPopulation/mid;
			for(State s: states) {
				quota = ((int) ((double)(1.0/mid) * s.getPopulation()));
				s.setMandates(quota);
				new_mandates += quota;
			}
			System.out.println("mid=" + mid + "\nmandatos atribuidos=" + new_mandates);
			if (new_mandates == mandates && mid < smallest_mid) {
				smallest_mid = mid;
				//mid--;
				//found_mid = true;
				new_mandates--;
				System.out.println("igual, " + mid + ", " + new_mandates);
			}
			if (new_mandates != mandates && (mid == minvalue || mid == maxvalue)) {
				if (smallest_mid < (long) Integer.MAX_VALUE) {
					System.out.printf("[f=1/%d]", smallest_mid);
					System.exit(0);
				}
				else {
					System.out.println("Invalid");
					System.exit(0);
				}
			}
			else if(new_mandates < mandates) {
				long old_mid = mid;
				mid = (maxvalue + mid)/2;
				new_mandates = 0;
				minvalue = old_mid;
				System.out.println("menor, [" + maxvalue + ", " + mid + ", " + minvalue + "], " + new_mandates);
			}
			else if (new_mandates > mandates) {
				long old_mid = mid;
				mid = (mid + minvalue)/2;
				new_mandates = 0;
				maxvalue = old_mid;
				System.out.println("maior, [" + maxvalue + ", " + mid + ", " + minvalue + "], " + new_mandates);
			}
			//count++;
			}
		}
	
	public void webster_fraction(ArrayList<State> states) {
		long totalPopulation = getTotalPopulation();
		int mandates = getMandates();
		int numberStates = states.size();
		int new_mandates = 0;
		long minvalue = totalPopulation/(mandates - numberStates);
		long maxvalue = totalPopulation/(mandates + numberStates);
		//System.out.println("[" + minvalue + ", " + maxvalue + "]");
		int quota = 0;
		double f_quota;
	    long mid;
	    long smallest_mid = (long) Integer.MAX_VALUE;
	    //boolean found_mid = false;
	    //long denominator;
		mid = (maxvalue + minvalue)/2;
		//int count = 0;
		while(new_mandates != mandates) {// && count < 20) {
			//denominator = totalPopulation/mid;
			for(State s: states) {
				f_quota = (double)(1.0/mid) * s.getPopulation();
				if (f_quota - (int)f_quota == 0.5 && (int)f_quota % 2 == 0)
					quota = (int)f_quota;
				else
					quota = (int)Math.round(f_quota);
				s.setMandates(quota);
				new_mandates += quota;
			}
			//System.out.println("mid=" + mid + "\nmandatos atribuidos=" + new_mandates);
			if (new_mandates == mandates && mid < smallest_mid) {
				smallest_mid = mid;
				//mid--;
				//found_mid = true;
				new_mandates--;
				//System.out.println("igual, " + mid + ", " + new_mandates);
			}
			if (new_mandates != mandates && (mid == minvalue || mid == maxvalue)) {
				if (smallest_mid < (long) Integer.MAX_VALUE) {
					System.out.printf("[f=1/%d]", smallest_mid);
					System.exit(0);
				}
				else {
					System.out.println("Invalid");
					System.exit(0);
				}
			}
			else if(new_mandates < mandates) {
				long old_mid = mid;
				mid = (maxvalue + mid)/2;
				new_mandates = 0;
				minvalue = old_mid;
				//System.out.println("menor, [" + maxvalue + ", " + mid + ", " + minvalue + "], " + new_mandates);
			}
			else if (new_mandates > mandates) {
				long old_mid = mid;
				mid = (mid + minvalue)/2;
				new_mandates = 0;
				maxvalue = old_mid;
				//System.out.println("maior, [" + maxvalue + ", " + mid + ", " + minvalue + "], " + new_mandates);
			}
			//count++;
			}
		}
	}
		

