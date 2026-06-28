package com.smtp.auth;
import org.springframework.stereotype.Component;

@Component
public class DmarcPolicy {
    public DmarcAction enforce(String domain, SpfResult spf, boolean dkimPass) {
        if (spf == SpfResult.PASS || dkimPass) return DmarcAction.ACCEPT;
        if (spf == SpfResult.FAIL && !dkimPass) return DmarcAction.REJECT;
        return DmarcAction.QUARANTINE;
    }
}
