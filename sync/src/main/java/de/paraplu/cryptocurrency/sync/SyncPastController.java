package de.paraplu.cryptocurrency.sync;

import java.math.BigInteger;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.paraplu.cryptocurrency.domain.mongodb.pojo.SyncStatusInfo;
import de.paraplu.cryptocurrency.sync.service.SyncServiceException;
import de.paraplu.cryptocurrency.sync.service.SyncServiceManager;

@RestController
public class SyncPastController {

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

    @RequestMapping(value = "/sync", method = RequestMethod.POST)
    public SyncStatusInfo sync(@RequestBody SyncRequest syncRequest) throws SyncServiceException {
        return syncServiceManager.sync(syncRequest.getFrom(), syncRequest.getAddress());
    }
}
