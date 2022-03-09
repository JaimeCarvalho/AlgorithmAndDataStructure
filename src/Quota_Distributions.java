// Interface responsavel pelos metodos de determinacao dos valores maximo, minimo e
//intermedio do intervalo de pesquisa binaria. Implementada pelas classes:
	// Adam_Distribution, Hill_Distribution, Jefferson_Distribution e Webster_Distribution

import java.util.ArrayList;

public interface Quota_Distributions {

	long getMaxValue();
	void setMaxValue(long maxvalue);
	long getMinValue();
	void setMinValue(long minvalue);
	ArrayList<State> getStates();
	long getMid();
	void setMid(long mid);
	double calculateQuota(int i);
}
