package com.example.demo.service;

import com.example.demo.dto.SolutionRequest;
import com.example.demo.dto.WebhookRequest;
import com.example.demo.dto.WebhookResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WebhookService {

    @Autowired
    private RestTemplate restTemplate;

    private static final String GENERATE_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
    private static final String SUBMIT_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

    // SQL query for odd registration number
    private static final String SQL_QUERY = "SELECT d.DEPARTMENT_NAME, t.SALARY, CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS EMPLOYEE_NAME, TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE FROM (SELECT p.EMP_ID, SUM(p.AMOUNT) AS SALARY FROM PAYMENTS p WHERE DAY(p.PAYMENT_TIME) <> 1 GROUP BY p.EMP_ID) t JOIN EMPLOYEE e ON t.EMP_ID = e.EMP_ID JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID JOIN (SELECT e2.DEPARTMENT, MAX(s.TotalSalary) AS MaxSalary FROM (SELECT p2.EMP_ID, SUM(p2.AMOUNT) AS TotalSalary FROM PAYMENTS p2 WHERE DAY(p2.PAYMENT_TIME) <> 1 GROUP BY p2.EMP_ID) s JOIN EMPLOYEE e2 ON s.EMP_ID = e2.EMP_ID GROUP BY e2.DEPARTMENT) x ON x.DEPARTMENT = e.DEPARTMENT AND x.MaxSalary = t.SALARY;";

    public void executeWorkflow() {
        try {
            // Step 1: Generate webhook
            System.out.println("Step 1: Generating webhook...");
            WebhookRequest request = new WebhookRequest("Markandey Vatsa", "22BCE7577", "vatsamarkandeya@gmail.com");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<WebhookRequest> requestEntity = new HttpEntity<>(request, headers);

            WebhookResponse response = restTemplate.postForObject(
                GENERATE_WEBHOOK_URL,
                requestEntity,
                WebhookResponse.class
            );

            if (response != null) {
                System.out.println("Webhook URL received: " + response.getWebhook());
                System.out.println("Access Token received: " + response.getAccessToken());

                // Step 2: Determine which SQL query to use based on registration number
                // REG12347 ends with 7 (odd), so using the provided SQL query
                System.out.println("\nStep 2: Using SQL query for odd registration number");

                // Step 3: Submit the solution
                System.out.println("\nStep 3: Submitting solution to webhook...");
                submitSolution(response.getAccessToken(), SQL_QUERY);

                System.out.println("Workflow completed successfully!");
            } else {
                System.err.println("Failed to generate webhook - no response received");
            }

        } catch (Exception e) {
            System.err.println("Error during workflow execution: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void submitSolution(String accessToken, String sqlQuery) {
        try {
            SolutionRequest solutionRequest = new SolutionRequest(sqlQuery);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", accessToken);

            HttpEntity<SolutionRequest> requestEntity = new HttpEntity<>(solutionRequest, headers);

            String response = restTemplate.postForObject(
                SUBMIT_WEBHOOK_URL,
                requestEntity,
                String.class
            );

            System.out.println("Solution submitted successfully!");
            System.out.println("Response: " + response);

        } catch (Exception e) {
            System.err.println("Error submitting solution: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

