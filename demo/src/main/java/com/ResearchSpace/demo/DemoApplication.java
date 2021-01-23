package com.ResearchSpace.demo;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//Set up the research space end point
		RestTemplate restTemplate = new RestTemplate();
		final String baseUrl = "https://demos.researchspace.com/api/inventory/v1/samples?pageNumber=0&pageSize=20&orderBy=name%20asc";
		URI uri = new URI(baseUrl);
		HttpHeaders headers = new HttpHeaders();
		headers.set("apiKey", "810O06lbnJ1Qv0hwYLQYhyaLNMSzD8WE");  
		HttpEntity<String> requestEntity = new HttpEntity<>(headers);
		
		//Send request to the end point
		ResponseEntity<String> result;
		try {
			result = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, String.class);
		}
		catch (Exception e) {
			result = null;
		}
		
		//Interpret the data received from the API
		if (result != null) {
			JSONParser parser = new JSONParser(); 
			JSONObject json = (JSONObject) parser.parse(result.getBody());
			JSONArray samples =(JSONArray) json.get("samples");
			samples.forEach(j -> printSample((JSONObject) j));
		}
	}
	
	private void printSample(JSONObject sample_json) {
		
		//New lines
		for (int i  = 0; i < 2; i++)
			System.out.println();
		
		//Add a warning label to samples that are close to expiry
		String expiryDate = (String) sample_json.get("expiryDate");
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		try {
			c.setTime(dateFormater.parse(expiryDate));
			c.add(Calendar.DATE, -7);
			if (c.getTime().after(Calendar.getInstance().getTime())) 
				System.out.println("*WARNING - SAMPLE EXPIRES IN LESS THAN 7 DAYS*");
		} catch (ParseException e) {
			System.out.println("(Unable to parse expiry date)");
		}

		//Print out some info about the samples
		System.out.println(sample_json.get("name"));
		System.out.println("Description : " + sample_json.get("description"));
		System.out.println("ID : " + sample_json.get("id"));
		System.out.println("Global ID : " + sample_json.get("globalId"));
		System.out.println("Created : " + sample_json.get("created"));
		System.out.println("Expiry Date : " + expiryDate);
		
		//Print out info inside nested JSON objects
		JSONObject owner = (JSONObject) sample_json.get("owner");
		System.out.println("Owner Email : " + owner.get("email"));
		JSONObject quantity = (JSONObject) sample_json.get("quantity");
		System.out.println("Quantity : " + quantity.get("numericValue"));
		JSONObject storageTempMin = (JSONObject) sample_json.get("storageTempMin");
		System.out.println("Storage Temperature Min : " + storageTempMin.get("numericValue"));
		JSONObject storageTempMax = (JSONObject) sample_json.get("storageTempMax");
		System.out.println("Storage Temperature Max : " + storageTempMax.get("numericValue"));
	}

	
	
}
