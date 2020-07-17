package com.diegogocode.springdata.app;

import com.diegogocode.springdata.app.services.IUploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringDataJpaApplication implements CommandLineRunner {

	@Autowired
	private IUploadFileService iUploadFileService;

	public static void main(String[] args) {
		SpringApplication.run(SpringDataJpaApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		iUploadFileService.deleteAll();
		iUploadFileService.init();
	}
}
