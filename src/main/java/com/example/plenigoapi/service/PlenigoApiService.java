package com.example.plenigoapi.service;

import com.example.plenigoapi.model.CsvItem;
import com.example.plenigoapi.util.CsvUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PlenigoApiService {

    @Value("${api.url}")
    private String apiUrl;

    @Value("${api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CsvUtil csvUtil;

    private static final int MAX_ORDERS_PER_REQUEST = 100;

    public void processOrders(int numberOfOrders) {

        List<CsvItem> csvItems = new ArrayList<>();
        List<Map<String, Object>> orders = fetchOrders(numberOfOrders);

        System.out.println("Processing orders...");

        for (Map<String, Object> order : orders) {
            int orderId = (int) order.get("orderId");
            String customerId = (String) order.get("invoiceCustomerId");

            List<Map<String, Object>> items = (List<Map<String, Object>>) order.get("items");
            for (Map<String, Object> item : items) {
                CsvItem csvItem = new CsvItem();
                csvItem.setOrderId(orderId);
                csvItem.setInvoiceCustomerId(customerId);
                csvItem.setTitle((String) item.get("title"));
                if (item.get("price") instanceof Double) {
                    csvItem.setPrice((Double) item.get("price"));
                }
                csvItem.setTax(Double.parseDouble(item.get("tax").toString()));
                csvItem.setPosition((Integer) item.get("position"));

                fetchAdditionalData(csvItem, orderId, customerId);
                csvItems.add(csvItem);
            }
        }
        csvUtil.writeToCsv(csvItems);
    }

    private List<Map<String, Object>> fetchOrders(int count) {

        List<Map<String, Object>> allOrders = new ArrayList<>();
        String startingAfter = null;
        int ordersToGo = count;
        int size = MAX_ORDERS_PER_REQUEST;

        int numberOfCalls = (int) Math.ceil((double) count / 100);

        for (int i = 0; i < numberOfCalls; i++) {

            String url = apiUrl + "/orders?size=" + size;

            if (startingAfter != null) {
                url += "&startingAfter=" + startingAfter;
            }

            RequestEntity<Void> request = RequestEntity.get(URI.create(url))
                    .header("X-plenigo-token", apiKey)
                    .build();
            ResponseEntity<String> response = restTemplate.exchange(request, String.class);
            ObjectMapper mapper = new ObjectMapper();
            String json = response.getBody();

            JsonNode rootNode;
            try {
                rootNode = mapper.readTree(json);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            JsonNode itemsNode = rootNode.path("items");
            List<Map<String, Object>> itemsList = new ArrayList<>();
            if (itemsNode.isArray()) {
                for (JsonNode itemNode : itemsNode) {
                    itemsList.add(mapper.convertValue(itemNode, Map.class));
                }
                System.out.println("Fetched " + itemsList.size() + " orders." );
                allOrders.addAll(itemsList);
            }

            JsonNode startingAfterNode = rootNode.path("startingAfterId");
            startingAfter = mapper.convertValue(startingAfterNode, String.class);

            ordersToGo = ordersToGo - MAX_ORDERS_PER_REQUEST;

            if(ordersToGo < MAX_ORDERS_PER_REQUEST){
                size = ordersToGo;
            }
        }
        return allOrders;
    }

    private void fetchAdditionalData(CsvItem csvItem, int orderId, String customerId) {
        RequestEntity<Void> requestInvoice = RequestEntity.get(URI.create(apiUrl + "/invoices?orderId=" + orderId))
                .header("X-plenigo-token", apiKey)
                .build();
        ResponseEntity<Map> responseInvoice = restTemplate.exchange(requestInvoice, Map.class);

        Map<String, Object> responseBody = responseInvoice.getBody();

        if (responseBody != null) {
            List<Map<String, Object>> items = (List<Map<String, Object>>) responseBody.get("items");

            if (items != null && !items.isEmpty()) {
                // since it wasn't clear in the task, I decided to use only first invoice if there are more
                // in RL situation I'd ask someone in charge

//                for (Map<String, Object> item : items) {
//                    System.out.println("number of invoices for orderId " + orderId + " is " + items.size());
//                    Integer invoiceId = (Integer) item.get("invoiceId");
//                    String invoiceDate = (String) item.get("invoiceDate");
//
//                    System.out.println("Invoice ID: " + invoiceId);
//                    System.out.println("Invoice Date: " + invoiceDate);
//                }
                csvItem.setInvoiceId((Integer) items.get(0).get("invoiceId"));
                csvItem.setInvoiceDate((String) items.get(0).get("invoiceDate"));
            }
        }

        RequestEntity<Void> requestCustomer = RequestEntity.get(URI.create(apiUrl + "/customers/" + customerId))
                .header("X-plenigo-token", apiKey)
                .build();

        ResponseEntity<Map> responseCustomer = restTemplate.exchange(requestCustomer, Map.class);
        Map<String, Object> customer = responseCustomer.getBody();

        csvItem.setEmail((String) customer.get("email"));
        csvItem.setCreatedDate((String) customer.get("createdDate"));
    }
}
