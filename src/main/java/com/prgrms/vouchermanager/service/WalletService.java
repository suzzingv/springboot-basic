package com.prgrms.vouchermanager.service;

import com.prgrms.vouchermanager.domain.voucher.Voucher;
import com.prgrms.vouchermanager.domain.wallet.Wallet;
import com.prgrms.vouchermanager.repository.customer.CustomerRepository;
import com.prgrms.vouchermanager.repository.voucher.VoucherRepository;
import com.prgrms.vouchermanager.repository.wallet.WalletRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final CustomerRepository customerRepository;
    private final VoucherRepository voucherRepository;

    public WalletService(WalletRepository walletRepository, CustomerRepository customerRepository, VoucherRepository voucherRepository) {
        this.walletRepository = walletRepository;
        this.customerRepository = customerRepository;
        this.voucherRepository = voucherRepository;
    }

    public Wallet create(UUID customerId, UUID voucherId) {
        Wallet wallet = new Wallet(voucherId, customerId);
        walletRepository.create(wallet);
        return wallet;
    }

    public List<Voucher> findByCustomerId(UUID id) {
        List<Wallet> walletList = walletRepository.findByCustomerId(id);
        List<Voucher> voucherList = new ArrayList<>();

        walletList.forEach(wallet -> {
            UUID voucherId = wallet.getVoucherId();
            voucherList.add(voucherRepository.findById(voucherId));
        });
        return voucherList;
    }

    public List<com.prgrms.vouchermanager.domain.customer.Customer> findByVoucherId(UUID id) {
        List<Wallet> walletList = walletRepository.findByVoucherId(id);
        List<com.prgrms.vouchermanager.domain.customer.Customer> customerList = new ArrayList<>();

        walletList.forEach(wallet -> {
            UUID customerId = wallet.getCustomerId();
            customerList.add(customerRepository.findById(customerId));
        });
        return customerList;
    }

    public int delete(UUID customerId, UUID voucherId) {
        return walletRepository.delete(customerId, voucherId);
    }
}
