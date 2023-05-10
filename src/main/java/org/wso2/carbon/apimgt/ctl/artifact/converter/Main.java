package org.wso2.carbon.apimgt.ctl.artifact.converter;

import org.wso2.carbon.apimgt.ctl.artifact.converter.exception.CTLArtifactConversionException;

public class Main {
    public static void main(String[] args) {
        String srcVersion = Constants.V320;
        String targetVersion = Constants.V420;
        String srcPath = "/Users/sachini/wso2/2023/FISGLOBAL-950/artifacts/Graphql-1.0";
        String targetPath = "/Users/sachini/wso2/2023/FISGLOBAL-950/artifacts/t1";
        String format = Constants.YAML_FORMAT;

        try {
            //srcPath here should be path to extracted zip file
            ArtifactConversionManager conversionManager = new ArtifactConversionManager(srcVersion, targetVersion,
                    srcPath, targetPath, format);
            conversionManager.convert();
            System.out.println("Hello world!");
        } catch (CTLArtifactConversionException e) {
            //todo: properly handle exception
            e.printStackTrace();
        }
    }
}