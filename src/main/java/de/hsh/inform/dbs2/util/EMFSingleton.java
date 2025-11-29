package de.hsh.inform.dbs2.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class EMFSingleton {
	
	private static EntityManagerFactory emf;
	
	static {
		emf = null;
		try (InputStream input = new FileInputStream("db.properties")) {
			Properties props = new Properties();
			props.load(input);

			Map<String, String> overrideProps = new HashMap<>();
			overrideProps.put("jakarta.persistence.jdbc.url", props.getProperty("db.url"));
			overrideProps.put("jakarta.persistence.jdbc.user", props.getProperty("db.user"));
			overrideProps.put("jakarta.persistence.jdbc.password", props.getProperty("db.password"));

			emf = Persistence.createEntityManagerFactory("movie", overrideProps);
		} catch (Exception e) {
			System.err.println("Error while connecting to database");
			e.printStackTrace();
			System.exit(1);
		} 
	}

	public static EntityManagerFactory getEntityManagerFactory() {
		return emf;
	}
}
