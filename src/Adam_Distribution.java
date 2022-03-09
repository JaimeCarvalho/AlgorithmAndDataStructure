import java.util.ArrayList;

public class Adam_Distribution implements Quota_Distributions {

	private long _maxvalue;
	private long _minvalue;
	private long _totalPopulation;
	private ArrayList<State> _states;
	private int _totalMandates;
	private int _numberOfStates;
	private long _mid;
	
	// Os valores iniciais do intervalo de pesquisa binaria sao logo calculados com a instancia da classe (pontos 1 e 2 da
	//estrategia do metodo rounding_methods)
	//Como este metodo arredonda as quotas sempre por excesso, o intervalo da pesquisa e entre o numero de mandatos a atribuir
	//menos o numero total de estados, e o numero de mandatos a atribuir
	public Adam_Distribution(ArrayList<State> states, long totalPopulation, int mandates) {
		_totalPopulation = totalPopulation;
		_states = states;
		_totalMandates = mandates;
		_numberOfStates = states.size();
		_maxvalue = _totalPopulation/(_totalMandates - _numberOfStates);
		_minvalue = _totalPopulation/_totalMandates;
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
	//As quotas sao arredondas por excesso
	public double calculateQuota(int i) {
		double quota = (1.0/_mid) * _states.get(i).getPopulation();
		return ((quota - (int)quota) != 0) ? ++quota : quota;
	}
	
}
