package it.polito.tdp.food.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.food.db.FoodDao;

public class Model {
	private Graph<Food, DefaultWeightedEdge> grafo;
	private Map<Integer, Food> idMap;
	
	
	public Model() {
		idMap = new HashMap<Integer,Food>();
		
	}
	
	public void creaGrafo(Integer portions) {
		
		FoodDao dao = new FoodDao();
		dao.listFoods(idMap,portions);
		
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//Aggiungere i vertici
		Graphs.addAllVertices(this.grafo, idMap.values());
		
		//Aggiungere gli archi
		
		for(Adiacenza a : dao.getAd(idMap)) {
			if(a.getPeso()!=0) {
				Graphs.addEdge(grafo, a.getF1(), a.getF2(), a.getPeso());
			}
		}

	}
	
	public List<Food> getFoods(){
		List<Food> lista = new ArrayList<>(this.grafo.vertexSet());
		Collections.sort(lista);
		return lista;
	}
	
	public List<Food> trovaViciniAdiacenti(Food c){
		List<Food> vicini = Graphs.neighborListOf(this.grafo, c);
		return vicini;
	}

	public int nVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public static void main(String args[]) {
		Model m = new Model();
		m.creaGrafo(3);
		Food d = new Food(14109030,"");
		System.out.println(m.trovaViciniAdiacenti(d));
		}
}
