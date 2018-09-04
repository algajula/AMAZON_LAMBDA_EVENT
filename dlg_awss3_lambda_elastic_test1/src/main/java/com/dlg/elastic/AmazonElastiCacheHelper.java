package com.dlg.elastic;

import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.elasticache.AmazonElastiCacheClient;
import com.amazonaws.services.elasticache.model.CacheCluster;
import com.amazonaws.services.elasticache.model.CacheNode;
import com.amazonaws.services.elasticache.model.DescribeCacheClustersRequest;
import com.amazonaws.services.elasticache.model.DescribeCacheClustersResult;
import com.dlg.aws.AmazonBucketHelper;
import com.dlg.bean.PolicyDetailBean;

import redis.clients.jedis.Jedis;

public class AmazonElastiCacheHelper {
	
	
	public void getElastiCacheNodes(List<PolicyDetailBean> list)throws Exception{
		try{
			System.out.println("getElastiCacheNodes Start:::");
			AmazonBucketHelper amazonBucketHelper=new AmazonBucketHelper();
			AWSCredentials credentials = amazonBucketHelper.getAWSCredentials();
			
			AmazonElastiCacheClient client = new AmazonElastiCacheClient(credentials);
	        System.out.println("Got client, client.getEndpointPrefix() = " + client.getEndpointPrefix());
	        client.setRegion(Region.getRegion(Regions.US_EAST_1));
	     
	        System.out.println("setEndpoint passed.");
	        DescribeCacheClustersRequest dccRequest = new DescribeCacheClustersRequest();
	        dccRequest.setShowCacheNodeInfo(true);

	        System.out.println("About to call describeCacheClusters() now");
	        DescribeCacheClustersResult clusterResult = client.describeCacheClusters(dccRequest);
	        System.out.println("got clusterResult.");

	        System.out.println("CacheEngineVersions: " + client.describeCacheEngineVersions());

	        List<CacheCluster> cacheClusters = clusterResult.getCacheClusters();
	        System.out.println("About to enter for loop now, cacheClusters.size() = " + cacheClusters.size());
	        for (CacheCluster cacheCluster : cacheClusters) {
	            System.out.println("In for loop now.");
	            for (CacheNode cacheNode : cacheCluster.getCacheNodes()) {
	                System.out.println("In inner for loop now.");
	                System.out.println(cacheNode.toString());
	                String addr = cacheNode.getEndpoint().getAddress();
//					if (!addr.startsWith("hermes-dev")) continue;
	                int port = cacheNode.getEndpoint().getPort();
	                String url =  addr + ":" + port;
	                System.out.println("formed url is: " + url);

	                Jedis jedis = new Jedis(url);
	                System.out.println("Connection to server sucessfully");
	                for(PolicyDetailBean policy:list){
	                	jedis.set(policy.getPolicyId(),policy.getPolicyName());
	                }
	                for(PolicyDetailBean policy:list){
	                	System.out.println("CACHE:::"+jedis.get(policy.getPolicyId()));
	                }
	                
	                // check whether server is running or not
	                System.out.println("Server is running: " + jedis.ping());
					System.out.println("Server is running: " + jedis.clusterInfo());
	            }
	        }
			System.out.println("getElastiCacheNodes End:::");
		}catch (Exception e) {
			System.out.println("getElastiCacheNodes Error."+e.getMessage());
		}
	}
   
}