package com.prgrms.vouchermanager.domain.voucher;

import lombok.Getter;

@Getter
public enum VoucherType {
    FIXED("fixed"),
    PERCENT("precent");

    private final String type = null;

    VoucherType(String type) {
    }
}
