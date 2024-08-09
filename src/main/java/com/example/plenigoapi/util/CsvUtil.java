package com.example.plenigoapi.util;

import com.example.plenigoapi.model.CsvItem;
import org.springframework.stereotype.Component;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVFormat;


import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Component
public class CsvUtil {
    public void writeToCsv(List<CsvItem> csvItems) {
        try (FileWriter writer = new FileWriter("orders.csv");
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
                     "OrderID",
                     "Position",
                     "Title",
                     "Price",
                     "Tax",
                     "CustomerID",
                     "CustomerEmail",
                     "CustomerCreationDate",
                     "InvoiceID",
                     "InvoiceDate"
             ))) {
            for (CsvItem item : csvItems) {
                csvPrinter.printRecord(
                        item.getOrderId(),
                        item.getPosition(),
                        item.getTitle(),
                        item.getPrice(),
                        item.getTax(),
                        item.getInvoiceCustomerId(),
                        item.getEmail(),
                        item.getCreatedDate(),
                        item.getInvoiceId(),
                        item.getInvoiceDate());
            }
            csvPrinter.flush();
            System.out.println("File orders.csv successfully created.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
