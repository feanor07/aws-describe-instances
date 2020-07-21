package com.dilek.alptug.awsdescribeinstances;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class InstanceDescriber implements CommandLineRunner {
    @Value("${cloud.aws.region.static}")
    private String awsRegion;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void run(String... args) {
        checkArgs(args);
        String filterKey = args[0];
        String filterValue = args[1];

        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().withRegion(awsRegion).build();
        boolean done = false;
        boolean foundInstance = false;

        logger.info("Querying instances with filter key: {} and filter value: {}", filterKey, filterValue);

        Filter filter = new Filter(filterKey).withValues(filterValue);

        DescribeInstancesRequest request = new DescribeInstancesRequest().withFilters(filter);
        while(!done) {
            DescribeInstancesResult response = ec2.describeInstances(request);

            for(Reservation reservation : response.getReservations()) {
                for(Instance instance : reservation.getInstances()) {
                    foundInstance = true;

                    logger.info("Found instance with id {} and security groups {}", instance.getInstanceId(),
                            instance.getSecurityGroups().stream().map(sg -> sg.getGroupName()).collect(Collectors.toList()).toString());
                }
            }

            request.setNextToken(response.getNextToken());

            if(response.getNextToken() == null) {
                done = true;
            }
        }

        if (!foundInstance) {
            logger.info("Could not found any instance with filter key: {} and filter value: {}", filterKey, filterValue);
        }
    }

    private void checkArgs(String[] args) {
        if (args.length < 2) {
            throw new IllegalArgumentException("Please provide filter key and value as first and second arguments");
        }
    }
}
