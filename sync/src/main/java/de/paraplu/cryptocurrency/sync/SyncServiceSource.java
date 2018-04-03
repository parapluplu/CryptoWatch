package de.paraplu.cryptocurrency.sync;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;

public class SyncServiceSource {

    @Autowired
    private Source source;

}
