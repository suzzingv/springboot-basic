package com.prgrms.vouchermanager.handler.executor;

import com.prgrms.vouchermanager.controller.WalletController;
import com.prgrms.vouchermanager.domain.customer.Customer;
import com.prgrms.vouchermanager.domain.voucher.Voucher;
import com.prgrms.vouchermanager.exception.EmptyListException;
import com.prgrms.vouchermanager.exception.NotCorrectIdException;
import com.prgrms.vouchermanager.io.ConsolePrint;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import static com.prgrms.vouchermanager.message.ConsoleMessage.*;

@Component
public class WalletExecutor {

    private final ConsolePrint consolePrint;
    private final WalletController controller;
    private final Scanner sc = new Scanner(System.in);

    public WalletExecutor(ConsolePrint consolePrint, WalletController controller) {
        this.consolePrint = consolePrint;
        this.controller = controller;
    }
    public void create() {
        try {
            consolePrint.printMessage(GET_CUSTOMER_ID.getMessage());
            UUID customerId = UUID.fromString(sc.nextLine());
            consolePrint.printMessage(GET_VOUCHER_ID.getMessage());
            UUID voucherId = UUID.fromString(sc.nextLine());
            controller.create(customerId, voucherId);
        } catch (IllegalArgumentException e) {
            throw new NotCorrectIdException();
        }

        consolePrint.printMessage(COMPLETE_CREATE_VOUCHER.getMessage());
    }

    public void findByCustomerId() {
        try {
            consolePrint.printMessage(GET_CUSTOMER_ID.getMessage());
            UUID customerId = UUID.fromString(sc.nextLine());

            List<Voucher> vouchers = controller.findByCustomerId(customerId);
            if(vouchers.isEmpty()) throw new EmptyListException(vouchers);
            else consolePrint.printList(vouchers);
        } catch (IllegalArgumentException e) {
            throw new NotCorrectIdException();
        }
    }

    public void findByVoucherId() {
        try {
            consolePrint.printMessage(GET_VOUCHER_ID.getMessage());
            UUID customerId = UUID.fromString(sc.nextLine());

            controller.findByVoucherId(customerId);
            List<Customer> customers = controller.findByVoucherId(customerId);
            if(customers.isEmpty()) throw new EmptyListException(customers);
            else consolePrint.printList(customers);
        } catch (IllegalArgumentException e) {
            throw new NotCorrectIdException();
        }
    }

    public void delete() {
        try {
            consolePrint.printMessage(GET_CUSTOMER_ID.getMessage());
            UUID customerId = UUID.fromString(sc.nextLine());
            consolePrint.printMessage(GET_VOUCHER_ID.getMessage());
            UUID voucherId = UUID.fromString(sc.nextLine());
            controller.delete(customerId, voucherId);
        } catch (IllegalArgumentException e) {
            throw new NotCorrectIdException();
        }

        consolePrint.printMessage(COMPLETE_DELETE_CUSTOMER.getMessage());
    }
}
