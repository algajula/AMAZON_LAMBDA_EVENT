package com.dlg.aws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.dlg.bean.PolicyDetailBean;
import com.dlg.util.AWSConstants;

public class AmazonBucketHelper {


	public static void main(String[] args) throws Exception {
        System.out.println("Entered.");
        AmazonBucketHelper B=new AmazonBucketHelper();
        B.readAwsfile();
    }
	
	public List<PolicyDetailBean> readAwsfile() throws Exception {
		List<PolicyDetailBean> list  = new ArrayList<PolicyDetailBean>();
		S3Object objectPortion = null;
		try{
			AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.US_EAST_1)
                    .withCredentials(new ClasspathPropertiesFileCredentialsProvider())
                    .build();

            // Get a range of bytes from an object and print the bytes.
            GetObjectRequest rangeObjectRequest = new GetObjectRequest(AWSConstants.bucketName, "dlgproduct.csv");
            objectPortion = s3Client.getObject(rangeObjectRequest);
            System.out.println("Printing bytes retrieved.");
            list = displayTextInputStream(objectPortion.getObjectContent());
            return list;
		}catch(IOException e){
			System.out.println("IOException ERROR:::"+e.getMessage());
		}catch(AmazonServiceException e) {
			System.out.println(" AmazonServiceException ERROR:::"+e.getMessage());
        }catch(SdkClientException e) {
        	System.out.println("SdkClientException ERROR:::"+e.getMessage());
        }finally {
            if(objectPortion != null) {
                try {
					objectPortion.close();
				} catch (IOException e) {
					System.out.println("ERROR:::"+e.getMessage());
				}
            }
        }
		return list;
	}
	
	private static List<PolicyDetailBean> displayTextInputStream(InputStream input) throws IOException {
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
	
	public static AWSCredentials getAWSCredentials(){
		AWSCredentials credentials=new AWSCredentials() {
			@Override
			public String getAWSSecretKey() {
				// TODO Auto-generated method stub
				return AWSConstants.secretkey;
			}
			
			@Override
			public String getAWSAccessKeyId() {
				// TODO Auto-generated method stub
				return AWSConstants.accesskey;
			}
		};
		return credentials;
	}
}
