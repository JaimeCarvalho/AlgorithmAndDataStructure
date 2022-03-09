// Classe que instancia cada estado com os seus respetivos atributos:
	// Nome e populacao
// Tambem contem os atributos que sao calculados nos metodos respetivos:
	// Numero de mandatos atribuidos e quota

public class State implements Comparable<State> {
	
	private String _name;
	private long _population;
	private int _attributedMandates;
	private double _quota;
	
	public State(String name, String population) {
		_name = name;
		_population = Long.parseLong(population);
	}
	
	public String getName() {
		return _name;
	}

	public long getPopulation() {
		return _population;
	}

	public int getMandates() {
		return _attributedMandates;
	}

	public void setMandates(int mandates) {
		_attributedMandates = mandates;
	}		

	public double getQuota() {
		return _quota;
	}

	public void setQuota(double quota) {
		_quota = quota;
		setMandates((int) _quota);		// Atualiza automaticamente o numero de mandatos atribuidos
	}
	
	// Metodo que retorna apenas a parte fracionaria da quota
	public double getFractionQuota() {
		return getQuota() - (int) getQuota();
	}
	
	// Compara os estados pela parte fracionaria (apenas utilizado no metodo de Hamilton)
	@Override
	public int compareTo(State other) {
		if(getFractionQuota() < (other.getFractionQuota()))	
			return 1;
		else if(getFractionQuota() > (other.getFractionQuota()))
			return -1;
		return 0;
	}
}