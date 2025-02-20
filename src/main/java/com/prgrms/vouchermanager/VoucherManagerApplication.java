package com.prgrms.vouchermanager;

import com.prgrms.vouchermanager.exception.MyException;
import com.prgrms.vouchermanager.handler.CommandHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class VoucherManagerApplication implements CommandLineRunner {

    private final CommandHandler commandHandler;

    @Autowired
    public VoucherManagerApplication(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    public static void main(String[] args) {
        SpringApplication.run(VoucherManagerApplication.class, args);
    }

    @Override
    public void run(String... args) {
            while(true){
                boolean flag = true;
                try {
                    flag = commandHandler.selectProgram();
                } catch (MyException e) {
                    log.error(e.getMessage());
                    System.out.println(e.consoleMessage());
                }
                if(!flag) break;
            }
    }
}
