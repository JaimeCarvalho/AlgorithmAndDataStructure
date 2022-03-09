import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class Proportional {
	
	private int _totalMandates;
	private int _numberOfStates;
	private long _totalPopulation;
	
	public Proportional(int mandates, int numberState) {
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
		Proportional proj = new Proportional(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
		
		ArrayList<State> estados = new ArrayList<>();			// Lista que guarda os estados e respetiva populacao
		long totalPopulation = 0;
		
		// Le as restantes linhas, exceto a ultima, e vai calculando a populacao maxima
		for(int i = 0; i < proj.getNumberState(); i++) { 
			line = br.readLine();
			split = line.split("\\s+");
			estados.add(new State(split[0], split[1])); 		// Adiciona cada estado a lista
			totalPopulation += Long.parseLong(split[1]);
			}
		
		proj.setTotalPopulation(totalPopulation);
		line = br.readLine(); 									// A ultima linha guarda a chave para a escolha dos metodos
		br.close();

		// De acordo com a chave introduzida, realiza o respetivo metodo. As classes responsaveis pela implementacao da
		//interface Quota_Distributions sao instanciadas, e portanto o intervalo de pesquisa binaria ja e calculado, assim
		//como o valor intermedio
		switch(line) {
        case "H":
            proj.hamilton(estados);
            break;
        case "J":
            proj.rounding_methods(new Jefferson_Distribution(estados, proj.getTotalPopulation(), proj.getTotalMandates()));
            break;
        case "A":
            proj.rounding_methods(new Adam_Distribution(estados, proj.getTotalPopulation(), proj.getTotalMandates()));
            break;
        case "W":
            proj.rounding_methods(new Webster_Distribution(estados, proj.getTotalPopulation(), proj.getTotalMandates()));
            break;
        case "L":
            proj.rounding_methods(new Hill_Distribution(estados, proj.getTotalPopulation(), proj.getTotalMandates()));
            break;
        case "FJ":
        	proj.smallest_fractions(new Jefferson_Distribution(estados, proj.getTotalPopulation(), proj.getTotalMandates()));
        	break;
        case "FW":
        	proj.smallest_fractions(new Webster_Distribution(estados, proj.getTotalPopulation(), proj.getTotalMandates()));
        	break;
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
		// 1. O intervalo da pesquisa binaria e determinado de acordo com o metodo utilizado (interface Quota_Distributions dist). Utilizamos o inverso
	//da fracao, isto e, a populacao total a dividir pelo numero de mandatos, pois assim ao calcular as quotas de cada estado apenas temos de dividir
	//a sua populacao por esta fracao
		// 2. O ponto intermedio do intervalo e calculado (variavel mid) e esse valor e utilizado para calcular as quotas de cada estado
		// 3. As quotas de cada estado sao calculadas e atribuidas de acordo com o metodo escolhido e a fracao mid utilizada
		// 4. Caso o numero de mandatos atribuidos com a fracao mid seja diferente do numero total de mandatos, a fracao mid e recalculda de acordo com
	//o metodo da pesquisa binaria. Os pontos 2 e 3 serao repetidos ate que o numero de mandatos atribuidos seja igual ao numero total de mandatos:
			// Se existerem mandatos por atribuir, entao a fracao mid tem de ser inferior, e portanto o novo intervalo de pesquisa sera entre o extremo
		//inferior e o mid. O novo mid e calculado de acordo com o novo intervalo.
			// Se forem atribuidos mandatos a mais, entao a fracao mid tem de ser superior, e portanto o novo intervalo de pesquisa sera entre o extremo
		//superior e o mid + 1. O novo mid e calculado de acordo com o novo intervalo.
			// Se a fracao mid ja e igual a um dos extremos do intervalo de pesquisa, entao nao existe solucao ("Invalid")
		// 5. Caso o numero de mandatos atribuidos com a fracao mid seja igual ao numero total de mandatos, entao ja se encontrou uma fracao que e solucao.
	//O output e a lista de estados e o numero de mandatos atribuidos, seguindo a ordem de introducao
	// Os pontos 1, 2 e 3 da estrategia sao calculados pela interface Quota_Distribution dist, isto e, pela classe que corresponde ao metodo utilizado; o
	//ponto 4 e calculado no metodo binary_search_eval, que avalia em qual das condicoes nos encontramos e, se for o caso, calcula o novo intervalo
	//de pesquisa binaria.
	
	public void rounding_methods(Quota_Distributions dist) {
		
		int mandates = getTotalMandates();
		int attributedMandates = 0;
		double quota = 0;
	    ArrayList<State> states = dist.getStates();
		long mid = dist.getMid();
		
		while(attributedMandates != mandates) {
			attributedMandates = 0;				// Reinicia a contagem dos mandatos atribuidos
			
			// Ponto 3 da estrategia
			int i = 0;
			for(State s : states) {
				quota = dist.calculateQuota(i++);
				s.setMandates((int) quota);
				attributedMandates += (int) quota;
			}

			// Ponto 4 da estrategia
			mid = binary_search_eval(attributedMandates, mid, dist.getMaxValue(), dist.getMinValue(), dist);
			dist.setMid(mid);
		}
		
		// Ponto 5 da estrategia
		for(State s: states)
			System.out.printf("%s %3d%n", s.getName(), s.getMandates());
	}

	
	// Ponto 4 da estrategia
	public long binary_search_eval(int attributedMandates, long mid, long maxvalue, long minvalue, Quota_Distributions dist) {
		
		if (attributedMandates != getTotalMandates() && (mid == minvalue || mid == maxvalue)) {
			System.out.println("Invalid");
			System.exit(0);
		}
		
		else if (attributedMandates < getTotalMandates()) {
			long old_mid = mid;
			mid = (minvalue + mid)/2;
			dist.setMaxValue(old_mid);
		}
					
		else if (attributedMandates > getTotalMandates()) {
			long old_mid = mid;
			mid = (mid + maxvalue)/2;
			dist.setMinValue(old_mid + 1);
		}
		
		return mid;
	}
	
	
	// Estrategia dos metodos de Jefferson (FJ) e Webster (FW) para obter a menor fracao possivel
	// Estes metodos realizam pesquisa binaria para encontrar a fracao que consegue atribuir todos os mandatos, tal como no metodo rounding_methods,
	//tendo apenas uma pequena diferenca:
		// 4.1. Sempre que o numero de mandatos atribuidos for igual ao numero total de mandatos, entao encontramos uma fracao que e solucao. O valor de
	//mid e guardado na variavel auxiliar smallest_mid, que foi inicializada como "infinito". O numero de mandatos atribuidos e decrementado
	//propositadamente para que o ponto 4 da estrategia seja realizado novamente. Assim, a pesquisa binaria continuara a ser realizada para valores
	//inferiores de mid, de forma a tentar encontrar outra fracao inferior que tambem seja solucao. Este ponto vai parar quando a fracao mid for igual a
	//um dos extremos do intervalo de pesquisa. Entao, se o smallest_mid for inferior a "infinito", significa que ja encontramos pelo menos uma fracao que e
	//solucao, e esta e apresentada; caso contrario, entao o problema nao tem solucao ("Invalid")
	// O ponto 4 e calculado no metodo fraction_bsearch_eval, que avalia em qual das condicoes nos encontramos e, se for o caso, calcula o novo intervalo
	//de pesquisa binaria.
	
	public void smallest_fractions(Quota_Distributions dist) {
		
		int mandates = getTotalMandates();
		int attributedMandates = 0;
		double quota = 0;
	    long smallest_mid = (long) Integer.MAX_VALUE;
	    ArrayList<State> states = dist.getStates();
		long mid = dist.getMid();
		
		while(attributedMandates != mandates) {
			attributedMandates = 0;				// Reinicia a contagem dos mandatos atribuidos
			
			// Ponto 3 da estrategia
			int i = 0;
			for(State s : states) {
				quota = dist.calculateQuota(i++);
				s.setMandates((int) quota);
				attributedMandates += (int) quota;
			}

			// Ponto 4.1 da estrategia
			if (attributedMandates == mandates) {
				smallest_mid = mid;
				attributedMandates--;
			}

			// Ponto 4 da estrategia
			mid = fraction_bsearch_eval(attributedMandates, mid, dist.getMaxValue(), dist.getMinValue(), smallest_mid, dist);
			dist.setMid(mid);
		}
	}

	
	// Ponto 4 da estrategia
	private long fraction_bsearch_eval(int attributedMandates, long mid, long maxvalue, long minvalue, long smallest_mid, Quota_Distributions dist) {
		
		if (attributedMandates != getTotalMandates() && (mid == minvalue || mid == maxvalue)) {
			
			// Pelo menos uma solucao foi encontrada - solucao com menor fracao
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
		
		else if(attributedMandates < getTotalMandates()) {
			long old_mid = mid;
			mid = (minvalue + mid)/2;
			dist.setMaxValue(old_mid);
		}
		
		else if (attributedMandates > getTotalMandates()) {
			long old_mid = mid;
			mid = (mid + maxvalue)/2;
			dist.setMinValue(old_mid + 1);
		}

		return mid;
	}
}