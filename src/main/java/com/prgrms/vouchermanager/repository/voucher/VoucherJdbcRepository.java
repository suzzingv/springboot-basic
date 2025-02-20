package com.prgrms.vouchermanager.repository.voucher;

import com.prgrms.vouchermanager.domain.voucher.FixedAmountVoucher;
import com.prgrms.vouchermanager.domain.voucher.PercentAmountVoucher;
import com.prgrms.vouchermanager.domain.voucher.Voucher;
import com.prgrms.vouchermanager.io.FileIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.prgrms.vouchermanager.message.QueryMessage.*;

@Slf4j
@Repository
@Profile("jdbc")
public class VoucherJdbcRepository implements VoucherRepository {

    private final JdbcTemplate jdbcTemplate;

    public VoucherJdbcRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
        fileToDb();
    }

    public Voucher create(Voucher voucher) {
        jdbcTemplate.update(INSERT_VOUCHER.getMessage(),
                voucher.getId().toString().getBytes(),
                voucher instanceof FixedAmountVoucher ? "fixed" : "percent",
                voucher.getDiscount());
        return voucher;
    }

    public List<Voucher> list() {
        return jdbcTemplate.query(LIST_VOUCHER.getMessage(), voucherRowMapper());
    }

    public Voucher findById(UUID id) {
        return jdbcTemplate.queryForObject(FIND_BY_ID_VOUCHER.getMessage(), voucherRowMapper(), id.toString().getBytes());
    }

    public void updateDiscount(UUID id, int discount) {
        jdbcTemplate.update(UPDATE_DISCOUNT_VOUCHER.getMessage(), discount, id.toString().getBytes());
    }

    public int delete(UUID id) {
        return jdbcTemplate.update(DELETE_VOUCHER.getMessage(), id.toString().getBytes());
    }

    private void fileToDb() {
        Map<UUID, Voucher> voucherMap = new HashMap<>();
        String filePath = "src/main/resources/voucher_list.csv";
        FileIO fileIO = new FileIO(filePath);
        fileIO.fileToVoucherMap(voucherMap);
        voucherMap
                .values()
                .stream()
                .toList()
                .forEach(this::create);
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
