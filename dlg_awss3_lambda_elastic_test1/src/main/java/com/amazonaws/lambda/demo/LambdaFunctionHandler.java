package com.amazonaws.lambda.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.dlg.bean.PolicyDetailBean;
import com.dlg.elastic.AmazonElastiCacheHelper;

public class LambdaFunctionHandler implements RequestHandler<S3Event, String> {

    private AmazonS3 s3 = AmazonS3ClientBuilder.standard()
            .withRegion(Regions.US_EAST_1)
            .withCredentials(new ClasspathPropertiesFileCredentialsProvider())
            .build();

    public LambdaFunctionHandler() {}

    // Test purpose only.
    LambdaFunctionHandler(AmazonS3 s3) {
        this.s3 = s3;
    }

    @Override
    public String handleRequest(S3Event event, Context context) {
        context.getLogger().log("Received event: " + event);

        String contentType="";
        // Get the object from the event and show its content type
        String bucket = event.getRecords().get(0).getS3().getBucket().getName();
        String key = event.getRecords().get(0).getS3().getObject().getKey();
        try {
            S3Object response = s3.getObject(new GetObjectRequest(bucket, key));
            contentType = response.getObjectMetadata().getContentType();
            context.getLogger().log("CONTENT TYPE: " + contentType);
            InputStream objectData = response.getObjectContent();
            AmazonElastiCacheHelper elastiCacheHelper=new AmazonElastiCacheHelper();
            List<PolicyDetailBean> list = displayTextInputStream(objectData);
            elastiCacheHelper.getElastiCacheNodes(list);
            return contentType;
        } catch (Exception e) {
            e.printStackTrace();
            context.getLogger().log(String.format(
                "Error getting object %s from bucket %s. Make sure they exist and"
                + " your bucket is in the same region as this function.", key, bucket));
        }
        return contentType;
    }
    
    private static List<PolicyDetailBean> displayTextInputStream(InputStream input) throws Exception {
		List<PolicyDetailBean> list  = new ArrayList<PolicyDetailBean>();
        // Read the text input stream one line at a time and display each line.
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = null;
        while ((line = reader.readLine()) != null) {
        	PolicyDetailBean policy=new PolicyDetailBean();
        	String[] feilds = line.split("\\|",-1);
        	System.out.println(line);
            policy.setPolicyId(feilds[0]);
            policy.setPolicyName(feilds[1]);
            list.add(policy);
        }
        System.out.println("displayTextInputStream End");
        return list;
    }
    
    
}