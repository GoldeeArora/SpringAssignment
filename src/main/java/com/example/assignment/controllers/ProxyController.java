package com.example.assignment.controllers;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.objenesis.instantiator.basic.NewInstanceInstantiator;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.client.RestTemplate;

import com.example.assignment.entity.AuthenticationResponse;
import com.example.assignment.entity.CustomerDetails;
import com.example.assignment.entity.ExternalApiRequest;
import com.example.assignment.entity.ExternalApiResponse;
import com.example.assignment.entity.LoginRequest;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.example.assignment.entity.ApiResponse;

@RestController
@RequestMapping("/api")

public class ProxyController {
        public String getToken(String tokenExtraction) {
                int ind = tokenExtraction.indexOf("access_token");
                String ans = "";
                int i = ind + 15;
                while (tokenExtraction.charAt(i) != '}') {
                        ans += tokenExtraction.charAt(i);
                        i++;
                }
                System.out.println(ans.substring(0, ans.length() - 1));
                return ans.substring(0, ans.length() - 1);
        }

        public String getUsefulData(String data) {
                int ind = data.indexOf("[");
                String ans = "";
                int i = ind;
                while (data.charAt(i) != ']') {
                        ans += data.charAt(i);
                        i++;
                }
                ans += data.charAt(i);

                return ans;
        }
        public String getDeleteData(String data)
        {
           int ind = data.indexOf("<");
           int i = ind+1;
           String ans = data.substring(i,i+3);
           return ans;
        }
public String getDataFromcreateCustomer(String data) {
	if(data==null) return null;
	int ind = data.indexOf("<");
	int i = ind + 1;
	return data.substring(i,i+3);
}
        @PostMapping("/authenticate")
        public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
                // Replace LoginRequest with your actual request body class
                // The class should have fields for login_id and password

                // Get the login_id and password from the request body
                String login_id = loginRequest.getLogin_id();
                String password = loginRequest.getPassword();
                // Object loginCredentials = {login_id: "test@sunbasedata.com", password:
                // "Test@123"};

                // Your authentication logic here (e.g., validating credentials against a
                // database)
                // ...

                // Assume the authentication is successful
                String authenticatedUser = loginRequest.getLogin_id(); // Replace with actual authenticated user

                // Make a request to the external API with login_id and password
                String externalApiUrl = "https://qa2.sunbasedata.com/sunbase/portal/api/assignment_auth.jsp"; // Replace
                                                                                                              // with
                                                                                                              // the
                                                                                                              // external
                                                                                                              // API URL
                RestTemplate restTemplate = new RestTemplate();

                // Create the request body to send to the external API
                ExternalApiRequest externalRequest = new ExternalApiRequest(login_id, password);
                System.out.println(externalRequest.getLogin_id());
                System.out.println(externalRequest.getPassword());
                // Make the request to the external API
                String token = null;
                String externalApiResponse = null;
                try {

                        externalApiResponse = restTemplate.postForEntity(externalApiUrl, externalRequest, String.class)
                                        + "";
                        System.out.println(externalApiResponse);
                        token = getToken(externalApiResponse);

                } catch (Exception e) {
                        System.out.println("error is working");
                        System.out.println(e);

                }
                // System.out.println(externalApiResponse);
                // Get the response data from the external API
                // ExternalApiResponse externalData = externalApiResponse.getBody();
                // System.out.println(externalData);
                // Set the 'Access-Control-Allow-Origin' header to allow requests from your
                // frontend domain
                HttpHeaders headers = new HttpHeaders();
                // System.out.println(token);
                headers.set("Access-Control-Allow-Origin", "http://localhost:8080"); // Replace with your frontend
                                                                                     // domain
                ExternalApiResponse response = new ExternalApiResponse(token);
                // Return the data received from the external API along with the authenticated
                // user
                return new ResponseEntity<>(new AuthenticationResponse(authenticatedUser, response), headers,
                                HttpStatus.OK);

        }


        @GetMapping("/getAllCustomers")
        public ResponseEntity<?> getDataWithBearerToken(@RequestHeader("Authorization") String authorizationHeader) {
//                System.out.println("Api for getting all customers is getting called");
                // try {
                // Create the request headers and add the bearer token to the Authorization
                // header
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", authorizationHeader);
//                System.out.println(authorizationHeader);
                // Create the request entity with headers
                HttpEntity<String> requestEntity = new HttpEntity<>(headers);

                // Replace the following URL with the actual URL of the external API
                String apiUrl = "https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=get_customer_list";

                // Make the GET request to the external API
                RestTemplate restTemplate = new RestTemplate();
                String responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, String.class) + "";
//                System.out.println(responseEntity + "line 107");
                // Return the response from the external API as-is or customize it if needed
                responseEntity = getUsefulData(responseEntity);
                return new ResponseEntity<>(new ApiResponse(responseEntity), HttpStatus.OK);

        }

        @PostMapping("/deleteCustomer")
        public ResponseEntity<?> deleteCustomer(@RequestHeader("Authorization") String bearerToken,
                        @RequestParam("uuid") String uuid) {
        	System.out.println("we are calling the delete user method");
        	System.out.println(uuid);
        	System.out.println(bearerToken);
                String apiUrl = "https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=delete&uuid=" + uuid;
                HttpHeaders headers = new HttpHeaders();

                headers.setBearerAuth(bearerToken);
                HttpEntity<String> requestEntity = new HttpEntity<>(headers);
                RestTemplate restTemplate = new RestTemplate();
                String responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, String.class)
                                + "";
//                System.out.println(responseEntity);
                responseEntity = getDeleteData(responseEntity);
                System.out.println(responseEntity);
                if(responseEntity.equals("200"))
                {
                	System.out.println("we deleted successfully");
                	return new ResponseEntity<>(new ApiResponse("We have deleted Successfully"),HttpStatus.OK);
                }
                return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);

        }
        
        @PostMapping("/addCustomer")
        public ResponseEntity<?> addCustomer(@RequestHeader("Authorization") String bearerToken,@RequestBody CustomerDetails customerDetails) {
                // Replace LoginRequest with your actual request body class
                // The class should have fields for login_id and password
System.out.println("we are here to add a new customer");
                // Get the login_id and password from the request body
                String first_name = customerDetails.getFirst_name();
                String last_name = customerDetails.getLast_name();
                String street = customerDetails.getStreet();
                String address = customerDetails.getAddress();
                String city= customerDetails.getCity();
                String state = customerDetails.getState();
                String email = customerDetails.getEmail();
                String phone = customerDetails.getPhone();
              

                // Make a request to the external API with login_id and password
                String apiUrl = "https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=create"; // Replace
                                                                                                              // with
                HttpHeaders headers = new HttpHeaders();                                                                                           // the
                headers.setBearerAuth(bearerToken);    
                Map<String, Object> requestBodyMap = new HashMap<>();
                requestBodyMap.put("first_name", first_name);
                requestBodyMap.put("last_name", last_name);
                requestBodyMap.put("street", street);
                requestBodyMap.put("address", address);
                requestBodyMap.put("city", city);
                requestBodyMap.put("state", state);
                requestBodyMap.put("email", email);
                requestBodyMap.put("phone", phone);

              System.out.println(requestBodyMap);
                
                
//             
                HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBodyMap, headers);                                                                          // API URL
                RestTemplate restTemplate = new RestTemplate();
//
//                // Create the request body to send to the external API
//
//                // Make the request to the external API
//                
                String externalApiResponse = null;
                try {

                        externalApiResponse = restTemplate.exchange(apiUrl,HttpMethod.POST, requestEntity, String.class)
                                        + "";
                        System.out.println(externalApiResponse);
                       

                } catch (Exception e) {
                        System.out.println("error is working");
                        System.out.println(e);

                }
                 System.out.println(externalApiResponse);
                 externalApiResponse = getDataFromcreateCustomer(externalApiResponse);
                 if(externalApiResponse != null && externalApiResponse.equals("201"))
                 {
                	 return new ResponseEntity<>(new ApiResponse("Customer has been created"),HttpStatus.CREATED); 
                 }
                 return new ResponseEntity<>(new ApiResponse("Customer couldn't be created"),HttpStatus.INTERNAL_SERVER_ERROR);

   

        }
        @PostMapping("/updateCustomer")
        public ResponseEntity<?> updateCustomer(@RequestParam("uuid") String uuid,@RequestHeader("Authorization") String bearerToken,@RequestBody CustomerDetails customerDetails) {
                // Replace LoginRequest with your actual request body class
                // The class should have fields for login_id and password
System.out.println("we are here to update our  customer");
                // Get the login_id and password from the request body
                String first_name = customerDetails.getFirst_name();
                String last_name = customerDetails.getLast_name();
                String street = customerDetails.getStreet();
                String address = customerDetails.getAddress();
                String city= customerDetails.getCity();
                String state = customerDetails.getState();
                String email = customerDetails.getEmail();
                String phone = customerDetails.getPhone();
              

        
                String apiUrl = "https://qa2.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=update&uuid=" + uuid; 
                HttpHeaders headers = new HttpHeaders();                                                                                           // the
                headers.setBearerAuth(bearerToken);    
                Map<String, Object> requestBodyMap = new HashMap<>();
                requestBodyMap.put("first_name", first_name);
                requestBodyMap.put("last_name", last_name);
                requestBodyMap.put("street", street);
                requestBodyMap.put("address", address);
                requestBodyMap.put("city", city);
                requestBodyMap.put("state", state);
                requestBodyMap.put("email", email);
                requestBodyMap.put("phone", phone);

              System.out.println(requestBodyMap);
                System.out.println(uuid);
                System.out.println(bearerToken);
                
          
                HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBodyMap, headers);                                                                          // API URL
                RestTemplate restTemplate = new RestTemplate();
           
                String externalApiResponse = null;
               

                        externalApiResponse = restTemplate.exchange(apiUrl,HttpMethod.POST, requestEntity, String.class)
                                        + "";
                        System.out.println(externalApiResponse);
                       

//                } catch (Exception e) {
//                        System.out.println("error is working");
//                        System.out.println(e);
//
//                }
                 System.out.println(externalApiResponse);
                 externalApiResponse = getDataFromcreateCustomer(externalApiResponse);
                 if(externalApiResponse!=null && externalApiResponse.equals("200"))
                 {
                	 return new ResponseEntity<>(new ApiResponse("Customer has been updated"),HttpStatus.CREATED); 
                 }
                 return null;
//                 return new ResponseEntity<>(new ApiResponse("Customer couldn't be created"),HttpStatus.INTERNAL_SERVER_ERROR);

   

        }

}
