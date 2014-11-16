package importer.service;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.impl.set.mutable.UnifiedSet;

@Service
public class BitbucketService {

    private Log log = LogFactory.getLog(BitbucketService.class);

    MutableSet<String> versions;
    MutableSet<String> users;

    public void initializeData(byte[] importFile) throws JsonProcessingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(importFile);

        parseVersions(rootNode.get("versions"));
        parseUsers(rootNode.get("logs"));
    }

    private void parseUsers(JsonNode jsonNode) {
        users = UnifiedSet.newSet(jsonNode.findValuesAsText("user"));
        log.debug(users);
    }

    private void parseVersions(JsonNode jsonNode) {
        versions = UnifiedSet.newSet(jsonNode.findValuesAsText("name"));
        log.debug(versions);
    }

    public List<String> listUsers() {
        return users.toSortedList();
    }
}
