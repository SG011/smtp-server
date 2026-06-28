package com.smtp.auth;
import org.springframework.stereotype.Component;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

@Component
public class SpfValidator {
    public SpfResult validate(String senderEmail, String clientIp) {
        if (senderEmail == null || clientIp == null) return SpfResult.NONE;
        try {
            String domain = senderEmail.substring(senderEmail.indexOf('@') + 1);
            var env = new Hashtable<String, String>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            var ctx = new InitialDirContext(env);
            var attrs = ctx.getAttributes("dns:/" + domain, new String[]{"TXT"});
            var txtAttr = attrs.get("TXT");
            if (txtAttr == null) return SpfResult.NONE;
            for (int i = 0; i < txtAttr.size(); i++) {
                String record = txtAttr.get(i).toString();
                if (record.startsWith("v=spf1")) {
                    return record.contains("ip4:" + clientIp) ? SpfResult.PASS : SpfResult.NONE;
                }
            }
        } catch (Exception ignored) {}
        return SpfResult.NONE;
    }
}
