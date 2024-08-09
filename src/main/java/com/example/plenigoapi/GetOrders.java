package com.example.plenigoapi;

import com.example.plenigoapi.service.PlenigoApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class GetOrders {

    @Autowired
    private PlenigoApiService plenigoApiService;

    @ShellMethod(key="orders", value="Fetch --n number of orders, get customer and invoice data and create csv file")
    public void getOrders(@ShellOption(value="--n", defaultValue = "300") int numberOfOrders) {
        plenigoApiService.processOrders(numberOfOrders);
    }
}
