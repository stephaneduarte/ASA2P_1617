//PROJETO ASA

import java.io.*;
import java.util.*;

class Cidade {
	private int _id;

	//Variaveis union-find
	private Cidade _cidlig;
	private int _rank = 0;

	public Cidade(int id) { _id = id; }

	public int getId() { return _id; }
	public void setCidLig(Cidade cidade) { _cidlig = cidade; }
	public int getRank() { return _rank; }
	public void incRank() { _rank++; }
	public Cidade getCidLig() { 
		if (_cidlig != this) _cidlig = _cidlig.getCidLig();
		return _cidlig;
	}
}

class Ligacao {
	private Cidade _origem;
	private Cidade _destino;
	private int _custo;
	private boolean _aerea;

	public Ligacao(Cidade origem, Cidade destino, int custo, boolean aerea){
		_origem = origem;
		_destino = destino;
		_custo = custo;
		_aerea = aerea;
	}

	public Cidade getOrigem(){ return _origem; }
	public Cidade getDestino(){ return _destino; }
	public int getCusto(){ return _custo; }
	public boolean isAerea(){ return _aerea; }
}

public class Graph{

	private List<Cidade> _cidades = new ArrayList<Cidade>();
	private List<Ligacao> _ligacoes = new ArrayList<Ligacao>();

	public Graph(int nrcidades){
		for (int i = 0; i < (nrcidades + 1); i++){
			_cidades.add(new Cidade(i));
		}
	}

	public void initCitLig(){
		for (Cidade cidade : _cidades){
			cidade.setCidLig(cidade);
		}
	}

	public void insereLigacao(int cidadeid1, int cidadeid2, int custo){
		if(cidadeid2 != -1) _ligacoes.add(new Ligacao(_cidades.get(cidadeid1), _cidades.get(cidadeid2), custo, false));
		else _ligacoes.add(new Ligacao(_cidades.get(cidadeid1), _cidades.get(0), custo, true));
	}

	public void junta(Cidade cidade1, Cidade cidade2){
		Cidade cid1lig = cidade1.getCidLig();
		Cidade cid2lig = cidade2.getCidLig();
		if (cid1lig.getRank() < cid2lig.getRank()) cid1lig.setCidLig(cid2lig);
		else if (cid1lig.getRank() > cid2lig.getRank()) cid2lig.setCidLig(cid1lig);
		else {
			cid2lig.setCidLig(cid1lig);
			cid1lig.incRank();
		}
	}

	public void sort(){
		//Ordena as ligacoes, primeiro por custo e depois por estada vs aeroporto
		Collections.sort(_ligacoes, (new Comparator<Ligacao>() {
			@Override
			public int compare(Ligacao l1, Ligacao l2){
				int resultado = Integer.compare(l1.getCusto(), l2.getCusto());
				if (resultado == 0)
					resultado = Boolean.compare(l1.isAerea(), l2.isAerea());
				return resultado;
			}
		}));
	}

	public List kruskal(boolean aerea) {

		int _custo = 0;
		int _nraeroportos = 0;
		int _nrestradas = 0;

		int count = 0;
		int pos = 0;
		initCitLig();

		int limite;
		if (aerea) limite = _cidades.size() - 1;
		else limite = _cidades.size() - 2;

		while(count < limite && pos < _ligacoes.size()){
			Ligacao _ligacao = _ligacoes.get(pos++);

			if (!_ligacao.isAerea() || _ligacao.isAerea() == aerea){
				Cidade cidade1 = _ligacao.getOrigem();
				Cidade cid1lig = cidade1.getCidLig();

				Cidade cidade2 = _ligacao.getDestino();
				Cidade cid2lig = cidade2.getCidLig();

				if (cid1lig.getId() != cid2lig.getId()){
					junta(cid1lig, cid2lig);
					_custo += _ligacao.getCusto();
					if (_ligacao.isAerea()) _nraeroportos++;
					else _nrestradas++;
					count++;
				}
			}
		}

		List<Integer> _result = new ArrayList<Integer>();
		if (count != limite) {
			_result.add(-1);
			_result.add(-1);
			_result.add(-1);
		}
		else {
			_result.add(_custo);
			_result.add(_nraeroportos);
			_result.add(_nrestradas);
		}

		return _result;
	}

	@SuppressWarnings("unchecked")
	public static void main(String args[]){
		try{
			BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));

			int nrcidades = Integer.parseInt(bufferRead.readLine());
			Graph g = new Graph(nrcidades);

	    	int nraeroportos = Integer.parseInt(bufferRead.readLine());
	    	for (int i = 0; i < nraeroportos; i++){
	    		String[] aeroporto = bufferRead.readLine().split(" ");
	    		g.insereLigacao(Integer.parseInt(aeroporto[0]), -1, Integer.parseInt(aeroporto[1]));
	    	}

	    	int nrestradas = Integer.parseInt(bufferRead.readLine());
			for (int i = 0; i < nrestradas; i++){
	    		String[] estrada = bufferRead.readLine().split(" ");
	    		g.insereLigacao(Integer.parseInt(estrada[0]), Integer.parseInt(estrada[1]), Integer.parseInt(estrada[2]));
	    	}

	    	g.sort();

	    	List<Integer> result1 = g.kruskal(false);
	    	List<Integer> result2 = g.kruskal(true);

	    	if (result1.get(0) == -1 && result2.get(0) == -1) System.out.println("Insuficiente");
	    	else if (result1.get(0) != -1 && (result1.get(0) <= result2.get(0) || result2.get(0) == -1)){
	    		System.out.println(result1.get(0));
	    		System.out.println(result1.get(1) + " " + result1.get(2));
	    	}
	    	else {
	    		System.out.println(result2.get(0));
	    		System.out.println(result2.get(1) + " " + result2.get(2));
	    	}
	    }
	    catch (IOException e){}
	}
}