package com.tn76.BusTicketBooking.utils;

/**
 * DataFlowUtils.java
 *
 * @author Mohamed Subaideen Imran A (mohamedsubaideenimran@nmsworks.co.in)
 * @module in.co.nmsworks.wisdom.em.utils
 * @created Aug 07, 2023
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

import static com.tn76.BusTicketBooking.utils.DataFlowStatus.*;

public class DataFlowUtils {

    private final String dataFlowServerBaseUrl = "http://localhost:9393"; // Replace with your Data Flow Server's URL
    private final String appsEndpoint = dataFlowServerBaseUrl + "/runtime/apps";

    public Map<String, DataFlowStatus> checkAllDataFlowsStatus() throws JSONException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(appsEndpoint, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String responseBody = response.getBody();
            return extractDataFlowStatuses(responseBody);
        }

        return null; // Handle error case
    }

    private Map<String, DataFlowStatus> extractDataFlowStatuses(String responseBody) {
        Map<String, DataFlowStatus> dataFlowStatuses = new HashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode appStatusResourceList = rootNode.path("_embedded").path("appStatusResourceList");

            for (JsonNode appNode : appStatusResourceList)
            {
                String name = appNode.path("name").asText();
                String state = appNode.path("state").asText();

                DataFlowStatus status = UNKNOWN;
                if ("deployed".equalsIgnoreCase(state)) {
                    status = DEPLOYED;
                } else if ("deploying".equalsIgnoreCase(state)) {
                    status = DEPLOYING;
                }else if ("partial".equalsIgnoreCase(state)) {
                    status = PARTIAL;
                }else if ("fail".equalsIgnoreCase(state)) {
                    status = FAILED;
                }

                dataFlowStatuses.put(name, status);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("dataFlowStatuses = " + dataFlowStatuses);
        return dataFlowStatuses;
    }

    public static boolean checkingDataFlowStatus() throws JSONException, JsonProcessingException {
        DataFlowUtils dataFlowUtils = new DataFlowUtils();
        Map<String, DataFlowStatus> dataFlowStatuses = dataFlowUtils.checkAllDataFlowsStatus();
        List<String> apps = getApps();
        boolean b = new HashSet<>(dataFlowStatuses.keySet()).containsAll(apps);

        if (b)
        {
            System.out.println(" inside b = " +b);
            for (Map.Entry<String, DataFlowStatus> entry : dataFlowStatuses.entrySet()) {
                DataFlowStatus state = entry.getValue();
                System.out.println("name : " + entry.getKey() + " && " + " state = " + state);
                if (state != DEPLOYED)
                    return false;
            }
            return true;
        }
        return false;
    }

    private static void calling() throws JSONException, JsonProcessingException, InterruptedException {
        int count = 1;
        while (true)
        {
            if (count > 30)
                break;
            System.out.println("<---------------- CHECKING DATAFLOW STATUS -----------------> ");
            boolean status = checkingDataFlowStatus();
            System.out.println(count +"th Time Status = " + status);
            if (status)
                break;
            count++;
            Thread.sleep(10000);
        }
    }
    public static void main(String[] args) throws JSONException, JsonProcessingException, InterruptedException {
        checkBooking();
    }

    private static List<String> getApps() throws JsonProcessingException {
        String url = "http://localhost:9393/apps";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        List<String> names = new ArrayList<>();
        if (response.getStatusCode().is2xxSuccessful())
        {
            String responseBody = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                JsonNode rootNode = objectMapper.readTree(responseBody);
                JsonNode appStatusResourceList = rootNode.path("_embedded").path("appRegistrationResourceList");

                for (JsonNode appNode : appStatusResourceList) {
                    String name = appNode.path("name").asText();
                    if (!name.startsWith("icmp")) {
                        names.add(name);
                    }
                }

                System.out.println("names = " + names);
            }
            catch (Exception e)
            {
                System.out.println("e = " + e);
            }

        }
        return names;
    }

    private static void checkBooking() {
        String url = "https://in.bookmyshow.com/buytickets/inox-the-marina-mall-omr/cinema-chen-INTO-MT/20230813";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        System.out.println("response = " + response);
        List<String> names = new ArrayList<>();
        if (response.getStatusCode().is2xxSuccessful()) {
            String responseBody = response.getBody();
            System.out.println("responseBody = " + responseBody);
            ObjectMapper objectMapper = new ObjectMapper();

            try {
                JsonNode rootNode = objectMapper.readTree(responseBody);
                JsonNode appStatusResourceList = rootNode.path("_embedded").path("appRegistrationResourceList");

                for (JsonNode appNode : appStatusResourceList) {
                    String name = appNode.path("name").asText();
                    if (!name.startsWith("icmp")) {
                        names.add(name);
                    }
                }

                System.out.println("names = " + names);
            } catch (Exception e) {
                System.out.println("e = " + e);
            }

        }
    }
}

enum DataFlowStatus {
    DEPLOYED,
    DEPLOYING,

    PARTIAL,
    FAILED,
    UNKNOWN // Add more statuses as needed
}

