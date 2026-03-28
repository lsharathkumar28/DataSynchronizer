package com.outreach.datasynchronizer;

import org.springframework.boot.SpringApplication;

public class TestDataSynchronizerApplication {

    public static void main(String[] args) {
        SpringApplication.from(DataSynchronizerApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
