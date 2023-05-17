package org.wso2.carbon.apimgt.ctl.artifact.converter.util;

import com.google.gson.JsonObject;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.Certificates;
import org.wso2.carbon.apimgt.ctl.artifact.converter.model.v42.V42Certificates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CertificatesMappingUtil {
    public static void mapCertificates(Certificates srcCertificates, Certificates targetCertificates,
                                                   String srcVersion, String targetVersion) {
        if (srcVersion.equals(Constants.V320) && targetVersion.equals(Constants.V420)) {
            targetCertificates.setEndpointCertificates(v32tov42EPCertificates(srcCertificates.getEndpointCertificates()));
            ((V42Certificates)targetCertificates).setEndpointCrts(getCrtsFromV32CertList(srcCertificates.getEndpointCertificates()));

            targetCertificates.setClientCertificates(v32tov42ClientCertificates(srcCertificates.getClientCertificates()));
            ((V42Certificates)targetCertificates).setClientCrts(getCrtsFromV32CertList(srcCertificates.getClientCertificates()));
        }
        //todo: implement other versions, if no matching conversion found(unlikely to happen), return the same list
    }

    public static List<JsonObject> v32tov42EPCertificates(List<JsonObject> v32certificates) {
        List<JsonObject> v42Certificates = new ArrayList<>();
        for (JsonObject v32certificate : v32certificates) {
            JsonObject v42Certificate = new JsonObject();
            String alias = CommonUtil.readElementAsString(v32certificate, "alias");
            v42Certificate.addProperty("alias", alias);
            v42Certificate.addProperty("endpoint", CommonUtil.readElementAsString(v32certificate, "hostName"));
            v42Certificate.addProperty("certificate", alias + Constants.CRT_EXTENSION);
            v42Certificates.add(v42Certificate);
        }
        return v42Certificates;
    }

    public static List<JsonObject> v32tov42ClientCertificates(List<JsonObject> v32certificates) {
        List<JsonObject> v42Certificates = new ArrayList<>();
        for (JsonObject v32certificate : v32certificates) {
            JsonObject v42Certificate = new JsonObject();
            String alias = CommonUtil.readElementAsString(v32certificate, "alias");
            v42Certificate.addProperty("alias", alias);
            v42Certificate.addProperty("certificate", alias + Constants.CRT_EXTENSION);
            v42Certificate.addProperty("tierName", CommonUtil.readElementAsString(v32certificate, "tierName"));
            v42Certificate.add("apiIdentifier", v32certificate.get("apiIdentifier"));
            v42Certificates.add(v42Certificate);
        }
        return v42Certificates;
    }

    public static Map<String, String> getCrtsFromV32CertList(List<JsonObject> v32certificates) {
        Map<String, String> crts = new HashMap<>();
        for (JsonObject v32certificate : v32certificates) {
            String alias = CommonUtil.readElementAsString(v32certificate, "alias");;
            String crt = CommonUtil.readElementAsString(v32certificate, "certificate");
            crts.put(alias + Constants.CRT_EXTENSION, crt);
        }
        return crts;
    }
}
