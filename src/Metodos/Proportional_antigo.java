package Metodos;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class Proportional_antigo {
	
	private int _totalMandates;
	private int _numberOfStates;
	private long _totalPopulation;
	
	public Proportional_antigo(int mandates, int numberState) {
		_totalMandates = mandates;
		_numberOfStates = numberState;
	}

	public int getTotalMandates() {
		return _totalMandates;
	}

	public int getNumberState() {
		return _numberOfStates;
	}
	
	public long getTotalPopulation() {
		return _totalPopulation;
	}
	
	public void setTotalPopulation(long population) {
		_totalPopulation = population;
	}

	public static void main(String[] args) throws IOException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = br.readLine();
		String[] split = line.split("\\s+");
		Proportional_antigo proj = new Proportional_antigo(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
		
		ArrayList<State> estados = new ArrayList<>(); // Lista que guarda os estados e respetiva populacao
		long totalPopulation = 0;
		
		// Le as restantes linhas, exceto a ultima, e vai calculando a populacao maxima
		for(int i = 0; i < proj.getNumberState(); i++) { 
			line = br.readLine();
			split = line.split("\\s+");
			estados.add(new State(split[0], split[1])); // Adiciona cada estado a lista
			totalPopulation += Long.parseLong(split[1]);
			}
		
		proj.setTotalPopulation(totalPopulation);
		line = br.readLine(); // A ultima linha guarda a chave para a escolha dos metodos
		br.close();

		// De acordo com a chave introduzida, realiza o respetivo metodo
		if (line.equals("H"))
			proj.hamilton(estados);
		else if (line.equals("FJ") || line.equals("FW"))
			proj.smallest_fraction(estados, line);
		else
			proj.rounding_methods(estados, line.charAt(0));
		
	}

	// Classe interna que instancia cada estado com os seus respetivos atributos
	private static class State implements Comparable<State> {
		
		private String _name;
		private long _population;
		private int _mandates;
		private double _quota;
		
		public State(String name, String population) {
			_population = Long.parseLong(population);
			_name = name;
		}
		
		public String getName() {
			return _name;
		}

		public long getPopulation() {
			return _population;
		}

		public int getMandates() {
			return _mandates;
		}

		public void setMandates(int mandates) {
			_mandates = mandates;
		}		

		public double getQuota() {
			return _quota;
		}

		public void setQuota(double quota) {
			_quota = quota;
			setMandates((int)_quota); // Atualiza automaticamente o numero de mandatos atribuido 
		}
		
		// Metodo que retorna apenas a parte fracionaria da quota
		public double getFractionQuota() {
			return getQuota() - (int) getQuota();
		}
		
		// Compara os estados pela parte fracionaria (apenas utilizado no metodo de Hamilton)
		@Override
		public int compareTo(State o) {
			if(getFractionQuota() < (o.getFractionQuota()))	
				return 1;
			else if(getFractionQuota() > (o.getFractionQuota()))
				return -1;
			return 0;
		}
	}
	
	
	// Estrategia do metodo de Hamilton:
	// 1. Recebe a lista de estados e calcula a quota de cada um deles
	// 2. Sao atribuidos os mandatos aos estados de acordo com a parte inteira das quotas
	// 3. Os estados sao copiados para outro array, que e ordenado de forma decrescente pela parte fracionaria das quotas
	// 4. Caso existem mandatos por atribuir, verificamos se tem solucao - compara as partes fracionarias do ultimo estado que receberia
	//um mandato, com o primeiro estado que nao receberia; caso tenham partes fracionarias iguais, nao existe solucao ("Invalid")
	// 5. Atribuimos os restantes mandatos de acordo com a ordem decrescente da parte fracionaria das quotas
	// 6. O output utiliza a lista original, visto que mantem a ordem de introducao
	
	public void hamilton(ArrayList<State> states) {
		
		long totalPopulation = getTotalPopulation();
		int totalMandates = getTotalMandates();
		int attributedMandates = 0;
		double quota = 0;
		
		// Ponto 1 e 2 da estrategia
		for (State s : states) {
			quota = s.getPopulation() * (double) totalMandates/totalPopulation;
			s.setQuota(quota);
			attributedMandates = attributedMandates + (int) quota;
		}
		
		// Ponto 3 da estrategia
		ArrayList<State> states_copy = new ArrayList<>();
		states_copy.addAll(states);
		Collections.sort(states_copy); 						// Ordenar pela parte fracionaria da quota
		
		int cutoff = totalMandates - attributedMandates;	// Indice que corresponde ao primeiro estado da lista ordenada que nao recebe um mandato extra
		int last_attributed = cutoff - 1;					// Indice que corresponde ao ultimo estado da lista ordenada que recebe um mandato extra

		// Ponto 4 da estrategia
		if (states_copy.get(last_attributed).getFractionQuota() == states_copy.get(cutoff).getFractionQuota()) {
			System.out.println("Invalid");
			System.exit(0);
		}
		// Ponto 5 da estrategia
		else {
			int i = 0;
			while(attributedMandates != totalMandates)	{
				State s_i = states_copy.get(i);
				s_i.setQuota((int) s_i.getQuota() + 1);
				attributedMandates++;
				i++;
			}
		}

		// Ponto 6 da estrategia
		for(State s : states) {
			System.out.printf("%s %3d%n", s.getName(), s.getMandates());
		}
	}
	
	
	// Estrategia de todos os metodos que implicam o arredondamento das quotas (metodos de Jefferson (J), de Adam (A), de Webster (W) e de Hill (L))
	// Estes metodos realizam pesquisa binaria para encontrar a fracao que consegue atribuir todos os mandatos
	// 1. O intervalo da pesquisa binaria e determinado de acordo com o metodo utilizado (variavel choice). Utilizamos o inverso da fracao, isto e,
	//a populacao total a dividir pelo numero de mandatos, pois assim ao calcular as quotas de cada estado apenas temos de dividir a sua populacao
	//por esta fracao
		// (J): Como este metodo arredonda as quotas sempre por defeito (ignora a parte fracionaria), o intervalo da pesquisa e entre o numero de
		//mandatos a atribuir e o numero de mandatos a atribuir mais o numero total de estados
		// (A): Como este metodo arredonda as quotas sempre por excesso, o intervalo da pesquisa e entre o numero de mandatos a atribuir menos o
		//numero total de estados e o numero de mandatos a atribuir
		// (W/L): Ambos os metodos podem arredondar por excesso ou por defeito, e portanto o intervalo da pesquisa e entre o numero de mandatos a
		//atribuir menos o numero total de estados e o numero de mandatos a atribuir mais o numero total de estados
	// 2. O ponto intermedio do intervalo e calculado (variavel mid) e esse valor e utilizado para calcular as quotas de cada estado
	// 3. As quotas de cada estado sao calculadas e atribuidas de acordo com o metodo escolhido e a fracao mid utilizada
		// (J): As quotas sao arredondadas por defeito
		// (A): As quotas sao arredondas por excesso
		// (W): A quota e comparada com a media dos inteiros adjacentes - se a media for superior, a quota e arredondada por defeito; se a
		//media for inferior, a quota e arredondada por excesso; se a media for igual, a quota e arredondada de forma a ser um numero par
		// (L): A parte fracionaria da quota e comparada com a media geometrica - se a media for superior, a quota e arredondada por defeito;
		//se a media for igual ou superior, a quota e arredondada por excesso
	// 4. Caso o numero de mandatos atribuidos com a fracao mid seja diferente do numero total de mandatos, a fracao mid e recalculda de acordo com
	//o metodo da pesquisa binaria. Os pontos 2 e 3 serao repetidos ate que o numero de mandatos atribuidos seja igual ao numero total de mandatos:
		// Se existerem mandatos por atribuir, entao a fracao mid tem de ser inferior, e portanto o novo intervalo de pesquisa sera entre o extremo
		//inferior e o mid - 1. O novo mid e calculado de acordo com o novo intervalo.
		// Se forem atribuidos mandatos a mais, entao a fracao mid tem de ser superior, e portanto o novo intervalo de pesquisa sera entre o extremo
		//superior e o mid + 1. O novo mid e calculado de acordo com o novo intervalo.
		// Se a fracao mid ja e igual a um dos extremos do intervalo de pesquisa, entao nao existe solucao ("Invalid")
	// 5. Caso o numero de mandatos atribuidos com a fracao mid seja igual ao numero total de mandatos, entao ja se encontrou uma fracao que e solucao.
	//O output e a lista de estados e o numero de mandatos atribuidos, seguindo a ordem de introducao
	
	public void rounding_methods(ArrayList<State> states, char choice) {
		
		long totalPopulation = getTotalPopulation();
		int mandates = getTotalMandates();
		int numberStates = states.size();
		int new_mandates = 0;
		long maxvalue;
		long minvalue;
		double quota = 0;
	    long mid;
	    
		// Ponto 1 da estrategia
		if (choice == 'J') { // JEFFERSON
			maxvalue = totalPopulation/mandates;
			minvalue = totalPopulation/(mandates + numberStates);
		}
		else if (choice == 'A') {  // ADAM
			maxvalue = totalPopulation/(mandates - numberStates);
			minvalue = totalPopulation/mandates;
		}
		else { // WEBSTER AND HILL
			maxvalue = totalPopulation/(mandates - numberStates);
			minvalue = totalPopulation/(mandates + numberStates);
		}
	    
	    // Ponto 2 da estrategia
		mid = (maxvalue + minvalue)/2;
		
		// Ponto 3 e 4 da estrategia
		while(new_mandates != mandates) {
			
			// Ponto 3 da estrategia
			for(State s : states) {
				quota = (1.0/mid) * s.getPopulation();
				
				if((quota - (int)quota) != 0 && choice == 'A')
					quota++;
				
				else if(quota - (int)quota != 0.5 && choice == 'W')
					quota = Math.round(quota);
				
				else if(quota >= Math.sqrt((int)quota * (((int) quota) + 1)) && choice == 'L')
                    quota++;
				
				// Para o metodo de Jefferson e os casos que arredondam por defeito nos metodos de Webster e Hill, o arredondamento da quota e feito aqui:
				s.setMandates((int)quota);
				new_mandates += (int)quota;
			}
			
			// Ponto 4 da estrategia
			if (new_mandates != mandates && (mid == minvalue || mid == maxvalue)) {
				System.out.println("Invalid");
				System.exit(0);
			}
			
			else if(new_mandates < mandates) {
				long old_mid = mid;
				mid = (minvalue + mid)/2;
				new_mandates = 0;
				maxvalue = old_mid - 1;
			}
						
			else if (new_mandates > mandates) {
				long old_mid = mid;
				mid = (mid + maxvalue)/2;
				new_mandates = 0;
				minvalue = old_mid + 1;
			}
		}
		
		// Ponto 5 da estrategia
		for(State s: states)
			System.out.printf("%s %3d%n", s.getName(), s.getMandates());
	}

	
	// Estrategia dos metodos de Jefferson (FJ) e Webster (FW) para obter a menor fracao possivel
	// Estes metodos realizam pesquisa binaria para encontrar a fracao que consegue atribuir todos os mandatos, tal como no metodo rounding_methods,
	//tendo apenas uma pequena diferenca:
		// 4.1. Sempre que o numero de mandatos atribuidos for igual ao numero total de mandatos, entao encontramos uma fracao que e solucao. O valor de
		//mid e guardado na variavel auxiliar smallest_mid, que foi inicializada como "infinito". O numero de mandatos atribuidos e decrementado
		//propositadamente para que o ponto 4 seja realizado novamente. Assim, a pesquisa binaria continuara a ser realizada para valores inferiores de
		//mid, de forma a tentar encontrar outra fracao inferior que tambem seja solucao. Este ponto vai parar quando a fracao mid for igual a um dos
		//extremos do intervalo de pesquisa. Entao, se o smallest_mid for inferior a "infinito", significa que ja encontramos pelo menos uma fracao que e
		//solucao, e esta e apresentada; caso contrario, entao o problema nao tem solucao ("Invalid")

	public void smallest_fraction(ArrayList<State> states, String choice) {
		
		long totalPopulation = getTotalPopulation();
		int mandates = getTotalMandates();
		int numberStates = states.size();
		int new_mandates = 0;
		long minvalue;
		long maxvalue;
		double quota = 0;
	    long mid;
	    long smallest_mid = (long) Integer.MAX_VALUE;
		
		// Ponto 1 da estrategia
		if (choice.equals("FJ")) { // JEFFERSON
			maxvalue = totalPopulation/mandates;
			minvalue = totalPopulation/(mandates + numberStates);
		}
		else { // WEBSTER
			maxvalue = totalPopulation/(mandates - numberStates);
			minvalue = totalPopulation/(mandates + numberStates);
		}
		
		// Ponto 2 da estrategia
		mid = (maxvalue + minvalue)/2;
		
		// Ponto 3, 4 e 4.1 da estrategia
		while(new_mandates != mandates) {
			
			for(State s : states) {
				quota = (1.0/mid) * s.getPopulation();
				
				// Ponto 3 da estrategia
				if(quota - (int)quota != 0.5 && choice.equals("FW")) //Webster
					quota = Math.round(quota);
				
				// Para o metodo de Jefferson e os casos que arredondam por defeito no de Webster, o arredondamento da quota e feito aqui
				s.setMandates((int) quota);
				new_mandates += (int) quota;
			}
			
			// Ponto 4.1 da estrategia
			if (new_mandates == mandates) {
				smallest_mid = mid;
				new_mandates--;
			}

			// Ponto 4 da estrategia
			if (new_mandates != mandates && (mid == minvalue || mid == maxvalue)) {
				// Pelo menos uma solucao foi encontrada
				if (smallest_mid < (long) Integer.MAX_VALUE) {
					System.out.println("[f=1/" + smallest_mid + "]");
					System.exit(0);
				}
				// Nao existe solucao
				else {
					System.out.println("Invalid");
					System.exit(0);
				}
				
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
	}
}