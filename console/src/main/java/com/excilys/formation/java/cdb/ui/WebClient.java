package com.excilys.formation.java.cdb.ui;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class WebClient {

	public static void main(String[] args) {

		getRequest();
	}

	public static void getRequest() {
		Client client = Client.create();
		// set the appropriate URL
		String getUrl = "http://localhost:8080/webapp/computers/1";
		WebResource webResource = client.resource(getUrl);
		ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
		// a successful response returns 200
		if (response.getStatus() != 200) {
			throw new RuntimeException("HTTP Error: " + response.getStatus());
		}

		String result = response.getEntity(String.class);
		System.out.println("Response from the Server: ");
		System.out.println(result);
	}

}
