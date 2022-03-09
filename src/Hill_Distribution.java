import java.util.ArrayList;

public class Hill_Distribution implements Quota_Distributions {

	private long _maxvalue;
	private long _minvalue;
	private long _totalPopulation;
	private ArrayList<State> _states;
	private int _totalMandates;
	private int _numberOfStates;
	private long _mid;
	
	// Os valores iniciais do intervalo de pesquisa binaria sao logo calculados com a instancia da classe (pontos 1 e 2 da
	//estrategia do metodo rounding_methods)
	//Este metodo pode arredondar por excesso ou por defeito, e portanto o intervalo da pesquisa e entre o numero de mandatos
	//a atribuir menos o numero total de estados, e o numero de mandatos a atribuir mais o numero total de estados
	public Hill_Distribution(ArrayList<State> states, long totalPopulation, int mandates) {
		_totalPopulation = totalPopulation;
		_states = states;
		_totalMandates = mandates;
		_numberOfStates = states.size();
		_maxvalue = _totalPopulation/(_totalMandates - _numberOfStates);
		_minvalue = _totalPopulation/(_totalMandates + _numberOfStates);
		_mid = (_maxvalue + _minvalue)/2;
	}
	
	public long getMaxValue() {
		return _maxvalue;
	}

	public void setMaxValue(long maxvalue) {
		_maxvalue = maxvalue;
	}

	public long getMinValue() {
		return _minvalue;
	}

	public void setMinValue(long minvalue) {
		_minvalue = minvalue;
	}
	
	public ArrayList<State> getStates() {
		return _states;
	}
	
	public long getMid() {
		return _mid;
	}

	public void setMid(long mid) {
		_mid = mid;
	}
	
	// Calcula a quota do estado i com determinado valor intermedio da pesquisa binaria
	//A parte fracionaria da quota e comparada com a media geometrica - se a media for superior, a quota e arredondada por
	//defeito; se a media for igual ou superior, a quota e arredondada por excesso
	public double calculateQuota(int i) {
		double quota = (1.0/_mid) * _states.get(i).getPopulation();
		return (quota >= Math.sqrt((int)quota * (((int) quota) + 1))) ? ++quota : quota;
	}
	
}
