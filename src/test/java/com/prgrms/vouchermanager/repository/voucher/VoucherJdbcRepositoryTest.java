package com.prgrms.vouchermanager.repository.voucher;

import com.prgrms.vouchermanager.domain.voucher.FixedAmountVoucher;
import com.prgrms.vouchermanager.domain.voucher.PercentAmountVoucher;
import com.prgrms.vouchermanager.domain.voucher.Voucher;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.sql.DataSource;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;


@SpringJUnitConfig
class VoucherJdbcRepositoryTest {
    private VoucherJdbcRepository repository;
    @Autowired
    private JdbcTemplate template;
    @Autowired
    private DataSource dataSource;
    private final Voucher voucher1 = new FixedAmountVoucher(20000);
    private final Voucher voucher2 = new PercentAmountVoucher(10);

//    @Autowired
//    public VoucherJdbcRepositoryTest(JdbcTemplate template, DataSource dataSource) {
//        this.template = template;
//        this.dataSource = dataSource;
//    }

        @Configuration
    static class TestConfig {
        @Bean
        public DataSource dataSource() {
            return DataSourceBuilder.create()
                    .driverClassName("com.mysql.cj.jdbc.Driver")
                    .url("jdbc:mysql://localhost:3306/voucher_manager?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=Asia/Seoul&characterEncoding=UTF-8")
                    .username("root")
                    .password("suzzingV1999@")
                    .build();
        }

        @Bean
        public JdbcTemplate jdbcTemplate() {
            return new JdbcTemplate(dataSource());
        }
    }

    @BeforeEach
    void beforeEach() {
        this.repository = new VoucherJdbcRepository(dataSource);
        repository.create(voucher2);
    }
    @AfterEach
    void afterEach() {
        repository.delete(UUID.fromString("c80f7d69-5033-423c-b7d2-a11e7ee936dd"));
        repository.delete(UUID.fromString("a2fe49e3-900d-4632-b3c1-0b6b25dd555e"));
        repository.delete(UUID.fromString("c3ce5fc7-5673-4c80-81e2-1dac1a409489"));
        repository.delete(voucher1.getId());
        repository.delete(voucher2.getId());
    }
    @Test
    @DisplayName("create")
    void create() {
        Voucher createVoucher = repository.create(voucher1);
        Assertions.assertThat(createVoucher).isSameAs(voucher1);
    }

    @Test
    @DisplayName("list")
    void list() {
        List<Voucher> list = repository.list();
        Assertions.assertThat(list.size()).isEqualTo(4);
    }

    @Test
    @DisplayName("findById")
    void findById() {
        Voucher voucher = repository.create(voucher1);
        Voucher findVoucher = repository.findById(voucher.getId());

        Assertions.assertThat(findVoucher.getDiscount()).isEqualTo(voucher.getDiscount());
        Assertions.assertThat(findVoucher).isInstanceOf(FixedAmountVoucher.class);
    }

    @Test
    @DisplayName("updateDiscount")
    void updateDiscount() {
        repository.updateDiscount(voucher2.getId(), 20);
        Voucher updateVoucher = template.queryForObject("select * from vouchers where voucher_id=UUID_TO_BIN(?)",
                voucherRowMapper(),
                voucher2.getId().toString().getBytes());
        Assertions.assertThat(updateVoucher.getDiscount()).isEqualTo(20);
    }

    @Test
    @DisplayName("delete")
    void delete() {
        UUID deleteId = repository.delete(voucher2.getId());
        Assertions.assertThat(deleteId).isEqualTo(voucher2.getId());
    }

    private RowMapper<Voucher> voucherRowMapper() {
        return (rs, rowNum) -> {
            if(rs.getString("voucher_type").equals("fixed")) {
                return new FixedAmountVoucher(convertBytesToUUID(rs.getBytes("voucher_id")),
                        rs.getInt("discount"));
            } else {
                return new PercentAmountVoucher(convertBytesToUUID(rs.getBytes("voucher_id")),
                        rs.getInt("discount"));
            }
        };
    }

    private UUID convertBytesToUUID(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        long high = byteBuffer.getLong();
        long low = byteBuffer.getLong();
        return new UUID(high, low);
    }
}
