package org.wso2.carbon.apimgt.ctl.artifact.converter.util;

import com.google.gson.JsonObject;
import org.wso2.carbon.apimgt.ctl.artifact.converter.Constants;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.EPCertificates;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.v42.V42EPCertificates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CertificatesMappingUtil {
    public static void mapCertificates(EPCertificates srcCertificates, EPCertificates targetCertificates,
                                                   String srcVersion, String targetVersion) {
        if (srcVersion.equals(Constants.V320) && targetVersion.equals(Constants.V420)) {
            targetCertificates.setCertificates(v32tov42Certificates(srcCertificates.getCertificates()));
            ((V42EPCertificates)targetCertificates).setCrts(getCrtsFromV32CertList(srcCertificates.getCertificates()));
        }
        //todo: implement other versions, if no matching conversion found(unlikely to happen), return the same list
    }

    public static List<JsonObject> v32tov42Certificates(List<JsonObject> v32certificates) {
        List<JsonObject> v42Certificates = new ArrayList<>();
        for (JsonObject v32certificate : v32certificates) {
            JsonObject v42Certificate = new JsonObject();
            String alias = v32certificate.get("alias").getAsString();
            v42Certificate.addProperty("alias", alias);
            v42Certificate.addProperty("endpoint", v32certificate.get("hostName").getAsString());
            v42Certificate.addProperty("certificate", alias + Constants.CRT_EXTENSION);
            v42Certificates.add(v42Certificate);
        }
        return v42Certificates;
    }

    public static Map<String, String> getCrtsFromV32CertList(List<JsonObject> v32certificates) {
        Map<String, String> crts = new HashMap<>();
        for (JsonObject v32certificate : v32certificates) {
            String alias = v32certificate.get("alias").getAsString();
            String crt = v32certificate.get("certificate").getAsString();
            crts.put(alias + Constants.CRT_EXTENSION, crt);
        }
        return crts;
    }
}
