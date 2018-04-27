package de.paraplu.cryptocurrency.sync;

import java.math.BigInteger;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import de.paraplu.cryptocurrency.sync.service.SyncServiceManager;

@RestController
public class SyncController {

    public static class SyncRequest {
        @NotNull
        @Min(0)
        private BigInteger from;
        @NotNull
        private String     address;

        public String getAddress() {
            return address;
        }

        public BigInteger getFrom() {
            return from;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setFrom(BigInteger from) {
            this.from = from;
        }
    }

    @Autowired
    private SyncServiceManager syncServiceManager;
}
