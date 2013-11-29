package com.gsms.geocoding;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import com.gsms.db.DBTool;

public class GUI extends JFrame {

	public GUI() {
		this.setTitle("地址转经纬度");
		this.setSize(300, 200);
		this.setLocation(getWidth()/2, getWidth()/2);
		this.setVisible(true);
	}

	public static void main(String[] args) {

		GUI gui = new GUI();
		JButton start = new JButton("开始");
		start.setVisible(true);
		JButton end = new JButton("退出");
		end.setVisible(true);
		gui.add(start,BorderLayout.NORTH);
		gui.add(end,BorderLayout.SOUTH);
		start.addActionListener(listener);
		end.addActionListener(listener);

	}

	public static ActionListener listener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if (e.getActionCommand().equals("开始")) {
				JFileChooser chooser = new JFileChooser();
				int result = chooser.showDialog(null, "保存文件");
				File file = null;
				if (result == JFileChooser.APPROVE_OPTION) {
					file = chooser.getSelectedFile();
				}

				try {
					convert(file);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}else {
				System.exit(0);
			}
			
			
		}

		private void convert(File file) throws Exception {
			// TODO Auto-generated method stub
			System.setOut(new PrintStream(file));

			String address = "";
			int size = getAddress().size();
			for (int i = 0; i < size; i++) {
				address = getAddress().get(i);
				System.out.print("Address: " + address);
				address = address.replaceAll(" ", "+");
				String url = "http://maps.googleapis.com/maps/api/geocode/json?address="
						+ address + "&sensor=true";
				// String url =
				// "http://maps.googleapis.com/maps/api/geocode/json?latlng=36.780779,119.963751&sensor=true&language=zh_cn";
				CloseableHttpClient httpClient = HttpClients.createDefault();
				HttpGet httpGet = new HttpGet(url);

				HttpResponse response = httpClient.execute(httpGet);

				HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream inStream = entity.getContent();
					String respstr = convertStreamToString(inStream);
					// System.out.println(respstr);

					// json数据解析
					JSONObject jsonObject = new JSONObject(respstr);
					JSONArray results = jsonObject.getJSONArray("results");
					int count = results.length();
					// System.out.println(count);
					JSONObject resultObject = results.getJSONObject(0);
					JSONObject geometry = resultObject
							.getJSONObject("geometry");
					JSONObject location = geometry.getJSONObject("location");
					String lat = location.getString("lat");
					String lng = location.getString("lng");

					System.out.println("  lat: " + lat + " lng: " + lng);

				}
				httpClient.close();
			}
		}

	};

	private static String convertStreamToString(InputStream is)
			throws Exception {
		// TODO Auto-generated method stub

		InputStreamReader isr = new InputStreamReader(is, "UTF-8"); // 编码转换
		BufferedReader reader = new BufferedReader(isr);

		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		is.close();
		return sb.toString();
	}

	private static ArrayList<String> getAddress() throws Exception {
		DBTool dbTool = new DBTool();
		Connection connection = dbTool.connect();
		Statement stmt = null;
		ResultSet rs = null;
		stmt = connection.createStatement();
		String sql = "select * from Address";
		rs = stmt.executeQuery(sql);
		ArrayList<String> addressList = new ArrayList<String>();
		while (rs.next()) {
			StringBuilder address = new StringBuilder();
			String country = rs.getString("country");
			String adminarea = rs.getString("admin_area_level_1");
			String locality = rs.getString("locality");
			String sublocality = rs.getString("sublocality");
			String neighborhood = rs.getString("neighborhood");
			String route = rs.getString("route");
			String street_number = rs.getString("street_number");
			// String postal_code = rs.getString("postal_code");
			address.append(format(country)).append(format(adminarea))
					.append(format(locality)).append(format(sublocality))
					.append(format(neighborhood)).append(format(route))
					.append(format(street_number));
			addressList.add(address.toString());

		}
		return addressList;
	}

	private static String format(String str) {

		return (str == null) ? "" : str;
	}
}
