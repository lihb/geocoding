package com.gsms.geocoding;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import com.gsms.db.DBTool;


public class AddressToCoord {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		System.setOut(new PrintStream(new File("d:\\addressAndCoord.txt")));
		
		String address="";
		int size = getAddress().size();
		List< String> list = getAddress();
		for (int i = 0; i < size; i++) {
			address = list.get(i);
			System.out.print("Address: "+address);
			address = address.replaceAll(" ", "+");
			String url = "http://maps.googleapis.com/maps/api/geocode/json?address="+ address + "&sensor=true";
			//String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=36.780779,119.963751&sensor=true&language=zh_cn";
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(url);

			HttpResponse response = httpClient.execute(httpGet);
			
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inStream = entity.getContent();
				String respstr = convertStreamToString(inStream);
				System.out.println(respstr);
				
				//json数据解析
				JSONObject jsonObject = new JSONObject(respstr);
				JSONArray results = jsonObject.getJSONArray("results");
				int count = results.length();
				//System.out.println(count);
				JSONObject resultObject = results.getJSONObject(0);
				JSONObject geometry = resultObject.getJSONObject("geometry");
				JSONObject location = geometry.getJSONObject("location");
				String lat = location.getString("lat");
				String lng = location.getString("lng");
				//插入数据库
				Connection conn = DBTool.connect();
				String sql = " UPDATE MAP_CITY set GOOGLE_LNG="+lng+ "and set GOOGLE_LAT="+lat+" where CITY_NAME="+address+";";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, lng);
				pstmt.setString(2, lat);
				pstmt.execute();
				System.out.println("  lat: "+lat+" lng: "+lng);
				
			}
			//httpClient.close();
		}
		
	}

	private static String convertStreamToString(InputStream is) throws Exception {
		// TODO Auto-generated method stub

		InputStreamReader isr = new InputStreamReader(is, "UTF-8"); //编码转换
		BufferedReader reader = new BufferedReader(isr);

		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		is.close();
		return sb.toString();
	}
	
	private static ArrayList<String> getAddress() throws Exception{
		DBTool dbTool = new DBTool();
		Connection connection = dbTool.connect();
		Statement stmt = null;
		ResultSet rs = null;
		stmt = connection.createStatement();
		String sql = "select * from MAP_CITY";
		rs = stmt.executeQuery(sql);
		ArrayList<String> addressList = new ArrayList<String>();
		while (rs.next()) {
			StringBuilder address = new StringBuilder();
			String city_name = rs.getString("CITY_NAME");
			/*String country = rs.getString("country");
			String adminarea = rs.getString("admin_area_level_1");
			String locality = rs.getString("locality");
			String sublocality = rs.getString("sublocality");
			String neighborhood = rs.getString("neighborhood");
			String route = rs.getString("route");
			String street_number = rs.getString("street_number");
			//String postal_code = rs.getString("postal_code");
			address.append(format(country)).append(format(adminarea)).append(format(locality)).append(format(sublocality))
			.append(format(neighborhood)).append(format(route)).append(format(street_number));
			addressList.add(address.toString());*/
			address.append(city_name);
			addressList.add(address.toString());
		}
		return addressList;
	}
	
	private static String format(String str) {
		
		return (str==null)?"":str;
	}
	

}
