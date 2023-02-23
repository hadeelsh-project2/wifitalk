package sa.edu.ksu.pnes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import sa.edu.ksu.pnes.snort.CommandRunner;
import sa.edu.ksu.pnes.snort.NetworkScanner;

@SpringBootApplication
public class PnesApplication {

	@Value("${snort.command}")
	private String snortCommand;

	@Value("${snort.options}")
	private String snortOptions;

	@Value("${snort.log}")
	private String snortLogFile;

	@Value("${snort.interface}")
	private String netInterface;

	private Process snortProcess;
	private ExecutorService logService;
	private NetworkScanner netScanner;


	public static void main(String[] args) {
		SpringApplication.run(PnesApplication.class, args);
	}

	@PostConstruct
	public void initProcesses() {
		try {
			this.checkLogFiles();
			this.snortProcess = CommandRunner.startCommand(snortCommand, snortOptions, snortLogFile);

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	@PreDestroy
	public void stopProcesses() {

		CommandRunner.shutDown(snortProcess);
		if (this.logService != null) {
			logService.shutdownNow();
		}
		if (this.netScanner != null) {
			this.netScanner.shutdownProcess();
		}

	}

	private void checkLogFiles() {
		try {
			if (!Files.exists(Paths.get(snortLogFile))) {

				Files.createFile(Paths.get(snortLogFile));

			} else {
				Files.delete(Paths.get(snortLogFile));
				Files.createFile(Paths.get(snortLogFile));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Bean
	@Scope("singleton")
	public NetworkScanner pnesNetworkScanner() {
		this.netScanner = new NetworkScanner(this.netInterface);

		Executors.newSingleThreadExecutor().execute(new Runnable() {
			public void run() {
				try {
					netScanner.startProcess();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return this.netScanner;
	}
	
	@Bean
	@Scope("singleton")
	public CurrentUserType currentUserType()
	{
		return new CurrentUserType();
	}

}
