package it.polito.tdp.food.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.food.model.Adiacenza;
import it.polito.tdp.food.model.Condiment;
import it.polito.tdp.food.model.Food;
import it.polito.tdp.food.model.Portion;

public class FoodDao {
	public List<Food> listAllFoods(){
		String sql = "SELECT * FROM food" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Food> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Food(res.getInt("food_code"),
							res.getString("display_name")
							));
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}

	}
	
	public List<Condiment> listAllCondiments(){
		String sql = "SELECT * FROM condiment" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Condiment> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Condiment(res.getInt("condiment_code"),
							res.getString("display_name"),
							res.getDouble("condiment_calories"), 
							res.getDouble("condiment_saturated_fats")
							));
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Portion> listAllPortions(){
		String sql = "SELECT * FROM portion" ;
		try {
			Connection conn = DBConnect.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			List<Portion> list = new ArrayList<>() ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Portion(res.getInt("portion_id"),
							res.getDouble("portion_amount"),
							res.getString("portion_display_name"), 
							res.getDouble("calories"),
							res.getDouble("saturated_fats"),
							res.getInt("food_code")
							));
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			e.printStackTrace();
			return null ;
		}

	}
	
	public void listFoods(Map<Integer, Food> idMap,Integer portions) {
		String sql = "SELECT f.food_code AS fc,f.display_name AS ds,COUNT(*) AS c " + 
				"FROM `portion` p,food f " + 
				"WHERE p.food_code=f.food_code " + 
				"GROUP BY f.food_code " + 
				"HAVING COUNT(*)=?";

		Connection conn = DBConnect.getConnection() ;
		try {
			
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, portions);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				
				if(!idMap.containsKey(rs.getInt("fc"))) {
					Food food = new Food(rs.getInt("fc"), rs.getString("ds"));
					idMap.put(food.getFood_code(), food);
				}
			}

			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<Adiacenza> getAd(Map<Integer, Food> idMap) {
		String sql = "SELECT p1.food_code AS food1,p2.food_code AS food2,AVG(c.condiment_calories) AS media " + 
				"FROM `portion` p1,`portion` p2,food_condiment f1,food_condiment f2,condiment c " + 
				"WHERE p1.food_code=f1.food_code AND p2.food_code=f2.food_code AND " + 
				"f1.condiment_code=f2.condiment_code AND c.condiment_code=f1.condiment_code " + 
				"AND f1.id> f2.id AND p1.food_code<> p2.food_code AND p1.portion_id> p2.portion_id " + 
				"GROUP BY p1.food_code,p2.food_code";
		List<Adiacenza> result = new ArrayList<Adiacenza>();
		Connection conn = DBConnect.getConnection() ;

		try {
			
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			
			while(rs.next()) {
				Food sorgente = idMap.get(rs.getInt("food1"));
				Food destinazione = idMap.get(rs.getInt("food2"));
				
				if(sorgente != null && destinazione != null) {
					result.add(new Adiacenza(sorgente, destinazione, rs.getDouble("media")));
				} 

			}
			conn.close();
		}catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
		return result;
	}
}
